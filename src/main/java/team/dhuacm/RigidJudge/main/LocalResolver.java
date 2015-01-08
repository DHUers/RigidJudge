package team.dhuacm.RigidJudge.main;

import team.dhuacm.RigidJudge.model.Solution;

/**
 * Created by wujy on 15-1-8.
 */
public class LocalResolver implements Runnable {

    private Solution solution;

    public LocalResolver(Solution solution) {
        this.solution = solution;
    }

    @Override
    public void run() {

    }
}
