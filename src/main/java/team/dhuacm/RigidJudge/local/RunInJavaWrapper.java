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
 * Created by wujy on 15-2-22.
 */
class RunInJavaWrapper {

    private static final Logger logger = LoggerFactory.getLogger(RunInJavaWrapper.class.getSimpleName());

    public static boolean doRun(Solution solution) {
        boolean runResult = false;
        long time_usage = 0,  memory_usage = 0;
        ByteArrayOutputStream errorStream = null;
        ByteArrayOutputStream outputStream;
        ByteArrayInputStream inputStream;
        ExecuteWatchdog watchdog = null;

        try {
            String commandLine = DataProvider.Local_RunCommand.get(solution.getLanguage()).replace("{target}", "JavaWrapper");
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

            String[] wrapperReply = errorStream.toString().split("\n");
            time_usage = Long.parseLong(wrapperReply[0].split(": ")[1].replace(" ms", ""));
            memory_usage = Long.parseLong(wrapperReply[1].split(": ")[1].replace(" KB", ""));

            if (time_usage >= solution.getTimeLimit()) {
                solution.setResult(Result.Time_Limit_Exceeded);
                runResult = false;
            } else if (memory_usage >= solution.getMemoryLimit()) {
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
            if (watchdog.killedProcess()) {
                solution.setResult(Result.Time_Limit_Exceeded);
            } else {
                solution.setResult(Result.Runtime_Error);
            }
            logger.error("Error!\n{}", errorStream.toString());
            runResult = false;
        } catch (Exception e) {
            solution.setResult(Result.Runtime_Error);
            logger.error(null, e);
            runResult = false;
        } finally {
            solution.setTime(time_usage < solution.getTimeLimit() ? time_usage : solution.getTimeLimit());
            solution.setMemory(memory_usage < solution.getMemoryLimit() ? memory_usage : solution.getMemoryLimit());
            logger.info("Time: {} ms, Memory: {} KB", solution.getTime(), solution.getMemory());
        }

        return runResult;
    }
}
