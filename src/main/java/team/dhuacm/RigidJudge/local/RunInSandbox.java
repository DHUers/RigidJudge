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
class RunInSandbox {

    private static final Logger logger = LoggerFactory.getLogger(RunInSandbox.class.getSimpleName());

    public static boolean doRun(Solution solution, String target) {
        boolean runResult = false;
        long time_usage = 0, memory_usage = 0;
        ByteArrayOutputStream errorStream = null;
        ByteArrayOutputStream outputStream;
        ByteArrayInputStream inputStream;
        ExecuteWatchdog watchdog = null;
        //Sandbox sandbox = new Sandbox();  // TODO: Change to JNI
        //System.out.println(Sandbox.init());
        try {
            String commandLine = "./sandbox/sandbox " + DataProvider.Local_RunCommand.get(solution.getLanguage()).replace("{target}", target) + " " + solution.getTimeLimit() + " " + solution.getMemoryLimit();
            logger.info("cmd: {}", commandLine);

            CommandLine cmdLine = CommandLine.parse(commandLine);
            DefaultExecutor executor = new DefaultExecutor();
            watchdog = new ExecuteWatchdog(solution.getTimeLimit() * 2);
            executor.setWatchdog(watchdog);
            outputStream = new ByteArrayOutputStream();
            errorStream = new ByteArrayOutputStream();
            inputStream = new ByteArrayInputStream(solution.getInput().getBytes());
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream, inputStream);
            executor.setStreamHandler(streamHandler);

            executor.execute(cmdLine);

            logger.info("Run done!\n{}", errorStream);
            String[] sandboxReply = errorStream.toString().split("\n");
            int lines = sandboxReply.length;  // prevent program wrote data to error stream
            String result = sandboxReply[lines-5].split(": ")[1];
            time_usage = Long.parseLong(sandboxReply[lines-3].split(": ")[1].replace(" ms", ""));
            memory_usage = Long.parseLong(sandboxReply[lines-2].split(": ")[1].replace(" KB", ""));

            // "PD", /* pending */
            // "OK", /* okay */
            // "RF", /* restricted function */
            // "ML", /* memory limit exceeded */
            // "OL", /* output limit exceeded */
            // "TL", /* time limit exceeded */
            // "RT", /* runtime error */
            // "AT", /* abnormal termination */
            // "IE", /* internal error */
            // "BP", /* bad policy */
            if (result.equals("OK")) {
                solution.setOutput(outputStream.toString());
                if (outputStream.toString().length() >= DataProvider.Local_OutputLengthLimit) {
                    solution.setResult(Result.Output_Limit_Exceeded);
                } else {  // AC or WA or PE
                    runResult = true;
                }
            } else if (result.equals("ML")) {
                solution.setResult(Result.Memory_Limit_Exceeded);
                memory_usage = solution.getMemoryLimit();
            } else if (result.equals("TL")) {
                solution.setResult(Result.Time_Limit_Exceeded);
                time_usage = solution.getTimeLimit();
            } else if (result.equals("OL")) {
                solution.setResult(Result.Output_Limit_Exceeded);
            } else {
                solution.setResult(Result.Runtime_Error);
            }
            solution.setExecuteInfo(errorStream.toString());
        } catch (ExecuteException e) {
            if (watchdog.killedProcess()) {
                solution.setResult(Result.Time_Limit_Exceeded);
                time_usage = solution.getTimeLimit();
            } else {
                solution.setResult(Result.Runtime_Error);
            }
            logger.error("Error!\n{}", errorStream);
            runResult = false;
        } catch (ArrayIndexOutOfBoundsException e) {
            /* Issue:
             *      If program can not terminate when EOF and waiting for extra input redundantly,
             *      sandbox returned with blank error stream.
             *      This caused exception while parsing sandbox report.
             *      We mark it with TLE verdict currently.
             */
            solution.setResult(Result.Time_Limit_Exceeded);
            time_usage = solution.getTimeLimit();
            logger.error(null, e);
            runResult = false;
        } catch (Exception e) {
            solution.setResult(Result.Judge_Error);
            logger.error(null, e);
            runResult = false;
        } finally {
            solution.setTime(time_usage);
            solution.setMemory(memory_usage);
            logger.info("Time: {} ms, Memory: {} KB", solution.getTime(), solution.getMemory());
        }

        return runResult;
    }
}
