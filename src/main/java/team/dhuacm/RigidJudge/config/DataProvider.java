package team.dhuacm.RigidJudge.config;

import team.dhuacm.RigidJudge.model.Solution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by wujy on 15-1-7.
 */
public class DataProvider {

    // Common configurations
    public static String RabbitMQ_Host;
    public static int RabbitMQ_Port;
    public final static HashMap<OJ, OJProperty> OJs = new HashMap<OJ, OJProperty>();

    // Local judge configurations
    // TODO

    // Remote judge configurations
    public final static List<Integer> Remote_QueryInterval = new ArrayList<Integer>();
    public static int Remote_RetryTimes = 0;
    public static int Remote_SocketTimeout = 0;
    public static int Remote_ConnectionTimeout = 0;
    public final static HashMap<OJ, BlockingQueue<OJAccount>> Remote_OJAccounts = new HashMap<OJ, BlockingQueue<OJAccount>>();

    // Common data structure
    public final static LinkedBlockingQueue<Solution> JudgedSolutionQueue = new LinkedBlockingQueue<Solution>();

    static {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream("configs/Config.properties"));
        } catch (FileNotFoundException e1) {
            System.out.println();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        RabbitMQ_Host = p.getProperty("RabbitMQ_Host");
        RabbitMQ_Port = Integer.parseInt(p.getProperty("RabbitMQ_Port"));

        // TODO: load local judge configurations

        Remote_RetryTimes = Integer.parseInt(p.getProperty("Remote_RetryTimes"));
        Remote_SocketTimeout = Integer.parseInt(p.getProperty("Remote_SocketTimeout"));
        Remote_ConnectionTimeout = Integer.parseInt(p.getProperty("Remote_ConnectionTimeout"));
        for (String str : p.getProperty("Remote_QueryInterval").split(",")) {
            Remote_QueryInterval.add(Integer.parseInt(str));
        }

        System.out.printf("INFO: RabbitMQ Server: %s\n", RabbitMQ_Host);
        System.out.printf("INFO: [Local]\n");
        System.out.printf("INFO: [Remote] Retry Num: %s, Socket Timeout: %s, Connection Timeout: %s\n", Remote_RetryTimes, Remote_SocketTimeout, Remote_ConnectionTimeout);
        System.out.printf("INFO:          Query Time: %s\n", Remote_QueryInterval);

        // Remote OJ Accounts
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("configs/remote/OJAccounts.properties"));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;
                else {
                    String[] strs = line.split(",");
                    String ojName = strs[0].trim().toUpperCase();
                    OJ oj = OJ.valueOf(ojName);
                    OJAccount account = new OJAccount(strs[1].trim(), strs[2].trim());
                    BlockingQueue<OJAccount> queue = Remote_OJAccounts.get(oj);
                    if (null == queue) {
                        queue = new LinkedBlockingQueue<OJAccount>();
                        Remote_OJAccounts.put(oj, queue);
                    }
                    queue.put(account);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Fatal Error: Cannot find remote/OJAccounts.properties File!");
            System.exit(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
        System.out.println("INFO: Init remote/OJAccounts.properties Config OK!");
        for (OJ oj : Remote_OJAccounts.keySet()) {
            System.out.println(oj + "\t" + Remote_OJAccounts.get(oj).size());
        }

        // OJs
        File file = new File("configs/remote/OJProperty");
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (f.getName().endsWith(".properties")) {
                    OJProperty ojProperty = new OJProperty(f);
                    OJs.put(OJ.valueOf(ojProperty.getOjName().toUpperCase()), ojProperty);
                }
            }
        }
        System.out.println("INFO: Init remote/OJProperty Config OK!");
    }
}
