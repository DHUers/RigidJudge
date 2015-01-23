package team.dhuacm.RigidJudge.local;

/**
 * Created by wujy on 15-1-23.
 */
public class Sandbox {
    static {
        System.loadLibrary("sandbox");
    }

    private native void init();

    public static void main(String[] args) {
        new Sandbox().init();
    }
}
