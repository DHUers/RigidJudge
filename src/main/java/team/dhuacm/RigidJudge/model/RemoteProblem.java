package team.dhuacm.RigidJudge.model;

import team.dhuacm.RigidJudge.config.OJ;

/**
 * Created by wujy on 15-1-8.
 */
public class RemoteProblem extends Problem {
    private OJ oj;
    private String ojIndex;
    //private String sourceUrl;

    public RemoteProblem(int id, int judgeType, OJ oj, String ojIndex) {
        super(id, judgeType);
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
