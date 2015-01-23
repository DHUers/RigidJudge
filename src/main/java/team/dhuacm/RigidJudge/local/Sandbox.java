package team.dhuacm.RigidJudge.local;

/**
 * Created by wujy on 15-1-23.
 */
public class Sandbox {
    native void Sandbox();
    static {
        System.loadLibrary("sandbox");
    }
    static public void main(String argv[]) {
        Sandbox sandbox = new Sandbox();
        sandbox.init();
    }

    private void init() {
    }
}
