package team.dhuacm.RigidJudge.main;

import team.dhuacm.RigidJudge.model.Solution;

/**
 * Created by wujy on 15-1-8.
 */
public class LocalResolver {

    private Solution solution;

    public LocalResolver(Solution solution) {
        this.solution = solution;
    }

    public void handle() {
        if (compile()) {
            run();
            check();
        }
    }

    private boolean compile() {
        return true;
    }

    private void run() {

    }

    private void check() {

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
