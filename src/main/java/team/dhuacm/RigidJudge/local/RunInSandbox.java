package team.dhuacm.RigidJudge.local;

import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by wujy on 15-1-18.
 */
public class RunInSandbox {

    private final static Logger logger = LoggerFactory.getLogger(RunInSandbox.class.getSimpleName());
    public static long timeBegin = 0, timeEnd = 0, memoryBegin = 0, memoryEnd = 0;

    public static boolean doRun(Solution solution) {
        boolean runResult = false;

        ByteArrayOutputStream errorStream = null;
        ByteArrayOutputStream outputStream = null;
        ByteArrayInputStream inputStream = null;
        ExecuteWatchdog watchdog = null;
        Runtime runtime = null;

        Sandbox sandbox = new Sandbox();
        //sandbox.init();

        return runResult;
    }
}
