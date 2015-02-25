import java.lang.System;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;

import java.util.List;

public class JavaWrapper {
    public static long startTime, endTime;
    public static long memoryUsage = 0;
    public static Main main = null;

    public static void main(String[] args) {
        startTime = System.currentTimeMillis();

        main = new Main();
        main.main(null);

        endTime = System.currentTimeMillis();
        System.err.println("time: " + (endTime - startTime) + " ms");

        try {
            List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
            for (MemoryPoolMXBean pool : pools) {
                memoryUsage += pool.getPeakUsage().getUsed();
            }
            System.err.println("memory: " + memoryUsage / 1024 + " KB");
        } catch (Throwable t) {
            System.err.println("Exception in JavaWrapper: " + t);
        }
    }
}