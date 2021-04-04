import com.mysql.cj.jdbc.exceptions.ConnectionFeatureNotAvailableException;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class main {

    private final static String QUEUE_NAME = "hello";

    public static void main (String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        //factory.setHost("localhost");
        try(Connection cnxn = factory.newConnection();
        Channel channel = cnxn.createChannel()){
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hey bitch";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
            System.out.println("Message sent: " + message);
        }

    }

}
