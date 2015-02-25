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

    public static boolean doRun(Solution solution, String target) {
        boolean runResult = false;
        long timeBegin = 0, timeEnd = 0, memoryBegin = 0, memoryEnd = 0;
        ByteArrayOutputStream errorStream = null;
        ByteArrayOutputStream outputStream;
        ByteArrayInputStream inputStream;
        ExecuteWatchdog watchdog = null;
        Runtime runtime = null;

        try {
            String commandLine = DataProvider.Local_RunCommand.get(solution.getLanguage()).replace("{target}", "Sandbox");
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
            memoryEnd = runtime.totalMemory() - runtime.freeMemory();

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
            solution.setExecuteInfo(errorStream.toString());
            logger.info("Run done!\n{}", errorStream);
        } catch (ExecuteException e) {
            timeEnd = System.currentTimeMillis();
            memoryEnd = runtime.totalMemory() - runtime.freeMemory();
            if (watchdog.killedProcess()) {
                solution.setResult(Result.Time_Limit_Exceeded);
            } else {
                solution.setResult(Result.Runtime_Error);
            }
            logger.error("Error!\n{}", errorStream.toString());
            runResult = false;
        } catch (Exception e) {
            timeEnd = System.currentTimeMillis();
            memoryEnd = (runtime != null) ? runtime.totalMemory() - runtime.freeMemory() : memoryBegin;
            solution.setResult(Result.Runtime_Error);
            logger.error(null, e);
            runResult = false;
        } finally {
            long time_usage = timeEnd - timeBegin;
            long memory_usage = (memoryEnd - memoryBegin) / 1024;
            solution.setTime(time_usage < solution.getTimeLimit() ? time_usage : solution.getTimeLimit());
            solution.setMemory(memory_usage < solution.getMemoryLimit() ? memory_usage : solution.getMemoryLimit());
            logger.info("Time: {} ms, Memory: {} KB", solution.getTime(), solution.getMemory());
        }

        return runResult;
    }
}
