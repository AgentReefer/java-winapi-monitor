package com.monitor;

import com.monitor.system.*;
import com.monitor.util.Profiler;
import com.monitor.win.User32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Entry point for the system monitoring application.
 * This class initializes all services, measures execution time, and displays
 * system information in multiple MessageBox windows using WinAPI (via JNA).
 */
public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    /**
     * Main application entry.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        logger.info("=== System monitor started ===");

        List<SystemInfoProvider> services = List.of(
                new CpuInfoService(),
                new MemoryInfoService(),
                new OsInfoService(),
                new DiskInfoService(),
                new NetworkInfoService()
        );

        for (SystemInfoProvider service : services) {
            Profiler profiler = new Profiler();
            profiler.start();

            String info = service.getInfo();

            profiler.stop();
            long duration = profiler.getDurationMillis();

            logger.info("Executed {} in {} ms", service.getClass().getSimpleName(), duration);

            User32.showMessageBox(
                    service.getClass().getSimpleName().replace("Service", ""),
                    info
            );
        }

        logger.info("=== System monitor finished ===");
    }
}
