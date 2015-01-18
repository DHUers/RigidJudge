package team.dhuacm.RigidJudge.main;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.config.Language;
import team.dhuacm.RigidJudge.config.OJ;
import team.dhuacm.RigidJudge.model.Problem;
import team.dhuacm.RigidJudge.model.RemoteProblem;
import team.dhuacm.RigidJudge.model.Solution;
import team.dhuacm.RigidJudge.remote.RemoteResolver;

import java.io.IOException;
import java.util.*;

/**
 * Created by wujy on 15-1-8.
 */
public class RemoteController implements Runnable {

    private static final String QUEUE_NAME = "judger_proxy_queue";
    private static Channel channel;
    private static QueueingConsumer consumer;
    private static ObjectMapper objectMapper = new ObjectMapper();
    private final static Logger logger = LoggerFactory.getLogger(RemoteController.class.getSimpleName());

    public RemoteController(Connection connection) throws IOException {
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.basicQos(DataProvider.Remote_Concurrency);
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE_NAME, false, consumer);
        logger.info("Connected to the remote channel, waiting for solutions ...");
    }

    private Solution deserialize(String message) throws IOException {
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
        return new Solution(solutionId, problem, source, Language.valueOf(platform.toUpperCase()));
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Fetch solution;
                final QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                final String message = new String(delivery.getBody());

                logger.info("Received '{}'.", message);

                new Thread() {
                    public void run() {
                        try {
                            Solution solution = deserialize(message);

                            new RemoteResolver(solution).handle();
                            logger.info("Result is '{}'.", solution.getResult());

                            DataProvider.JudgedSolutionQueue.put(solution);
                            logger.info("Sent to finished queue successfully!");

                            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        } catch (JsonMappingException e) {
                            logger.error(null, e);
                        } catch (JsonParseException e) {
                            logger.error(null, e);
                        } catch (IOException e) {
                            logger.error(null, e);
                        } catch (InterruptedException e) {
                            logger.error(null, e);
                        }
                    }
                }.start();
            }
        } catch (InterruptedException e) {
            logger.error(null, e);
        } finally {
            if (null != channel) {
                try {
                    channel.close();
                } catch (IOException e) {
                    logger.error(null, e);
                }
            }
        }
    }
}
