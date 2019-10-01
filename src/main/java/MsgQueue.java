import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MsgQueue {
    private final static String queue_name = "reqest_log";

    private static MsgQueue msgQueue;

    private Connection conn;
    private Channel channel;

    public static MsgQueue current() {
        if (msgQueue == null) {
            synchronized (MsgQueue.class) {
                if (msgQueue == null) {
                    msgQueue = new MsgQueue();
                }
            }
        }
        return msgQueue;
    }

    MsgQueue() {
        try {
            getChannel();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void publish(String msg) {
        try {
            channel.basicPublish("", queue_name, null, msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("publish msg error: " + msg);
        }
    }

    private Channel getChannel() throws IOException, TimeoutException {
        if (channel != null) {
            return channel;
        }
        conn = getConnection();
        channel = conn.createChannel();
        channel.queueDeclare(queue_name, false, false, false, null);
        return channel;
    }

    public void close() throws IOException, TimeoutException {
        channel.close();
        conn.close();
    }

    private Connection getConnection() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("144.34.251.231");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("jkjkjk");
        Connection conn = connectionFactory.newConnection();
        return conn;
    }
}
