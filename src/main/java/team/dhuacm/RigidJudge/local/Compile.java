package team.dhuacm.RigidJudge.local;

import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.config.Language;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by wujy on 15-1-18.
 */
class Compile {

    private static final Logger logger = LoggerFactory.getLogger(Compile.class.getSimpleName());
    public static String compileInfo;

    public static boolean doCompile(Language language, String source, String target) {
        boolean compileResult;

        ByteArrayOutputStream errorStream = null;
        ByteArrayOutputStream outputStream = null;
        ExecuteWatchdog watchdog = null;

        try {
            String commandLine = DataProvider.Local_CompileCommand.get(language);
            commandLine = commandLine.replace("{source}", source).replace("{target}", target);
            logger.info("cmd: '{}'", commandLine);

            CommandLine cmdLine = CommandLine.parse(commandLine);
            DefaultExecutor executor = new DefaultExecutor();
            watchdog = new ExecuteWatchdog(DataProvider.Local_CompileTimeLimit * 1000);
            executor.setWatchdog(watchdog);
            outputStream = new ByteArrayOutputStream();
            errorStream = new ByteArrayOutputStream();
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
            executor.setStreamHandler(streamHandler);
            executor.execute(cmdLine);

            compileInfo = "Compile successfully!";
            compileResult = true;

        } catch (ExecuteException e) {
            String info;
            info = errorStream.toString() + outputStream.toString();
            if (watchdog.killedProcess()) {
                info = "Compile time limit exceeded!";
            }
            if (info.equals("")) {
                info = "Compile no exitValue!";
            }
            logger.error("Compile Error!\n{}", info);
            compileResult = false;
            compileInfo = info;
        } catch (IOException e) {
            logger.error("IO Error!", e);
            compileResult = false;
            compileInfo = "Compile Error!";
        }

        return compileResult;
    }
}
