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
    private static long timeBegin = 0;
    private static long timeEnd = 0;
    private static long memory_usage;

    public static boolean doRun(Solution solution) {
        boolean runResult = false;

        ByteArrayOutputStream errorStream = null;
        ByteArrayOutputStream outputStream;
        ByteArrayInputStream inputStream;
        ExecuteWatchdog watchdog = null;
        Runtime runtime = null;

        try {
            String commandLine = DataProvider.Local_RunCommand.get(solution.getLanguage()).replace("{target}", "JavaWrapper");
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

            timeBegin = System.currentTimeMillis();

            executor.execute(cmdLine);

            timeEnd = System.currentTimeMillis();
            System.out.println(errorStream);
            String[] sandboxReply = errorStream.toString().split("\n");
            //String result = sandboxReply[0].split(": ")[1];
            //time_usage = Long.parseLong(sandboxReply[1].split(": ")[1].replace("ms", ""));
            memory_usage = Long.parseLong(sandboxReply[0].split(": ")[1].replace("kB", ""));

            if (timeEnd - timeBegin >= solution.getTimeLimit()) {
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
            long time_usage = timeEnd - timeBegin;
            solution.setTime(time_usage < solution.getTimeLimit() ? time_usage : solution.getTimeLimit());
            solution.setMemory(memory_usage < solution.getMemoryLimit() ? memory_usage : solution.getMemoryLimit());
            logger.info("Time: {} ms, Memory: {} KB", solution.getTime(), solution.getMemory());
        }

        return runResult;
    }
}
