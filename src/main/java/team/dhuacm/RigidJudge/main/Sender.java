package team.dhuacm.RigidJudge.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wujy on 15-1-9.
 */
class Sender implements Runnable {

    private static final String QUEUE_NAME = "result_queue";
    private static Channel channel;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(Sender.class.getSimpleName());

    public Sender(Connection connection) throws IOException {
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    }


    /**
     * @param solution the Solution need to be serialized
     * @return JSON string
     * @throws JsonProcessingException
     * example: https://github.com/DHUers/RigidJudge/wiki/JSON-Prototype
     */
    private String serialize(Solution solution) throws JsonProcessingException {
        Map<String, Object> mapSolution = null;
        try {
            mapSolution = new HashMap<String, Object>();
            Map<String, Object> mapInfo = new HashMap<String, Object>();
            mapInfo.put("id", solution.getId());
            mapInfo.put("revision", 0);
            if (solution.getResult().equals(Result.Accepted)) {
                mapInfo.put("status", "accepted_answer");
            } else {
                mapInfo.put("status", solution.getResult().toString().toLowerCase());
            }
            mapInfo.put("time_usage", solution.getTime());
            mapInfo.put("memory_usage", solution.getMemory());
            mapInfo.put("report", solution.getCompileInfo() + "\n" + solution.getExecuteInfo() + "\n" + solution.getCompareInfo());
            mapSolution.put("solution", mapInfo);
        } catch (Exception e) {
            logger.error("Serialize to JSON error!", e);
        }
        return objectMapper.writeValueAsString(mapSolution);
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        try {
            while (true) {
                if (DataProvider.JudgedSolutionQueue.isEmpty()) {
                    Thread.sleep(1000);
                } else {
                    Solution solution = DataProvider.JudgedSolutionQueue.take();

                    String message = serialize(solution);

                    channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                    logger.info("Sent '{}'.", message);
                }
            }
        } catch (IOException e) {
            logger.error(null, e);
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
