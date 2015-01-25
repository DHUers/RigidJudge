package team.dhuacm.RigidJudge.model;

/**
 * Created by wujy on 15-1-7.
 */
public class Problem {
    private int id;
    private String judgeType;

    Problem(int id, String judgeType) {
        this.id = id;
        this.judgeType = judgeType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJudgeType() {
        return judgeType;
    }

    public void setJudgeType(String judgeType) {
        this.judgeType = judgeType;
    }

}
