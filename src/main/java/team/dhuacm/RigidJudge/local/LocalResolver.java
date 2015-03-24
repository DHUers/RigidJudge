package team.dhuacm.RigidJudge.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.config.Language;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.exception.JudgeException;
import team.dhuacm.RigidJudge.exception.NetworkException;
import team.dhuacm.RigidJudge.model.LocalProblem;
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

    public void handle() {
        String source = "tmp/Main", target = "tmp/Main";
        try {
            Prepare.doPrepare(solution, source);

            if (Compile.doCompile(solution.getLanguage(), source, target)) {
                solution.setCompileInfo(Compile.compileInfo);
                logger.info("Compile success!");

                if (((LocalProblem) solution.getProblem()).getLimitType().equals("per")) {  // limit per case
                    handleMultipleCase(solution, target);
                } else {  // normal once execution
                    runAndCheckAnswer(solution, target);
                }
            } else {
                solution.setCompileInfo(Compile.compileInfo);
                solution.setResult(Result.Compile_Error);
                logger.info("Compile failed!");
            }

            if (DataProvider.LOCAL_CLEAN_AFTER_JUDGE) {
                Clean.doClean();
            }
        } catch (IOException e) {
            logger.error("Fetch problem data failed!", e);
            solution.setResult(Result.Judge_Error);
        } catch (NetworkException e) {
            logger.error("Fetch problem data failed!", e);
            solution.setResult(Result.Network_Error);
        } catch (JudgeException e) {
            logger.error("Fetch problem data failed!", e);
            solution.setResult(Result.Judge_Error);
        }
    }

    private void handleMultipleCase(Solution solution, String target) throws IOException {
        String[] inputs = solution.getInput().split("<Case Separator>\\n");
        String[] outputs = solution.getStdAns().split("<Case Separator>\\n");
        if (inputs.length == outputs.length) {
            Result result = Result.Queue;
            for (int i = 0; i < inputs.length; i++) {
                Solution caseSolution = new Solution(solution);
                caseSolution.setInput(inputs[i]);
                caseSolution.setStdAns(outputs[i]);
                logger.info("Case #{} Begin", i+1, inputs[i], outputs[i]);
                runAndCheckAnswer(caseSolution, target);
                logger.info("Case #{} End {}", i+1, caseSolution.getResult());
                if (result.compareTo(caseSolution.getResult()) < 0) {
                    result = caseSolution.getResult();
                }
                solution.setExecuteInfo(solution.getExecuteInfo() + "Case #" + (i+1) + ": " + caseSolution.getResult() + "\n" + caseSolution.getExecuteInfo() + "\n");
                solution.setCompareInfo(solution.getCompareInfo() + "Case #" + (i+1) + ": " + caseSolution.getCompareInfo() + "\n");
            }
            solution.setResult(result);
        } else {
            logger.error("Test case not match! Input: {}, Output: {}.", inputs.length, outputs.length);
            solution.setResult(Result.Judge_Error);
        }
    }

    private void runAndCheckAnswer(Solution solution, String target) throws IOException {
        boolean runSuccess;
        if (solution.getLanguage().equals(Language.JAVA)) {
            runSuccess = RunInJavaWrapper.doRun(solution);
        } else {
            if (DataProvider.LOCAL_RUN_IN_SANDBOX) {
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
    }

}
