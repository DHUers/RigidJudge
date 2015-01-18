package team.dhuacm.RigidJudge.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.model.LocalProblem;
import team.dhuacm.RigidJudge.model.LocalSpecialProblem;
import team.dhuacm.RigidJudge.model.Solution;

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

    public static void doCheckAnswer(Solution solution) {
        LocalProblem problem = (LocalProblem) solution.getProblem();
        if (problem != null) {
            if (problem instanceof LocalSpecialProblem) {
                // TODO: Special Judge
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
