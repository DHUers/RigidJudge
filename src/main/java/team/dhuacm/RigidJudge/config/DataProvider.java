package team.dhuacm.RigidJudge.config;

import team.dhuacm.RigidJudge.bean.OJAccount;
import team.dhuacm.RigidJudge.bean.Solution;

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

    // judge listen port
    public static int ListenPort;
    // user pool
    public final static HashMap<team.dhuacm.RigidJudge.config.OJ, BlockingQueue<OJAccount>> Accounts = new HashMap<team.dhuacm.RigidJudge.config.OJ, BlockingQueue<OJAccount>>();
    // oj config
    public final static HashMap<team.dhuacm.RigidJudge.config.OJ, team.dhuacm.RigidJudge.bean.OJ> OJs = new HashMap<team.dhuacm.RigidJudge.config.OJ, team.dhuacm.RigidJudge.bean.OJ>();
    // solution queue
    public final static LinkedBlockingQueue<Solution> SolutionQueue = new LinkedBlockingQueue<Solution>();
    // judge server ip
    public static String JudgeServerIp;
    // judge server port
    public static int JudgeServerPort;
    // 查询时间间隔
    public static List<Integer> QueryTime = new ArrayList<Integer>();
    // Http请求重试次数
    public static int RetryCount = 0;
    // Socket等待时间
    public static int SocketTimeout = 0;
    // Connection建立时间
    public static int ConnectionTimeout = 0;

    static {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream("Config.properties"));
        } catch (FileNotFoundException e1) {
            System.out.println();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JudgeServerIp = p.getProperty("JudgeServer").split(":")[0];
        JudgeServerPort = Integer.parseInt(p.getProperty("JudgeServer").split(":")[1]);
        ListenPort = Integer.parseInt(p.getProperty("ListenPort"));
        RetryCount = Integer.parseInt(p.getProperty("RetryCount"));
        SocketTimeout = Integer.parseInt(p.getProperty("SocketTimeout"));
        ConnectionTimeout = Integer.parseInt(p.getProperty("ConnectionTimeout"));

        for (String str : p.getProperty("QueryTime").split(",")) {
            QueryTime.add(Integer.parseInt(str));
        }

        System.out.printf("INFO: Judge Server: %s:%s", JudgeServerIp, JudgeServerPort);
        System.out.printf("INFO: Listen Port: %s", ListenPort);
        System.out.printf("INFO: Retry Count: %s, Socket Timeout: %s, Connection Timeout: %s", RetryCount, SocketTimeout, ConnectionTimeout);
        System.out.printf("INFO: Query Time:%s", QueryTime);

        // users
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("Accounts"));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;
                else {
                    String[] strs = line.split(",");
                    String ojName = strs[0].trim().toUpperCase();
                    team.dhuacm.RigidJudge.config.OJ oj = team.dhuacm.RigidJudge.config.OJ.valueOf(ojName);
                    OJAccount bean = new OJAccount();
                    bean.setUsername(strs[1].trim());
                    bean.setPassword(strs[2].trim());
                    BlockingQueue<OJAccount> queue = Accounts.get(oj);
                    if (null == queue) {
                        queue = new LinkedBlockingQueue<OJAccount>();
                        Accounts.put(oj, queue);
                    }
                    queue.put(bean);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Fatal Error: Cannot find Accounts File!");
            System.exit(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
        System.out.println("INFO: Init Accounts Config OK!");
        for (team.dhuacm.RigidJudge.config.OJ oj : Accounts.keySet()) {
            System.out.println(oj + "\t" + Accounts.get(oj).size());
        }

        // OJs
        File file = new File("OJs");
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (f.getName().endsWith(".properties")) {
                    team.dhuacm.RigidJudge.bean.OJ oj = new team.dhuacm.RigidJudge.bean.OJ(f);
                    OJs.put(team.dhuacm.RigidJudge.config.OJ.valueOf(oj.getOjName().toUpperCase()), oj);
                }
            }
        }
        System.out.println("INFO: Init OJs Config OK!");

    }
}
