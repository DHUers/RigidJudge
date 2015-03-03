package team.dhuacm.RigidJudge.main;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.DataProvider;

import java.io.IOException;

/**
 * Created by wujy on 15-1-7.
 */
public class RigidJudge {
    private static Connection connection;
    private static final Logger logger = LoggerFactory.getLogger(RigidJudge.class.getSimpleName());

    private static void initialize() throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(DataProvider.RABBITMQ_HOST);
        factory.setPort(DataProvider.RABBITMQ_PORT);
        factory.setUsername("judger");
        factory.setPassword(DataProvider.RABBITMQ_PASSWORD);
        connection = factory.newConnection();
        logger.info("Initialization finished.");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run () {
                try {
                    connection.close();
                } catch (IOException e) {
                    logger.info("Connection closed.");
                }
            }
        });
    }

    public static void main(String[] args) throws IOException {
        initialize();
        new Thread(new LocalController(connection), "Local").start();
        new Thread(new RemoteController(connection), "Remote").start();
        new Thread(new Sender(connection), "Sender").start();
    }
}
