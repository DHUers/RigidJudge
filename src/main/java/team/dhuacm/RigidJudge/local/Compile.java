package team.dhuacm.RigidJudge.local;

import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.config.Language;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.*;

/**
 * Created by wujy on 15-1-18.
 */
public class Compile {

    private final static Logger logger = LoggerFactory.getLogger(Compile.class.getSimpleName());

    public static boolean doCompile(Solution solution) {
        boolean compileResult = false;

        File file;
        if (solution.getLanguage().equals(Language.C)) {  // Temporarily, will change to DataProvider
            file = new File("test.c");
        } else if (solution.getLanguage().equals(Language.CPP)) {
            file = new File("test.cpp");
        } else {
            file = new File("test.java");
        }

        ByteArrayOutputStream errorStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(solution.getCode());
            writer.close();

            String commandline = DataProvider.Local_CompileCommand.get(solution.getLanguage());
            logger.info("cmd: '{}'", commandline);

            CommandLine cmdLine = CommandLine.parse(commandline);
            DefaultExecutor executor = new DefaultExecutor();
            ExecuteWatchdog watchdog = new ExecuteWatchdog(DataProvider.Local_CompileTimeLimit * 1000);
            executor.setWatchdog(watchdog);
            outputStream = new ByteArrayOutputStream();
            errorStream = new ByteArrayOutputStream();
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
            executor.setStreamHandler(streamHandler);
            executor.execute(cmdLine);

            solution.setCompileInfo("Compile successfully!");
            compileResult = true;

        } catch (ExecuteException e) {
            String info = null;
            if (errorStream != null) {
                info = errorStream.toString() + outputStream.toString();
            }
            if (info != null && info.equals("")) {
                info = "Compile no exitValue!";
            }
            logger.error("Compile Error!\n{}", info);
            compileResult = false;
            solution.setCompileInfo(info);
        } catch (IOException e) {
            logger.error("IO Error!", e);
            compileResult = false;
            solution.setCompileInfo("Compile Error!");
        } finally {
            file.delete();
        }

        return compileResult;
    }
}
