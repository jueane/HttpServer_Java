import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class TestRabbitMQ {

    final static String QUEUE_NAME = "testqueue";

    public void test1() throws IOException, TimeoutException {
        System.out.println("________test1");
        Connection conn = TestRabbitMQ.getConnection();
        Channel channel = conn.createChannel();
        //创建队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        //发送消息
        for (int i = 0; i < 1; i++) {
            channel.basicPublish("", QUEUE_NAME, null, ("hi" + i).getBytes());
        }

        channel.close();
        conn.close();
        System.out.println("________test1 end");
    }

    public void consumeMsgSingle() throws IOException, TimeoutException, InterruptedException {
        Connection conn = getConnection();
        Channel channel = conn.createChannel();
        String queuename = "reqest_log";

        List<String> cnList=new ArrayList<>();
        List<String> reqList=new ArrayList<>();
        List<String> respListt=new ArrayList<>();

        GetResponse resp = null;
        while (true) {
            resp = channel.basicGet(queuename, true);
            if (resp == null) {
                break;
            }
            String  msg=new String(resp.getBody());
            if(msg.contains("connect"))
                cnList.add(msg);
            if(msg.contains("req /"))
                reqList.add(msg);
            if(msg.contains("resp"))
                respListt.add(msg);

//            System.out.println(Thread.currentThread().getId() + "        " + " " + msg);
        }

        if (channel.isOpen()) {
            channel.close();
        }
        conn.close();

        //分析
        System.out.println("conn :"+cnList.size());
        System.out.println("req:"+reqList.size());
        System.out.println("resp :"+respListt.size());



    }

    public static void main(String[] args) throws InterruptedException, TimeoutException, IOException {
        new TestRabbitMQ().consumeMsgConcurrent();
    }

    public void consumeMsgConcurrent() throws IOException, TimeoutException, InterruptedException {
        Connection conn = getConnection();
        Channel channel = conn.createChannel();
        String queuename = "reqest_log";

        channel.queueDeclare(queuename, false, false, false, null);

        AtomicInteger a = new AtomicInteger(3);
        AtomicBoolean stop = new AtomicBoolean();

//        AtomicReference<String> tag=new AtomicReference<>();
        ConcurrentSkipListSet cnList=new ConcurrentSkipListSet();
        ConcurrentSkipListSet reqList=new ConcurrentSkipListSet();
        ConcurrentSkipListSet respListt=new ConcurrentSkipListSet();


        DeliverCallback deliverCallback = (consumerTag, dilivery) -> {
            String msg = new String(dilivery.getBody(), "UTF-8");
            System.out.println(Thread.currentThread().getId() + "        " + "recv: " + msg);

            if(msg.contains("connect"))
                cnList.add(msg);
            if(msg.contains("req /"))
                reqList.add(msg);
            if(msg.contains("resp"))
                respListt.add(msg);

        };


        String tag1 = channel.basicConsume(queuename, true, deliverCallback, consumerTag -> {
        });
//        tag.set(tag1);

        Thread.sleep(2000);
        //分析
        System.out.println("conn :"+cnList.size());
        System.out.println("req:"+reqList.size());
        System.out.println("resp :"+respListt.size());

        if (channel.isOpen()) {
            channel.close();
        }
        conn.close();
    }

    //多线程消费
    public void consumer2() throws IOException, TimeoutException, InterruptedException {
        System.out.println("_________test3");

        ExecutorService es = Executors.newFixedThreadPool(1);

        Connection conn = getConnection();

        Channel channel = conn.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        boolean autoAck = true;
        channel.basicConsume(QUEUE_NAME, autoAck, "myConsumerTag",
                new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag,
                                               Envelope envelope,
                                               AMQP.BasicProperties properties,
                                               byte[] body)
                            throws IOException {
                        String routingKey = envelope.getRoutingKey();
                        String contentType = properties.getContentType();
                        long deliveryTag = envelope.getDeliveryTag();
                        // (process the message components here ...)
                        String msg = new String(body);

                        long tid = Thread.currentThread().getId();
                        System.out.println("tid: " + tid + " , recv: " + msg);


                        try {
                            long a = System.currentTimeMillis() % 1000;
                            Thread.sleep(a);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
//                        channel.basicAck(deliveryTag, false);
                    }
                });
        Thread.sleep(5000);
        long tid = Thread.currentThread().getId();
        System.out.println("___tid: " + tid);
        System.out.println("_________test3__end");
        channel.close();
        conn.close();
    }

    public static Connection getConnection() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("144.34.251.231");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("jkjkjk");
        Connection conn = connectionFactory.newConnection();
        return conn;
    }

    public static Connection getConnection(ExecutorService es) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("144.34.251.231");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("jkjkjk");
        Connection conn = connectionFactory.newConnection(es);
        return conn;
    }
}
