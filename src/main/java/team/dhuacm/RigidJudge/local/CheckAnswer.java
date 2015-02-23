package team.dhuacm.RigidJudge.local;

import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.model.LocalProblem;
import team.dhuacm.RigidJudge.model.LocalSpecialProblem;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.*;

/**
 * Created by wujy on 15-1-18.
 */
class CheckAnswer {

    private static final Logger logger = LoggerFactory.getLogger(CheckAnswer.class.getSimpleName());

    private static String removeSpace(String s) {
        StringBuilder temp = new StringBuilder();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (s.charAt(i) != ' ' && s.charAt(i) != '\n' && s.charAt(i) != '\t') {
                temp.append(s.charAt(i));
            }
        }
        return temp.toString();
    }

    private static int executeSpecialJudge(String filename) {
        int returnValue = -1;

        ByteArrayOutputStream errorStream = null;
        ByteArrayOutputStream outputStream;
        ExecuteWatchdog watchdog = null;

        try {
            String commandLine = "./" + filename + " test.in test.out test.user";
            logger.info("cmd: {}", commandLine);

            CommandLine cmdLine = CommandLine.parse(commandLine);
            DefaultExecutor executor = new DefaultExecutor();
            watchdog = new ExecuteWatchdog(DataProvider.Local_SpecialJudgeTimeLimit * 1000);
            executor.setWatchdog(watchdog);
            executor.setWorkingDirectory(new File("tmp"));
            executor.setExitValues(new int[]{0, 1, 2});
            outputStream = new ByteArrayOutputStream();
            errorStream = new ByteArrayOutputStream();
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
            executor.setStreamHandler(streamHandler);

            returnValue = executor.execute(cmdLine);

            logger.info("Run done!\n{}", outputStream.toString());
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

    public static void doCheckAnswer(Solution solution) throws IOException {
        LocalProblem problem = (LocalProblem) solution.getProblem();
        if (problem instanceof LocalSpecialProblem) {
            Writer writer = new BufferedWriter(new FileWriter(new File("tmp/test.user")));
            writer.write(solution.getOutput());
            writer.close();

            switch (executeSpecialJudge("spj")) {
                case 0:
                    solution.setResult(Result.Accept_Answer);
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
                    solution.setResult(Result.Judge_Error);
            }
        } else {
            //logger.info("'{}'", solution.getStdAns());
            //logger.info("'{}'", solution.getOutput());
            if (solution.getStdAns().equals(solution.getOutput())) {
                solution.setResult(Result.Accept_Answer);
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
