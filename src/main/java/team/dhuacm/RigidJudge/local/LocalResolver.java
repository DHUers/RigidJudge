package team.dhuacm.RigidJudge.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.model.Solution;

/**
 * Created by wujy on 15-1-8.
 */
public class LocalResolver {

    private Solution solution;
    private final static Logger logger = LoggerFactory.getLogger(LocalResolver.class.getSimpleName());

    public LocalResolver(Solution solution) {
        this.solution = solution;
        logger.info("{} - solution id: {}", solution.getProblem().getId(), solution.getId());
    }

    public void handle() {
        if (Prepare.doPrepare(solution)) {
            if (Compile.doCompile(solution)) {
                logger.info("Compile success!");
                if (Run.doRun(solution)) {
                    logger.info("Run success!");
                    //CheckAnswer.doCheckAnswer(solution);
                } else {
                    logger.info("Run failed! {}", solution.getResult());
                }
            } else {
                logger.info("Compile failed!");
                solution.setResult(Result.Compile_Error);
            }
        } else {
            logger.info("Fetch problem data failed!");
            solution.setResult(Result.Judge_Error);
        }
    }

}
