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
    public static long timeBegin = 0, timeEnd = 0, memoryBegin = 0, memoryEnd = 0;

    public static boolean doRun(Solution solution) {
        boolean runResult = false;

        ByteArrayOutputStream errorStream = null;
        ByteArrayOutputStream outputStream = null;
        ByteArrayInputStream inputStream = null;
        ExecuteWatchdog watchdog = null;
        Runtime runtime = null;

        try {
            String commandLine = DataProvider.Local_RunCommand.get(solution.getLanguage());
            logger.info("cmd: {}", commandLine);

            CommandLine cmdLine = CommandLine.parse(commandLine);
            DefaultExecutor executor = new DefaultExecutor();
            watchdog = new ExecuteWatchdog(solution.getTimeLimit());
            executor.setWatchdog(watchdog);
            outputStream = new ByteArrayOutputStream();
            errorStream = new ByteArrayOutputStream();
            inputStream = new ByteArrayInputStream(solution.getInput().getBytes());
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream, inputStream);
            executor.setStreamHandler(streamHandler);

            runtime = Runtime.getRuntime();
            memoryBegin = runtime.totalMemory() - runtime.freeMemory();
            new Thread(new MemoryDetect()).start();
            timeBegin = System.currentTimeMillis();

            executor.execute(cmdLine);

            timeEnd = System.currentTimeMillis();

            if (timeEnd - timeBegin >= solution.getTimeLimit()) {
                solution.setResult(Result.Time_Limit_Exceeded);
                runResult = false;
            } else if (memoryEnd - memoryBegin >= solution.getMemoryLimit() * 1024) {
                solution.setResult(Result.Memory_Limit_Exceeded);
                runResult = false;
            } else{
                solution.setOutput(outputStream.toString());
                if (outputStream.toString().length() >= DataProvider.Local_OutputLengthLimit) {
                    solution.setResult(Result.Output_Limit_Exceeded);
                    runResult = false;
                } else {
                    runResult = true;
                }
            }

            logger.info("Run done!");
        } catch (ExecuteException e) {
            timeEnd = System.currentTimeMillis();
            if (watchdog.killedProcess()) {
                solution.setResult(Result.Time_Limit_Exceeded);
            } else {
                solution.setResult(Result.Runtime_Error);
            }
            logger.error("Error!\n{}", errorStream.toString());
            runResult = false;
        } catch (Exception e) {
            timeEnd = System.currentTimeMillis();
            solution.setResult(Result.Runtime_Error);
            logger.error(null, e);
            runResult = false;
        } finally {
            solution.setTime(timeEnd - timeBegin);
            solution.setMemory((memoryEnd - memoryBegin) / 1024);
            logger.info("Runtime: {} ms, Memory: {} KB", solution.getTime(), solution.getMemory());
        }

        return runResult;
    }
}