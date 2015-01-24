package team.dhuacm.RigidJudge.local;

import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.model.LocalProblem;
import team.dhuacm.RigidJudge.model.LocalSpecialProblem;
import team.dhuacm.RigidJudge.model.Solution;

import javax.xml.crypto.Data;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by wujy on 15-1-18.
 */
public class CheckAnswer {

    private final static Logger logger = LoggerFactory.getLogger(CheckAnswer.class.getSimpleName());

    private static String removeSpace(String s) {
        StringBuffer temp = new StringBuffer();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (s.charAt(i) != ' ' && s.charAt(i) != '\n' && s.charAt(i) != '\t') {
                temp.append(s.charAt(i));
            }
        }
        return temp.toString();
    }

    public static int executeSpecialJudge(String filename) {
        int returnValue = -1;

        ByteArrayOutputStream errorStream = null;
        ByteArrayOutputStream outputStream;
        ByteArrayInputStream inputStream;
        ExecuteWatchdog watchdog = null;

        try {
            String commandLine = "./spj/" + filename;
            logger.info("cmd: {}", commandLine);

            CommandLine cmdLine = CommandLine.parse(commandLine);
            DefaultExecutor executor = new DefaultExecutor();
            watchdog = new ExecuteWatchdog(DataProvider.Local_SpecialJudgeTimeLimit);
            executor.setWatchdog(watchdog);
            executor.setExitValues(new int[]{0, 1, 2});
            outputStream = new ByteArrayOutputStream();
            errorStream = new ByteArrayOutputStream();
            inputStream = new ByteArrayInputStream("".getBytes());  // TODO
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream, inputStream);
            executor.setStreamHandler(streamHandler);

            returnValue = executor.execute(cmdLine);

            System.out.println(errorStream.toString());

            logger.info("Run done!");
        } catch (ExecuteException e) {
            if (watchdog.killedProcess()) {
                logger.error("SPJ TimeLimitExceeded!\n");
            }
            logger.error(errorStream.toString());
            returnValue = -1;
        } catch (Exception e) {
            logger.error(null, e);
            returnValue = -1;
        } finally {
            logger.info("SPJ Result: {}", returnValue);
        }

        return returnValue;
    }

    public static void doCheckAnswer(Solution solution) {
        LocalProblem problem = (LocalProblem) solution.getProblem();
        if (problem != null) {
            if (problem instanceof LocalSpecialProblem) {
                switch (executeSpecialJudge(((LocalSpecialProblem) problem).getJudgerProgramName())) {
                    case 0:
                        solution.setResult(Result.Accept);
                        break;
                    case 1:
                        solution.setResult(Result.Wrong_Answer);
                        break;
                    case 2:
                        solution.setResult(Result.Presentation_Error);
                        break;
                    case -1:
                        solution.setResult(Result.Judge_Error);
                        break;
                    default:
                        solution.setResult(Result.Other_Error);
                }
            } else {
                //logger.info("'{}'", solution.getStdAns());
                //logger.info("'{}'", solution.getOutput());
                if (solution.getStdAns().equals(solution.getOutput())) {
                    solution.setResult(Result.Accept);
                } else {
                    String stdAns = removeSpace(solution.getStdAns());
                    String output = removeSpace(solution.getOutput());
                    if (stdAns.equals(output)) {
                        solution.setResult(Result.Presentation_Error);
                    } else {
                        solution.setResult(Result.Wrong_Answer);
                    }
                }
            }
        }
    }

}
