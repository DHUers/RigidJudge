package team.dhuacm.RigidJudge.local;

import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.model.LocalProblem;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by wujy on 15-1-18.
 */
public class Run {

    private final static Logger logger = LoggerFactory.getLogger(Run.class.getSimpleName());

    public static boolean doRun(Solution solution) {
        boolean runResult = false;

        long timeBegin = 0, timeEnd = 0;
        ByteArrayOutputStream errorStream = null;
        ByteArrayOutputStream outputStream = null;
        ByteArrayInputStream inputStream = null;
        ExecuteWatchdog watchdog = null;

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

            timeBegin = System.currentTimeMillis();

            executor.execute(cmdLine);

            timeEnd = System.currentTimeMillis();
            //solution.setTime(timeEnd - timeBegin);

            //if (timeEnd - timeBegin >= solution.getTimeLimit()) {
            //    solution.setResult(Result.Time_Limit_Exceeded);
            //    runResult = false;
            //} else {
                solution.setOutput(outputStream.toString());
                if (outputStream.toString().length() >= DataProvider.Local_OutputLengthLimit) {
                    solution.setResult(Result.Output_Limit_Exceeded);
                    runResult = false;
                } else {
                    runResult = true;
                }
            //}

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
            logger.info("Runtime: {} ms, Memory: {} KB", solution.getTime(), solution.getMemory());
        }

        return runResult;
    }
}
