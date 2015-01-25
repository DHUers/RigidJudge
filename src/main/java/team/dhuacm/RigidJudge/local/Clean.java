package team.dhuacm.RigidJudge.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by wujy on 15-1-25.
 */
public class Clean {
    private final static Logger logger = LoggerFactory.getLogger(Clean.class.getSimpleName());

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean doClean() {
        if (deleteDir(new File("tmp"))) {
            logger.info("Success.");
        } else {
            logger.error("fail.");
        }
        return true;
    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
