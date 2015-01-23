package team.dhuacm.RigidJudge.local;

import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by wujy on 15-1-18.
 */
public class RunInSandbox {

    private final static Logger logger = LoggerFactory.getLogger(RunInSandbox.class.getSimpleName());
    public static long time_usage = 0, memory_usage = 0;
    public static String result;

    public static boolean doRun(Solution solution) {
        boolean runResult = false;

        ByteArrayOutputStream errorStream = null;
        ByteArrayOutputStream outputStream;
        ByteArrayInputStream inputStream;
        ExecuteWatchdog watchdog = null;

        try {
            String commandLine = "./sandbox/sandbox ./test " + solution.getTimeLimit() + " " + solution.getMemoryLimit();
            logger.info("cmd: {}", commandLine);

            CommandLine cmdLine = CommandLine.parse(commandLine);
            DefaultExecutor executor = new DefaultExecutor();
            watchdog = new ExecuteWatchdog(solution.getTimeLimit() + 100);
            executor.setWatchdog(watchdog);
            outputStream = new ByteArrayOutputStream();
            errorStream = new ByteArrayOutputStream();
            inputStream = new ByteArrayInputStream(solution.getInput().getBytes());
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream, inputStream);
            executor.setStreamHandler(streamHandler);

            executor.execute(cmdLine);

            System.out.println(errorStream.toString());
            String[] sandboxReply = errorStream.toString().split("\n");
            result = sandboxReply[0].split(": ")[1];
            time_usage = Long.parseLong(sandboxReply[1].split(": ")[1].replace("ms", ""));
            memory_usage = Long.parseLong(sandboxReply[2].split(": ")[1].replace("kB", ""));
            // "PD", "OK", "RF", "ML", "OL", "TL", "RT", "AT", "IE", "BP", NULL,
            if (result.equals("OK")) {
                solution.setOutput(outputStream.toString());
                if (outputStream.toString().length() >= DataProvider.Local_OutputLengthLimit) {
                    solution.setResult(Result.Output_Limit_Exceeded);
                } else {  // AC or WA or PE
                    runResult = true;
                }
            } else if (result.equals("RF")) {  // dangerous, killed
                solution.setResult(Result.Runtime_Error);
            } else if (result.equals("RT")) {
                solution.setResult(Result.Runtime_Error);
            } else if (result.equals("ML")) {
                solution.setResult(Result.Memory_Limit_Exceeded);
                memory_usage = solution.getMemoryLimit();
            } else if (result.equals("TL")) {
                solution.setResult(Result.Time_Limit_Exceeded);
                time_usage = solution.getTimeLimit();
            } else if (result.equals("OL")) {
                solution.setResult(Result.Output_Limit_Exceeded);
            }

            logger.info("Run done!");
        } catch (ExecuteException e) {
            if (watchdog.killedProcess()) {
                solution.setResult(Result.Time_Limit_Exceeded);
                time_usage = solution.getTimeLimit();
            }
            logger.error("Error!\n{}", errorStream.toString());
            runResult = false;
        } catch (Exception e) {
            logger.error(null, e);
            runResult = false;
        } finally {
            solution.setTime(time_usage);
            solution.setMemory(memory_usage);
            logger.info("Runtime: {} ms, Memory: {} KB", solution.getTime(), solution.getMemory());
        }

        return runResult;
    }
}
