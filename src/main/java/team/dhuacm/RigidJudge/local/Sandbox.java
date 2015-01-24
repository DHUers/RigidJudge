package team.dhuacm.RigidJudge.local;

/**
 * Created by wujy on 15-1-25.
 */
public class Sandbox {
    static {
        System.loadLibrary("sandbox");
    }

    public static native int init();

    public static void main(String[] args) {
        init();
    }
}
