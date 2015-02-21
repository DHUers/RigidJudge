package team.dhuacm.RigidJudge.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.utils.FileUtils;

import java.io.File;

/**
 * Created by wujy on 15-1-25.
 */
class Clean {
    private static final Logger logger = LoggerFactory.getLogger(Clean.class.getSimpleName());

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void doClean() {
        if (FileUtils.deleteDir(new File("tmp"))) {
            logger.info("Success.");
        } else {
            logger.error("Fail.");
        }
    }
}
