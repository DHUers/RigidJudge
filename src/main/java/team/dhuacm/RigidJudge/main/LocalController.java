package team.dhuacm.RigidJudge.main;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.IOException;

/**
 * Created by wujy on 15-1-8.
 */
public class LocalController implements Runnable {

    private static final String QUEUE_NAME = "local_unjudged_queue";
    private static Channel channel;
    private static QueueingConsumer consumer;

    public LocalController(Connection connection) throws IOException {
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.basicQos(3);
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE_NAME, false, consumer);
        System.out.println("INFO: [Local] Connected to the local channel, waiting for solutions ...");
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Fetch solution;
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                String message = new String(delivery.getBody());

                System.out.println(" [x] Received '" + message + "'");

                // LocalResolver;
                new Thread(new LocalResolver(new Solution())).run();  // TODO

                System.out.println(" [x] Done");

                // Send result;
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                DataProvider.JudgedSolutionQueue.put(null);  // TODO
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != channel) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
