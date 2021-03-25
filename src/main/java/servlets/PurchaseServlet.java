package servlets;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class PurchaseServlet extends HttpServlet {

    private boolean requestValid = false;

    public void init() throws ServletException {
        // Initialization
    }

    public void destroy() {
        //Tear Down
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String responseMessage;
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonString = new JSONObject();

        if (! this.requestValid){
            //Invalid request. Return 400.
            response.setStatus(400);
            responseMessage = jsonString.put("message", "Invalid request").toString();
            out.println(responseMessage);
            return;
        }
        // Valid request
        responseMessage = jsonString.put("message", "Valid request!").toString();
        out.println(responseMessage);
    }

    private void processRequest(HttpServletRequest request){
        // Process the request. If it's valid, set this.requestValid to true

    }

//    private Map<String, String> processUrl(HttpServletRequest request){
//        // Process/validate URL
//    }

    private boolean urlParamsValid(Map<String, String> urlParams){
        return false;
    }

}
