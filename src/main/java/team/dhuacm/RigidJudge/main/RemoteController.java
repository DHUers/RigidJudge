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
import team.dhuacm.RigidJudge.config.Result;
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
    private static Solution solution;
    
    public RemoteController(Connection connection) throws IOException {
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.basicQos(DataProvider.Remote_Concurrency);
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE_NAME, false, consumer);
        logger.info("Connected to the remote channel, waiting for solutions ...");
    }

    /**
     * @param message JSON string
     * @return whether parsed JSON successfully
     * example: https://github.com/DHUers/RigidJudge/wiki/JSON-Prototype
     */
    @SuppressWarnings("unchecked")
    private boolean deserialize(String message) {
        solution = new Solution(0, null, null, null);
        try {
            Map<String, Map<String, Object>> maps = objectMapper.readValue(message, Map.class);

            Map<String, Object> mapSolution = maps.get("solution");
            solution.setCode((String) mapSolution.get("source"));
            String platform = (String) mapSolution.get("platform");
            if (platform.equalsIgnoreCase("c++")) {
                platform = "cpp";
            }
            solution.setLanguage(Language.valueOf(platform.toUpperCase()));
            solution.setId((Integer) mapSolution.get("id"));
            int problemId = (Integer) mapSolution.get("problem_id");

            List<LinkedHashMap<String, Object>> listProblems = (List<LinkedHashMap<String, Object>>) maps.get("problems");
            Map<String, Object> judgeData = (Map<String, Object>) listProblems.get(0).get("judge_data");
            String[] vendor = ((String) judgeData.get("vendor")).split(",");

            solution.setProblem(new RemoteProblem(problemId, OJ.valueOf(vendor[0].toUpperCase()), vendor[1]));
        } catch (Exception e) {
            logger.error("Parse JSON error!", e);
            return false;
        }
        return true;
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
                            if (deserialize(message)) {
                                new RemoteResolver(solution).handle();
                            } else {
                                solution.setResult(Result.Judge_Error);
                            }
                            logger.info("Result is '{}'.", solution.getResult());
                            if (solution.getId() != 0) {
                                DataProvider.JudgedSolutionQueue.put(solution);
                                logger.debug("Sent to result queue successfully!");
                            }
                            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                            logger.debug("Sent ACK message successfully!");
                        } catch (InterruptedException e) {
                            logger.error("Send to result queue failed!", e);
                        } catch (IOException e) {
                            logger.error("Send ACK message failed!", e);
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
