package team.dhuacm.RigidJudge.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wujy on 15-1-9.
 */
public class Sender implements Runnable {

    private static final String QUEUE_NAME = "result_queue";
    private static Channel channel;
    private static ObjectMapper objectMapper = new ObjectMapper();
    private final static Logger logger = LoggerFactory.getLogger(Sender.class.getSimpleName());

    public Sender(Connection connection) throws IOException {
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    }

    private String serialize(Solution solution) throws JsonProcessingException {
        /*
        {
            "solution":
            {
                "id":1,
                "revision":0,
                "status":"accept_answer",
                "time_usage":1111(ms),
                "memory_usage": 232323(kb),
                "report":"html or markdown report, details about this solution. can be blank?"
            }
        }
        */
        Map<String, Object> mapSolution = new HashMap<String, Object>();
        Map<String, Object> mapInfo = new HashMap<String, Object>();
        mapInfo.put("id", solution.getId());
        mapInfo.put("revision", 0);
        mapInfo.put("status", solution.getResult().toString().toLowerCase());
        mapInfo.put("time_usage", solution.getTime());
        mapInfo.put("memory_usage", solution.getMemory());
        mapInfo.put("report", solution.getCompileInfo());
        mapSolution.put("solution", mapInfo);

        return objectMapper.writeValueAsString(mapSolution);
    }

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
                    logger.info("Send '{}'.", message);
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
