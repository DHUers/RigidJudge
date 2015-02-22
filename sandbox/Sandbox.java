import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;

import java.util.List;

public class Sandbox {
    public static long memoryUsage = 0;
    public static Main main = null;

    public static void main(String[] args) {
        main = new Main();
        main.main(null);

        try {
            List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
            for (MemoryPoolMXBean pool : pools) {
                memoryUsage += pool.getPeakUsage().getUsed();
            }
            System.err.println("mem: " + memoryUsage / 1024 + "kB");
        } catch (Throwable t) {
            System.err.println("Exception in agent: " + t);
        }
    }
}