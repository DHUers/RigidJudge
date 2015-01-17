package team.dhuacm.RigidJudge.main;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import team.dhuacm.RigidJudge.config.DataProvider;

import java.io.IOException;

/**
 * Created by wujy on 15-1-7.
 */
public class RigidJudge {
    private static ConnectionFactory factory;
    private static Connection connection;

    private static void initialize() throws IOException {
        factory = new ConnectionFactory();
        factory.setHost(DataProvider.RabbitMQ_Host);
        factory.setPort(DataProvider.RabbitMQ_Port);
        connection = factory.newConnection();
        System.out.println("Initialization finished.");
    }

    public static void main(String[] args) throws IOException {
        initialize();
        new Thread(new LocalController(connection)).start();
        new Thread(new RemoteController(connection)).start();
        new Thread(new Sender(connection)).start();
    }
}
