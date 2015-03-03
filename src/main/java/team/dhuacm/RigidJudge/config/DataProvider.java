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
    public static String RabbitMQ_Host;
    public static int RabbitMQ_Port;
    public static String RabbitMQ_Username;
    public static String RabbitMQ_Password;
    public static final HashMap<OJ, OJProperty> OJs = new HashMap<OJ, OJProperty>();

    // Local judge configurations
    public static String Local_DataServerHost;
    public static int Local_DataServerPort;
    public static String Local_DataServerUsername;
    public static String Local_DataServerPassword;
    public static boolean Local_RunInSandbox = true;
    public static int Local_CompileTimeLimit = 0;
    public static int Local_OutputLengthLimit = 0;
    public static int Local_SpecialJudgeTimeLimit = 0;
    public static boolean Local_DiffReport = true;
    public static final Map<Language, String> Local_FileSuffix = new HashMap<Language, String>();
    public static final Map<Language, String> Local_CompileCommand = new HashMap<Language, String>();
    public static final Map<Language, String> Local_RunCommand = new HashMap<Language, String>();

    // Remote judge configurations
    public static final List<Integer> Remote_QueryInterval = new ArrayList<Integer>();
    public static int Remote_Concurrency = 0;
    public static int Remote_RetryTimes = 0;
    public static int Remote_SocketTimeout = 0;
    public static int Remote_ConnectionTimeout = 0;
    public static final HashMap<OJ, BlockingQueue<OJAccount>> Remote_OJAccounts = new HashMap<OJ, BlockingQueue<OJAccount>>();

    // Common data structure
    public static final LinkedBlockingQueue<Solution> JudgedSolutionQueue = new LinkedBlockingQueue<Solution>();

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

        RabbitMQ_Host = getEnvIfNotEmpty("RabbitMQ_Host", "127.0.0.1");
        RabbitMQ_Port = Integer.parseInt(getEnvIfNotEmpty("RabbitMQ_Port", "5672"));
        RabbitMQ_Username = getEnvIfNotEmpty("RabbitMQ_Username", "judger");
        RabbitMQ_Password = getEnvIfNotEmpty("RabbitMQ_Password", "JUDGER_PASSWORD");

        Local_DataServerHost = getEnvIfNotEmpty("Local_DataServerHost", "127.0.0.1");
        Local_DataServerPort = Integer.parseInt(getEnvIfNotEmpty("Local_DataServerPort", "80"));
        Local_DataServerUsername = getEnvIfNotEmpty("Local_DataServerUsername", "");
        Local_DataServerPassword = getEnvIfNotEmpty("Local_DataServerPassword", "");
        Local_RunInSandbox = Boolean.parseBoolean(getEnvIfNotEmpty("Local_RunInSandbox", "true"));
        Local_CompileTimeLimit = Integer.parseInt(getEnvIfNotEmpty("Local_CompileTimeLimit", "5"));
        Local_OutputLengthLimit = Integer.parseInt(getEnvIfNotEmpty("Local_OutputLengthLimit", "5242880"));
        Local_SpecialJudgeTimeLimit = Integer.parseInt(getEnvIfNotEmpty("Local_SpecialJudgeTimeLimit", "5"));
        Local_DiffReport = Boolean.parseBoolean(getEnvIfNotEmpty("Local_DiffReport", "true"));

        Remote_Concurrency = Integer.parseInt(getEnvIfNotEmpty("Remote_Concurrency", "10"));
        Remote_RetryTimes = Integer.parseInt(getEnvIfNotEmpty("Remote_RetryTimes", "3"));
        Remote_SocketTimeout = Integer.parseInt(getEnvIfNotEmpty("Remote_SocketTimeout", "30"));
        Remote_ConnectionTimeout = Integer.parseInt(getEnvIfNotEmpty("Remote_ConnectionTimeout", "30"));
        for (String str : getEnvIfNotEmpty("Remote_QueryInterval", "10,20,25,30,60,120,600,1200,2400").split(",")) {
            Remote_QueryInterval.add(Integer.parseInt(str));
        }


        logger.info("RabbitMQ Server: {}:{}, Username: {}, Password: {}", RabbitMQ_Host, RabbitMQ_Port, RabbitMQ_Username, RabbitMQ_Password);
        logger.info("[Local] Run in Sandbox: {}, Diff Report: {}, Compile Time Limit: {}", Local_RunInSandbox, Local_DiffReport, Local_CompileTimeLimit);
        logger.info("        Output Length Limit: {}, Special Judge Time Limit: {}", Local_OutputLengthLimit, Local_SpecialJudgeTimeLimit);
        logger.info("        Data Server: {}:{}, username: {}, password: {}", Local_DataServerHost, Local_DataServerPort, Local_DataServerUsername, Local_DataServerPassword);

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
            Local_FileSuffix.put(l, suffix);
            logger.info("        {} - '{}'", l.name(), suffix);
        }
        logger.info("Init local/fileSuffix.properties Config OK!");

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
            Local_CompileCommand.put(l, command);
            logger.info("        {} - '{}'", l.name(), command);
        }
        logger.info("Init local/compileCmd.properties Config OK!");

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
            Local_RunCommand.put(l, command);
            logger.info("        {} - '{}'", l.name(), command);
        }
        logger.info("Init local/runCmd.properties Config OK!");


        logger.info("[Remote] Retry Num: {}, Socket Timeout: {}, Connection Timeout: {}", Remote_RetryTimes, Remote_SocketTimeout, Remote_ConnectionTimeout);
        logger.info("         Query Time: {}", Remote_QueryInterval);

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
                    BlockingQueue<OJAccount> queue = Remote_OJAccounts.get(oj);
                    if (null == queue) {
                        queue = new LinkedBlockingQueue<OJAccount>();
                        Remote_OJAccounts.put(oj, queue);
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

        for (OJ oj : Remote_OJAccounts.keySet()) {
            logger.info("         {} - {}", oj, Remote_OJAccounts.get(oj).size());
        }
        logger.info("Init remote/OJAccounts.properties Config OK!");

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
        logger.info("Init remote/OJProperty Config OK!");
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
