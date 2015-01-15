package team.dhuacm.RigidJudge.main;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.IOException;

/**
 * Created by wujy on 15-1-9.
 */
public class Sender implements Runnable {

    private static final String QUEUE_NAME = "result_queue";
    private static Channel channel;

    public Sender(Connection connection) throws IOException {
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (DataProvider.JudgedSolutionQueue.isEmpty()) {
                    Thread.sleep(1000);
                } else {
                    Solution solution = DataProvider.JudgedSolutionQueue.take();
                    // TODO
                    String message = "Hello World!";
                    channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                    System.out.println(" [x] Sent '" + message + "'");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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
