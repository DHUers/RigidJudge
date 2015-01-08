package team.dhuacm.RigidJudge.main;

import team.dhuacm.RigidJudge.model.Solution;

/**
 * Created by wujy on 15-1-8.
 */
public class RemoteResolver implements Runnable {

    private Solution solution;

    public RemoteResolver(Solution solution) {
        this.solution = solution;
    }

    @Override
    public void run() {

    }
}
