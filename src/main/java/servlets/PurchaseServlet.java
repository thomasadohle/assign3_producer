package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import models.Purchase;
import models.PurchaseItem;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;


public class PurchaseServlet extends HttpServlet {

    private boolean requestValid = false;
    private boolean messageWriteSuccessful = false;
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;

    public void init() throws ServletException {
        // Initialization
        factory = new ConnectionFactory();
        try{
            connection = factory.newConnection();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void destroy() {
        if (connection != null){
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (channel != null){
            try {
                channel.close();
            } catch (IOException| TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String responseMessage;
        Purchase purchase = null;
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonString = new JSONObject();
        try{
            purchase = processRequest(request);
        } catch (Exception e){
            this.requestValid = false;
        }

        if (! this.requestValid || purchase == null){
            //Invalid request. Return 400.
            response.setStatus(400);
            responseMessage = jsonString.put("message", "Invalid request").toString();
            out.println(responseMessage);
            return;
        }
        try{
            writeMessage(purchase);
        } catch (Exception e){
            e.printStackTrace();
        }

        if (! this.messageWriteSuccessful){
            //Persistence failed. Return 500.
            response.setStatus(500);
            responseMessage = jsonString.put("message", "Persistence failed").toString();
            out.println(responseMessage);
            return;
        }
        // Everything worked
        responseMessage = jsonString.put("message", "Valid request!").toString();
        out.println(responseMessage);
    }

    private void writeMessage(Purchase p) {
        ObjectMapper mapper = new ObjectMapper();
        try{
            channel = connection.createChannel();
            channel.exchangeDeclare("purchase", "fanout");
            channel.basicPublish("purchase","",null,mapper.writeValueAsBytes(p));
            this.messageWriteSuccessful = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Purchase processRequest(HttpServletRequest request) throws IOException {
        // Process the request. If it's valid, set this.requestValid to true
        Map<String, String> urlParams = this.processUrl(request);
        if (! urlParamsValid(urlParams)){
            this.requestValid = false;
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        Purchase purchase;
        try {
            purchase = mapper.readValue(request.getReader(), Purchase.class);
            purchase.setDate(urlParams.get("date"));
            purchase.setCustomerId(Integer.parseInt(urlParams.get("customerId")));
            purchase.setStoreId(Integer.parseInt(urlParams.get("storeId")));
        } catch(Exception e){
            this.requestValid = false;
            return null;
        }
        this.requestValid = true;
        return purchase;
    }

    private Map<String, String> processUrl(HttpServletRequest request){
        // Process/validate URL
        String path = request.getPathInfo();
        String[] paramArr = path.split("/");
        Map<String,String> urlParams = new HashMap<>();
        urlParams.put("storeId", paramArr[1]);
        urlParams.put("customerTitle", paramArr[2]);
        urlParams.put("customerId", paramArr[3]);
        urlParams.put("dateTitle", paramArr[4]);
        urlParams.put("date", paramArr[5]);
        return urlParams;
    }

    private boolean urlParamsValid(Map<String, String> urlParams){
        try{
            int storeId = Integer.parseInt(urlParams.get("storeId"));
        } catch (NumberFormatException e){
            return false;
        }
        // Must equal "customerTitle"
        String customerTitle = urlParams.get("customerTitle");
        if (! customerTitle.equals("customer")){
            return false;
        }
        // Must be an Integer
        try{
            Integer.parseInt(urlParams.get("customerId"));
        } catch (NumberFormatException e){
            return false;
        }
        // Must equal "date"
        String dateTitle = urlParams.get("dateTitle");
        if (! dateTitle.equals("date")){
            return false;
        }
        // Must be a valid date in yyyyMMdd format
        String date = urlParams.get("date");
        try{
            new SimpleDateFormat("yyyyDDmm").parse(date);
        } catch(ParseException e){
            return false;
        }
        return true;
    }

}
