package team.dhuacm.RigidJudge.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.config.Language;
import team.dhuacm.RigidJudge.config.OJ;
import team.dhuacm.RigidJudge.model.LocalProblem;
import team.dhuacm.RigidJudge.model.Problem;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wujy on 15-1-8.
 */
public class LocalController implements Runnable {

    private static final String QUEUE_NAME = "judger_local_queue";
    private static Channel channel;
    private static QueueingConsumer consumer;
    private static ObjectMapper objectMapper = new ObjectMapper();

    public LocalController(Connection connection) throws IOException {
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.basicQos(1);
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE_NAME, false, consumer);
        System.out.println("INFO: [Local] Connected to the local channel, waiting for solutions ...");
    }

    private Solution deserialize(String message) throws IOException {  // TODO
        Map<String, Map<String, Object>> maps = objectMapper.readValue(message, Map.class);
        Map<String, Object> mapSolution = maps.get("solution");
        List<LinkedHashMap<String, Object>> listProblems = (List<LinkedHashMap<String, Object>>) maps.get("problems");

        Map<String, Object> judge_data = (Map<String, Object>) listProblems.get(0).get("judge_data");
        String source = (String) mapSolution.get("source");
        String platform = (String) mapSolution.get("platform");
        if (platform.equals("c++")) {
            platform = "cpp";
        }
        int solutionId = (Integer) mapSolution.get("id");
        int problemId = (Integer) mapSolution.get("problem_id");

        Problem problem = new LocalProblem(problemId, "full_text", null, null, null, null);
        return new Solution(solutionId, problem, source, Language.valueOf(platform.toUpperCase()));
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Fetch solution;
                final QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                final String message = new String(delivery.getBody());

                System.out.println(" [Local] Received '" + message + "'");
                new Thread() {
                    public void run() {
                        String info = Thread.currentThread().getName();
                        try {
                            Solution solution = deserialize(message);

                            new Thread(new LocalResolver(new Solution())).run();  // TODO
                            System.out.println(info + " - result is " + solution.getResult());

                            DataProvider.JudgedSolutionQueue.put(solution);
                            System.out.println(info + " - send to finished queue success!");

                            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
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
