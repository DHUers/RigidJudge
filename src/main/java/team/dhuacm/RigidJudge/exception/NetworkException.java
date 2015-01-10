package team.dhuacm.RigidJudge.exception;

/**
 * Created by wujy on 15-1-11.
 */
public class NetworkException extends Exception {

    private static final long serialVersionUID = -1946027510506589673L;

    public NetworkException() {
        super();
    }

    public NetworkException(String message) {
        super(message);
    }

    public NetworkException(Throwable cause) {
        super(cause);
    }

    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }

}