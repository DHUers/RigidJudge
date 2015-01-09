package team.dhuacm.RigidJudge.config;

/**
 * Created by wujy on 15-1-7.
 */
public class OJAccount {
    private String username;
    private String password;

    public OJAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
