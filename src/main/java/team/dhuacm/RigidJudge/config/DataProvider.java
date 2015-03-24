package team.dhuacm.RigidJudge.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(DataProvider.class.getSimpleName());

    private static final Properties p;
    
    // Common configurations
    public static final String RABBITMQ_HOST;
    public static final Integer RABBITMQ_PORT;
    public static final String RABBITMQ_USERNAME;
    public static final String RABBITMQ_PASSWORD;
    public static final HashMap<OJ, OJProperty> OJ_LIST = new HashMap<OJ, OJProperty>();

    // Local judge configurations
    public static final String LOCAL_DATA_SERVER_URL;
    public static final String LOCAL_DATA_SERVER_TOKEN;
    public static final Boolean LOCAL_RUN_IN_SANDBOX;
    public static final Integer LOCAL_COMPILE_TIME_LIMIT;
    public static final Integer LOCAL_OUTPUT_LENGTH_LIMIT;
    public static final Integer LOCAL_SPECIAL_JUDGE_TIME_LIMIT;
    public static final Boolean LOCAL_DIFF_REPORT;
    public static final Boolean LOCAL_CLEAN_AFTER_JUDGE;
    public static final Map<Language, String> LOCAL_FILE_SUFFIX = new HashMap<Language, String>();
    public static final Map<Language, String> LOCAL_COMPILE_COMMAND = new HashMap<Language, String>();
    public static final Map<Language, String> LOCAL_RUN_COMMAND = new HashMap<Language, String>();

    // Remote judge configurations
    public static final List<Integer> REMOTE_QUERY_INTERVAL = new ArrayList<Integer>();
    public static final Integer REMOTE_CONCURRENCY;
    public static final Integer REMOTE_RETRY_TIMES;
    public static final Integer REMOTE_SOCKET_TIMEOUT;
    public static final Integer REMOTE_CONNECTION_TIMEOUT;
    public static final HashMap<OJ, BlockingQueue<OJAccount>> REMOTE_OJ_ACCOUNT_LIST = new HashMap<OJ, BlockingQueue<OJAccount>>();

    // Common data structure
    public static final LinkedBlockingQueue<Solution> JUDGED_SOLUTION_QUEUE = new LinkedBlockingQueue<Solution>();

    static {
        p = new Properties();
        try {
            p.load(new FileInputStream("configs/Config.properties"));
        } catch (FileNotFoundException e) {
            logger.error("Fatal Error: Cannot find the file: configs/Config.properties", e);
            System.exit(1);
        } catch (IOException e) {
            logger.error(null, e);
        }

        RABBITMQ_HOST = getEnvIfNotEmpty("RabbitMQ_Host", "127.0.0.1");
        RABBITMQ_PORT = Integer.parseInt(getEnvIfNotEmpty("RabbitMQ_Port", "5672"));
        RABBITMQ_USERNAME = getEnvIfNotEmpty("RabbitMQ_Username", "judger");
        RABBITMQ_PASSWORD = getEnvIfNotEmpty("RabbitMQ_Password", "JUDGER_PASSWORD");

        LOCAL_DATA_SERVER_URL = getEnvIfNotEmpty("Local_Data_Server_Url", "127.0.0.1:80");
        LOCAL_DATA_SERVER_TOKEN = getEnvIfNotEmpty("Local_Data_Server_Token", "RigidJudge");
        LOCAL_RUN_IN_SANDBOX = Boolean.parseBoolean(getEnvIfNotEmpty("Local_Run_In_Sandbox", "true"));
        LOCAL_COMPILE_TIME_LIMIT = Integer.parseInt(getEnvIfNotEmpty("Local_Compile_Time_Limit", "5"));
        LOCAL_OUTPUT_LENGTH_LIMIT = Integer.parseInt(getEnvIfNotEmpty("Local_Output_Length_Limit", "5242880"));
        LOCAL_SPECIAL_JUDGE_TIME_LIMIT = Integer.parseInt(getEnvIfNotEmpty("Local_Special_Judge_Time_Limit", "5"));
        LOCAL_DIFF_REPORT = Boolean.parseBoolean(getEnvIfNotEmpty("Local_Diff_Report", "true"));
        LOCAL_CLEAN_AFTER_JUDGE = Boolean.parseBoolean(getEnvIfNotEmpty("Clean_After_Judge", "true"));

        REMOTE_CONCURRENCY = Integer.parseInt(getEnvIfNotEmpty("Remote_Concurrency", "10"));
        REMOTE_RETRY_TIMES = Integer.parseInt(getEnvIfNotEmpty("Remote_Retry_Times", "3"));
        REMOTE_SOCKET_TIMEOUT = Integer.parseInt(getEnvIfNotEmpty("Remote_Socket_Timeout", "30"));
        REMOTE_CONNECTION_TIMEOUT = Integer.parseInt(getEnvIfNotEmpty("Remote_Connection_Timeout", "30"));
        for (String str : getEnvIfNotEmpty("Remote_Query_Interval", "10,20,25,30,60,120,600,1200,2400").split(",")) {
            REMOTE_QUERY_INTERVAL.add(Integer.parseInt(str));
        }


        logger.info("RabbitMQ server: {}:{}, Username: {}, Password: {}", RABBITMQ_HOST, RABBITMQ_PORT, RABBITMQ_USERNAME, RABBITMQ_PASSWORD);
        logger.info("[Local] Run in sandbox: {}, Diff report: {}, Compile time limit: {}", LOCAL_RUN_IN_SANDBOX, LOCAL_DIFF_REPORT, LOCAL_COMPILE_TIME_LIMIT);
        logger.info("        Output length limit: {}, Special judge time limit: {}", LOCAL_OUTPUT_LENGTH_LIMIT, LOCAL_SPECIAL_JUDGE_TIME_LIMIT);
        logger.info("        Data server: {}, Token: {}", LOCAL_DATA_SERVER_URL, LOCAL_DATA_SERVER_TOKEN);

        try {
            p.load(new FileInputStream("configs/local/fileSuffix.properties"));
        } catch (FileNotFoundException e) {
            logger.error("Fatal Error: Cannot find the file: configs/local/fileSuffix.properties", e);
            System.exit(1);
        } catch (IOException e) {
            logger.error(null, e);
        }
        for (Language l : Language.values()) {
            if (l.equals(Language.DEFAULT)) continue;
            String suffix = getEnvIfNotEmpty(l.name().toLowerCase(), "");
            LOCAL_FILE_SUFFIX.put(l, suffix);
            logger.info("        {} - '{}'", l.name(), suffix);
        }
        logger.info("Init local/fileSuffix.properties config OK!");

        try {
            p.load(new FileInputStream("configs/local/compileCmd.properties"));
        } catch (FileNotFoundException e) {
            logger.error("Fatal Error: Cannot find the file: configs/local/compileCmd.properties", e);
            System.exit(1);
        } catch (IOException e) {
            logger.error(null, e);
        }
        for (Language l : Language.values()) {
            if (l.equals(Language.DEFAULT)) continue;
            String command = getEnvIfNotEmpty(l.name().toLowerCase(), "");
            LOCAL_COMPILE_COMMAND.put(l, command);
            logger.info("        {} - '{}'", l.name(), command);
        }
        logger.info("Init local/compileCmd.properties config OK!");

        try {
            p.load(new FileInputStream("configs/local/runCmd.properties"));
        } catch (FileNotFoundException e) {
            logger.error("Fatal Error: Cannot find the file: configs/local/runCmd.properties", e);
            System.exit(1);
        } catch (IOException e) {
            logger.error(null, e);
        }
        for (Language l : Language.values()) {
            if (l.equals(Language.DEFAULT)) continue;
            String command = getEnvIfNotEmpty(l.name().toLowerCase(), "");
            LOCAL_RUN_COMMAND.put(l, command);
            logger.info("        {} - '{}'", l.name(), command);
        }
        logger.info("Init local/runCmd.properties config OK!");


        logger.info("[Remote] Retry num: {}, Socket timeout: {}, Connection timeout: {}", REMOTE_RETRY_TIMES, REMOTE_SOCKET_TIMEOUT, REMOTE_CONNECTION_TIMEOUT);
        logger.info("         Query interval: {}", REMOTE_QUERY_INTERVAL);

        // Remote OJ Accounts
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("configs/remote/OJAccounts.properties"));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    String[] strs = line.split(",");
                    String ojName = strs[0].trim().toUpperCase();
                    OJ oj = OJ.valueOf(ojName);
                    OJAccount account = new OJAccount(strs[1].trim(), strs[2].trim());
                    BlockingQueue<OJAccount> queue = REMOTE_OJ_ACCOUNT_LIST.get(oj);
                    if (null == queue) {
                        queue = new LinkedBlockingQueue<OJAccount>();
                        REMOTE_OJ_ACCOUNT_LIST.put(oj, queue);
                    }
                    queue.put(account);
                }
            }
        } catch (FileNotFoundException e) {
            logger.error("Fatal Error: Cannot find the file: remote/OJAccounts.properties", e);
            System.exit(1);
        } catch (InterruptedException e) {
            logger.error(null, e);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        for (OJ oj : REMOTE_OJ_ACCOUNT_LIST.keySet()) {
            logger.info("         {} - {}", oj, REMOTE_OJ_ACCOUNT_LIST.get(oj).size());
        }
        logger.info("Init remote/OJAccounts.properties config OK!");

        // OJ_LIST
        File file = new File("configs/remote/OJProperty");
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (f.getName().endsWith(".properties")) {
                    OJProperty ojProperty = new OJProperty(f);
                    OJ_LIST.put(OJ.valueOf(ojProperty.getOjName().toUpperCase()), ojProperty);
                }
            }
        }
        logger.info("Init remote/OJProperty config OK!");
    }

    private static String getEnvIfNotEmpty(String env, String defaultEnv) {
        String envName = "RIGIDOJ_" + env.toUpperCase();
        if (System.getenv(envName) != null) {
            return System.getenv(envName);
        } else {
            return p.getProperty(env, defaultEnv);
        }
    }
}
