package team.dhuacm.RigidJudge.local;

import org.apache.commons.exec.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.DemuxOutputStream;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

/**
 * Created by wujy on 15-1-18.
 */
public class Run {

    private final static Logger logger = LoggerFactory.getLogger(Run.class.getSimpleName());
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
/*

        // global ant project settings
        Project project = new Project();
        project.setBaseDir(new File(System.getProperty("user.dir")));
        project.init();
        DefaultLogger logger = new DefaultLogger();
        project.addBuildListener(logger);
        logger.setOutputPrintStream(System.out);
        logger.setErrorPrintStream(System.err);
        logger.setMessageOutputLevel(Project.MSG_INFO);
        System.setOut(new PrintStream(new DemuxOutputStream(project, false)));
        System.setErr(new PrintStream(new DemuxOutputStream(project, true)));
        project.fireBuildStarted();

        Throwable caught = null;
        try {
            // an echo example
            Echo echo = new Echo();
            echo.setTaskName("Echo");
            echo.setProject(project);
            echo.init();
            echo.setMessage("Launching ...");
            echo.execute();

            */
/** initialize an java task **//*

            Java javaTask = new Java();
            javaTask.setNewenvironment(true);
            javaTask.setTaskName("runjava");
            javaTask.setProject(project);
            javaTask.setFork(true);
            javaTask.setFailonerror(true);
            javaTask.setClassname(Run.class.getName());

            // add some vm args
            Commandline.Argument jvmArgs = javaTask.createJvmarg();
            jvmArgs.setLine("-Xms512m -Xmx512m");

            // added some args for to class to launch
            Commandline.Argument taskArgs = javaTask.createArg();
            taskArgs.setLine("bla path=/tmp/");

            */
/** set the class path *//*

            Path classPath = new Path(project);
            File classDir = new File(System.getProperty("user.dir"), "classes");
            javaTask.setClasspath(classPath);
            Path classPath = new Path(project);
            classPath.setPath(classDir.getPath());
            FileSet fileSet = new FileSet();
            fileSet.setDir(classDir);
           // fileSet.setIncludes("***
*/
/*.jar");
            classPath.addFileset(fileSet);
            javaTask.setClasspath(classPath);

            javaTask.init();
            int ret = javaTask.executeJava();
            System.out.println("return code: " + ret);

        } catch (BuildException e) {
            caught = e;
        }
        project.log("finished");
        project.fireBuildFinished(caught);
*/
        return runResult;
    }
}
