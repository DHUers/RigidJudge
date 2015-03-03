package team.dhuacm.RigidJudge.local;

import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.config.Language;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.model.LocalProblem;
import team.dhuacm.RigidJudge.model.LocalSpecialProblem;
import team.dhuacm.RigidJudge.model.Solution;
import team.dhuacm.RigidJudge.utils.DiffUtils;

import java.io.*;
import java.util.LinkedList;

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

    private static int executeSpecialJudge(String filename, Language language) {
        int returnValue = -1;

        ByteArrayOutputStream errorStream = null;
        ByteArrayOutputStream outputStream;
        ExecuteWatchdog watchdog = null;

        try {
            String commandLine;
            if (language.equals(Language.JAVA)) {
                commandLine = "java -cp tmp " + filename + " tmp/test.in tmp/test.out tmp/test.user";
            } else {
                commandLine = "./tmp/" + filename + " tmp/test.in tmp/test.out tmp/test.user";
            }
            logger.info("cmd: '{}'", commandLine);

            CommandLine cmdLine = CommandLine.parse(commandLine);
            DefaultExecutor executor = new DefaultExecutor();
            watchdog = new ExecuteWatchdog(DataProvider.LOCAL_SPECIAL_JUDGE_TIME_LIMIT * 1000);
            executor.setWatchdog(watchdog);
            executor.setExitValues(new int[]{0, 1, 2});
            outputStream = new ByteArrayOutputStream();
            errorStream = new ByteArrayOutputStream();
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
            executor.setStreamHandler(streamHandler);

            returnValue = executor.execute(cmdLine);

            logger.info("SPJ Run done!\n{}{}", outputStream.toString(), errorStream.toString());
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
            writer = new BufferedWriter(new FileWriter(new File("tmp/test.in")));
            writer.write(solution.getInput());
            writer.close();
            writer = new BufferedWriter(new FileWriter(new File("tmp/test.out")));
            writer.write(solution.getStdAns());
            writer.close();

            switch (executeSpecialJudge("spj", ((LocalSpecialProblem) problem).getJudgerProgramLanguage())) {
                case 0:
                    solution.setResult(Result.Accepted);
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
                solution.setResult(Result.Accepted);
            } else {
                String stdAns = removeSpace(solution.getStdAns());
                String output = removeSpace(solution.getOutput());
                if (stdAns.equals(output)) {
                    solution.setResult(Result.Presentation_Error);
                } else {
                    solution.setResult(Result.Wrong_Answer);
                }

                if (DataProvider.LOCAL_DIFF_REPORT) {
                    DiffUtils diff = new DiffUtils();
                    LinkedList<DiffUtils.Diff> diffs = diff.diff_main(stdAns, output);
                    diff.diff_cleanupSemantic(diffs);
                    solution.setCompareInfo(diff.diff_prettyHtml(diffs));
                    logger.info("Diff info: '{}'", solution.getCompareInfo());
                }
            }
        }
    }

}
