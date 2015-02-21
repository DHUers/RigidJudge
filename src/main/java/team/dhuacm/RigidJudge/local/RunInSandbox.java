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
    private static long time_usage;
    private static long memory_usage;

    public static boolean doRun(Solution solution, String target) {
        boolean runResult = false;

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
            watchdog = new ExecuteWatchdog(solution.getTimeLimit() + 100);
            executor.setWatchdog(watchdog);
            outputStream = new ByteArrayOutputStream();
            errorStream = new ByteArrayOutputStream();
            inputStream = new ByteArrayInputStream(solution.getInput().getBytes());
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream, inputStream);
            executor.setStreamHandler(streamHandler);

            executor.execute(cmdLine);

            System.out.println(errorStream);
            String[] sandboxReply = errorStream.toString().split("\n");
            String result = sandboxReply[0].split(": ")[1];
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
            } else {
                solution.setResult(Result.Runtime_Error);
            }
            logger.error("Error!\n{}", errorStream);
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
