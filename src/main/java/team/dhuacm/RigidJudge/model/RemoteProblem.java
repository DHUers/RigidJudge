package team.dhuacm.RigidJudge.model;

import team.dhuacm.RigidJudge.config.OJ;

/**
 * Created by wujy on 15-1-8.
 */
public class RemoteProblem extends Problem {
    private OJ oj;
    private String ojIndex;

    public RemoteProblem(int id, OJ oj, String ojIndex) {
        super(id);
        this.oj = oj;
        this.ojIndex = ojIndex;
    }

    public OJ getOj() {
        return oj;
    }

    public void setOj(OJ oj) {
        this.oj = oj;
    }

    public String getOjIndex() {
        return ojIndex;
    }

    public void setOjIndex(String ojIndex) {
        this.ojIndex = ojIndex;
    }
}
