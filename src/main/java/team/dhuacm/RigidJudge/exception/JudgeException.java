package team.dhuacm.RigidJudge.exception;

/**
 * Created by wujy on 15-1-11.
 */
public class JudgeException extends Exception {

    private static final long serialVersionUID = -1946027510506589673L;

    public JudgeException() {
        super();
    }

    public JudgeException(String message) {
        super(message);
    }

    public JudgeException(Throwable cause) {
        super(cause);
    }

    public JudgeException(String message, Throwable cause) {
        super(message, cause);
    }

}