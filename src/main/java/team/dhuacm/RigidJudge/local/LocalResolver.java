package team.dhuacm.RigidJudge.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.model.Solution;

/**
 * Created by wujy on 15-1-8.
 */
public class LocalResolver {

    private Solution solution;
    private final static Logger logger = LoggerFactory.getLogger(LocalResolver.class.getSimpleName());

    public LocalResolver(Solution solution) {
        this.solution = solution;
    }

    public void handle() {
        if (Compile.doCompile()) {
            if (Run.doRun()) {
                CheckAnswer.doCheckAnswer();
            }
        }
    }

    private boolean prepare() {
        // TODO
        // download

        // check

        // pre-compile special judge code

        // store

        return true;
    }

    private boolean load() {
        // TODO
        prepare();

        return true;
    }
}
