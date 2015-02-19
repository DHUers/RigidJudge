package team.dhuacm.RigidJudge.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.config.Language;
import team.dhuacm.RigidJudge.local.LocalResolver;
import team.dhuacm.RigidJudge.model.LocalProblem;
import team.dhuacm.RigidJudge.model.LocalSpecialProblem;
import team.dhuacm.RigidJudge.model.Problem;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wujy on 15-1-8.
 */
class LocalController implements Runnable {

    private static final String QUEUE_NAME = "judger_local_queue";
    private static Channel channel;
    private static QueueingConsumer consumer;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(LocalController.class.getSimpleName());

    public LocalController(Connection connection) throws IOException {
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.basicQos(1);
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE_NAME, false, consumer);
        logger.info("Connected to the local channel, waiting for solutions ...");
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
        String judgeType = (String) listProblems.get(0).get("judge_type");
        Map<String, Object> judgeData = (Map<String, Object>) listProblems.get(0).get("judge_data");
        String inputFileUrl = (String) judgeData.get("input_file_url");
        String outputFileUrl = (String) judgeData.get("output_file_url");
        String limitType = (String) judgeData.get("limit_type");
        Map<Language, Integer> timeLimit = (Map<Language, Integer>) judgeData.get("time_limit");  // TODO: default & require test
        Map<Language, Integer> memoryLimit = (Map<Language, Integer>) judgeData.get("memory_limit");  // TODO: default & require test

        Problem problem = null;
        if (judgeType.equals("full_text")) {
            problem = new LocalProblem(problemId, inputFileUrl, outputFileUrl, limitType, timeLimit, memoryLimit);
        } else if (judgeType.equals("program_comparison")) {
            String judgerProgramCode = (String) judgeData.get("judger_program_source");
            String judgerProgramLanguage = (String) judgeData.get("judger_program_platform");
            problem = new LocalSpecialProblem(problemId, inputFileUrl, outputFileUrl, limitType, timeLimit, memoryLimit, judgerProgramCode, Language.valueOf(judgerProgramLanguage.toUpperCase()));
        }
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

                            new LocalResolver(solution).handle();
                            logger.info("Result is '{}'.", solution.getResult());

                            DataProvider.JudgedSolutionQueue.put(solution);
                            logger.info("Sent to finished queue successfully!");

                            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        } catch (InterruptedException e) {
                            logger.error(null, e);
                        } catch (IOException e) {
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
