package team.dhuacm.RigidJudge.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wujy on 15-1-19.
 */
public class MemoryDetect implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(MemoryDetect.class.getSimpleName());

    Runtime runtime = Runtime.getRuntime();

    @Override
    public void run() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Run.memoryEnd = runtime.totalMemory() - runtime.freeMemory();
        //logger.info(String.valueOf(Run.memoryEnd));
    }
}
