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
import team.dhuacm.RigidJudge.config.Result;
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

    private static final String QUEUE_NAME = "judger_queue";
    private static Channel channel;
    private static QueueingConsumer consumer;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(LocalController.class.getSimpleName());
    private static Solution solution;
    
    public LocalController(Connection connection) throws IOException {
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.basicQos(1);
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE_NAME, false, consumer);
        logger.info("Connected to the local channel, waiting for solutions ...");
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
            solution.setId((Integer) mapSolution.get("id"));
            solution.setCode((String) mapSolution.get("source"));
            String platform = (String) mapSolution.get("platform");
            if (platform.equalsIgnoreCase("c++")) {
                platform = "cpp";
            }
            solution.setLanguage(Language.valueOf(platform.toUpperCase()));
            int problemId = (Integer) mapSolution.get("problem_id");

            List<LinkedHashMap<String, Object>> listProblems = (List<LinkedHashMap<String, Object>>) maps.get("problems");
            String judgeType = (String) listProblems.get(0).get("judge_type");
            Map<String, Object> mapJudgeData = (Map<String, Object>) listProblems.get(0).get("judge_data");
            String inputFileUrl = (String) mapJudgeData.get("input_file");
            String outputFileUrl = (String) mapJudgeData.get("output_file");
            String limitType = (Boolean) mapJudgeData.get("per_case_limit") ? "per" : "all";

            Map<String, Object> mapJudgeLimits = (Map<String, Object>) mapJudgeData.get("judge_limits");  // TODO: test
            Map<Language, Integer> timeLimit = new HashMap<Language, Integer>();
            Map<Language, Integer> memoryLimit = new HashMap<Language, Integer>();
            for (Map.Entry<String, Object> entry : mapJudgeLimits.entrySet()) {
                Language language;
                if (entry.getKey().equalsIgnoreCase("c++")) {
                    language = Language.CPP;
                } else {
                    language = Language.valueOf(entry.getKey().toUpperCase());
                }
                Map<String, Object> limits = (Map<String, Object>) entry.getValue();
                timeLimit.put(language, Integer.parseInt((String) limits.get("time")));
                memoryLimit.put(language, Integer.parseInt((String) limits.get("memory")));
            }

            Problem problem = null;
            if (judgeType.equals("full_text")) {
                problem = new LocalProblem(problemId, inputFileUrl, outputFileUrl, limitType, timeLimit, memoryLimit);
            } else if (judgeType.equals("program_comparison")) {
                String judgerProgramCode = (String) mapJudgeData.get("judger_program_source");
                String judgerProgramLanguage = (String) mapJudgeData.get("judger_program_platform");
                problem = new LocalSpecialProblem(problemId, inputFileUrl, outputFileUrl, limitType, timeLimit, memoryLimit, judgerProgramCode, Language.valueOf(judgerProgramLanguage.toUpperCase()));
            }
            solution.setProblem(problem);
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
                                new LocalResolver(solution).handle();
                            } else {
                                solution.setResult(Result.Judge_Error);
                            }
                            logger.info("Result is '{}'.", solution.getResult());
                            if (solution.getId() != 0) {
                                DataProvider.JUDGED_SOLUTION_QUEUE.put(solution);
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
