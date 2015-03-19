package team.dhuacm.RigidJudge.model;

/**
 * Created by wujy on 15-1-7.
 */
public abstract class Problem {
    private int id;

    Problem(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
