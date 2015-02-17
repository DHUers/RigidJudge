package team.dhuacm.RigidJudge.main;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wujy on 15-1-8.
 */
class RemoteController implements Runnable {

    private static final String QUEUE_NAME = "judger_proxy_queue";
    private static Channel channel;
    private static QueueingConsumer consumer;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(RemoteController.class.getSimpleName());

    public RemoteController(Connection connection) throws IOException {
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.basicQos(DataProvider.Remote_Concurrency);
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE_NAME, false, consumer);
        logger.info("Connected to the remote channel, waiting for solutions ...");
    }

    @SuppressWarnings("unchecked")
    private Solution deserialize(String message) throws IOException {
        Map<String, Map<String, Object>> maps = objectMapper.readValue(message, Map.class);

        Map<String, Object> mapSolution = maps.get("solution");
        String source = (String) mapSolution.get("source");
        String platform = (String) mapSolution.get("platform");
        if (platform.equals("c++")) {
            platform = "cpp";
        }
        int solutionId = (Integer) mapSolution.get("id");
        int problemId = (Integer) mapSolution.get("problem_id");

        List<LinkedHashMap<String, Object>> listProblems = (List<LinkedHashMap<String, Object>>) maps.get("problems");
        Map<String, Object> judgeData = (Map<String, Object>) listProblems.get(0).get("judge_data");
        String[] vendor = ((String) judgeData.get("vendor")).split(",");

        Problem problem = new RemoteProblem(problemId, OJ.valueOf(vendor[0].toUpperCase()), vendor[1]);
        return new Solution(solutionId, problem, source, Language.valueOf(platform.toUpperCase()));
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        try {
            while (true) {
                // Fetch solution;
                final Delivery delivery = consumer.nextDelivery();
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
