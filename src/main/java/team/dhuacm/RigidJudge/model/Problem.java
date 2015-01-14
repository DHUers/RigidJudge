package team.dhuacm.RigidJudge.model;

/**
 * Created by wujy on 15-1-7.
 */
public class Problem {
    protected int id;
    protected int judgeType;

    public Problem(int id, int judgeType) {
        this.id = id;
        this.judgeType = judgeType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJudgeType() {
        return judgeType;
    }

    public void setJudgeType(int judgeType) {
        this.judgeType = judgeType;
    }

}
