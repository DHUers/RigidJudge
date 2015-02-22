package team.dhuacm.RigidJudge.local;

import org.apache.commons.codec.language.bm.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.config.Language;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.IOException;

/**
 * Created by wujy on 15-1-8.
 */
public class LocalResolver {

    private final Solution solution;
    private static final Logger logger = LoggerFactory.getLogger(LocalResolver.class.getSimpleName());

    public LocalResolver(Solution solution) {
        this.solution = solution;
        logger.info("problem id: {} - solution id: {}", solution.getProblem().getId(), solution.getId());
    }

    public void handle() throws IOException {
        String source = "tmp/Main", target = "tmp/Main";
        if (Prepare.doPrepare(solution, source)) {
            if (Compile.doCompile(solution.getLanguage(), source, target)) {
                solution.setCompileInfo(Compile.compileInfo);
                logger.info("Compile success!");
                boolean runSuccess;
                if (solution.getLanguage().equals(Language.JAVA)) {
                    runSuccess = RunInJavaWrapper.doRun(solution);
                } else {
                    if (DataProvider.Local_RunInSandbox) {
                        runSuccess = RunInSandbox.doRun(solution, target);
                    } else {
                        runSuccess = Run.doRun(solution, target);
                    }
                }
                if (runSuccess) {
                    logger.info("Run success!");
                    CheckAnswer.doCheckAnswer(solution);
                } else {
                    logger.info("Run failed! {}", solution.getResult());
                }
            } else {
                solution.setCompileInfo(Compile.compileInfo);
                solution.setResult(Result.Compile_Error);
                logger.info("Compile failed!");
            }
        } else {
            logger.error("Fetch problem data failed!");
            solution.setResult(Result.Judge_Error);
        }
        Clean.doClean();
    }

}
