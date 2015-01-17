package team.dhuacm.RigidJudge.main;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.config.Language;
import team.dhuacm.RigidJudge.config.OJ;
import team.dhuacm.RigidJudge.model.Problem;
import team.dhuacm.RigidJudge.model.RemoteProblem;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadFactory;

/**
 * Created by wujy on 15-1-8.
 */
public class RemoteController implements Runnable {

    private static final String QUEUE_NAME = "judger_proxy_queue";
    private static Channel channel;
    private static QueueingConsumer consumer;

    public RemoteController(Connection connection) throws IOException {
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.basicQos(3);
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE_NAME, false, consumer);
        System.out.println("INFO: [Remote] Connected to the remote channel, waiting for solutions ...");
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Fetch solution;
                final QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                final String message = new String(delivery.getBody());

                System.out.println(" [Remote] Received '" + message + "'");
                new Thread() {
                    public void run() {
                        String info = Thread.currentThread().getName();
                        try {
                            // RemoteResolver;
                            ObjectMapper objectMapper = new ObjectMapper();
                            Map<String, Map<String, Object>> maps = objectMapper.readValue(message, Map.class);
                            Map<String, Object> mapSolution = maps.get("solution");
                            List<LinkedHashMap<String, Object>> listProblems = (List<LinkedHashMap<String, Object>>) maps.get("problems");

                            Map<String, Object> judge_data = (Map<String, Object>) listProblems.get(0).get("judge_data");
                            String[] vendor = ((String) judge_data.get("vendor")).split(",");
                            String source = (String) mapSolution.get("source");
                            String platform = (String) mapSolution.get("platform");
                            if (platform.equals("c++")) {
                                platform = "cpp";
                            }
                            int solutionId = (Integer) mapSolution.get("id");
                            int problemId = (Integer) mapSolution.get("problem_id");

                            Problem problem = new RemoteProblem(problemId, OJ.valueOf(vendor[0].toUpperCase()), vendor[1]);
                            Solution solution = new Solution(solutionId, problem, source, Language.valueOf(platform.toUpperCase()));

                            new Thread(new RemoteResolver(solution)).run();  // TODO: change to Coroutines later
                            /*
                            Scheduler scheduler = new Scheduler();
                            DailiTask task = new DailiTask(scheduler) {
                                @Override
                                public void execute() throws Pausable, Exception {

                                }
                            };
                            scheduler.callSoon(task);
                            scheduler.loop();
                            */

                            System.out.println(info + " - result is " + solution.getResult());

                            DataProvider.JudgedSolutionQueue.put(solution);
                            System.out.println(info + " - send to finished queue success!");

                            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        } catch (JsonMappingException e) {
                            e.printStackTrace();
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
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
