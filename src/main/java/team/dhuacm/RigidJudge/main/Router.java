package team.dhuacm.RigidJudge.main;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import team.dhuacm.RigidJudge.config.DataProvider;

import java.io.IOException;

/**
 * Created by wujy on 15-1-7.
 */
public class Router implements Runnable {

    private static final String TASK_QUEUE_NAME = "unjudged_queue";

    @Override
    public void run() {
        try {
            // Connect RMQ;
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(DataProvider.RabbitMQ_Host);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            System.out.println("INFO: Connected to RabbitMQ, waiting for solutions.");

            channel.basicQos(3);

            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(TASK_QUEUE_NAME, false, consumer);

            while (true) {
                // Fetch solution;
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                String message = new String(delivery.getBody());

                System.out.println(" [x] Received '" + message + "'");

                // Dispatch;
                // TODO

                // Send result;
                System.out.println(" [x] Done");

                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
