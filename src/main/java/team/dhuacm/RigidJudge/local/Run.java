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
class Run {

    private static final Logger logger = LoggerFactory.getLogger(Run.class.getSimpleName());
    private static long timeBegin = 0;
    private static long timeEnd = 0;
    private static long memoryBegin = 0;
    private static final long memoryEnd = 0;

    public static boolean doRun(Solution solution, String target) {
        boolean runResult = false;

        ByteArrayOutputStream errorStream = null;
        ByteArrayOutputStream outputStream;
        ByteArrayInputStream inputStream;
        ExecuteWatchdog watchdog = null;
        Runtime runtime;

        try {
            String commandLine = DataProvider.Local_RunCommand.get(solution.getLanguage()).replace("{target}", "Main");
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
            timeBegin = System.currentTimeMillis();

            executor.execute(cmdLine);

            timeEnd = System.currentTimeMillis();

            if (timeEnd - timeBegin >= solution.getTimeLimit()) {
                solution.setResult(Result.Time_Limit_Exceeded);
                runResult = false;
            } else if (memoryEnd - memoryBegin >= solution.getMemoryLimit() * 1024) {
                solution.setResult(Result.Memory_Limit_Exceeded);
                runResult = false;
            } else {
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
