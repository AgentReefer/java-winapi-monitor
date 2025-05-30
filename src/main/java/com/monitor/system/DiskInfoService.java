package com.monitor.system;


import com.monitor.util.Formatter;
import com.monitor.win.Kernel32;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.WString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Service for retrieving disk storage information for each logical drive.
 * Displays total and free space along with drive type.
 */
public class DiskInfoService implements SystemInfoProvider {

    private static final Logger logger = LoggerFactory.getLogger(DiskInfoService.class);

    /**
     * Retrieves formatted information about all root drives.
     *
     * @return Formatted string with per-drive details.
     */
    @Override
    public String getInfo() {
        StringBuilder result = new StringBuilder();
        File[] roots = File.listRoots();

        if (roots == null || roots.length == 0) {
            logger.warn("No root drives detected.");
            return Formatter.formatBlock("DiskInfo", Map.of("Status", "No drives found"));
        }

        for (File root : roots) {
            try {
                String path = root.getAbsolutePath(); // "C:\"
                String label = path.replace("\\", ""); // -> "C:"

                LongByReference freeBytes = new LongByReference();
                LongByReference totalBytes = new LongByReference();
                LongByReference availableBytes = new LongByReference();

                boolean success = Kernel32.INSTANCE.GetDiskFreeSpaceExW(
                        new WString(path),
                        availableBytes,
                        totalBytes,
                        freeBytes
                );

                if (!success) {
                    logger.warn("Failed to retrieve disk space for {}", path);
                    result.append(Formatter.formatBlock("DiskInfo." + label, Map.of(
                            "Status", "Failed to retrieve disk information"
                    )));
                    continue;
                }

                long total = totalBytes.getValue();
                long free = freeBytes.getValue();
                String type = resolveDriveType(path);

                Map<String, String> diskInfo = new LinkedHashMap<>();
                diskInfo.put("Total", formatBytes(total));
                diskInfo.put("Free", formatBytes(free));
                diskInfo.put("Type", type);

                logger.info("Disk {} info: Total = {}, Free = {}, Type = {}",
                        label, formatBytes(total), formatBytes(free), type);

                result.append(Formatter.formatBlock("DiskInfo." + label, diskInfo));

            } catch (Throwable t) {
                logger.error("Exception while processing drive {}", root, t);
                result.append(Formatter.formatBlock("DiskInfo.ERROR", Map.of(
                        "Path", root.getAbsolutePath(),
                        "Error", "Exception occurred"
                )));
            }
        }

        return result.toString();
    }

    /**
     * Resolves the drive type using GetDriveTypeW.
     *
     * @param path Root drive path (e.g., "C:\")
     * @return String representing drive type
     */
    private String resolveDriveType(String path) {
        int type = Kernel32.INSTANCE.GetDriveTypeW(new WString(path));
        return switch (type) {
            case 1 -> "No root directory";
            case 2 -> "Removable";
            case 3 -> "Local Disk";
            case 4 -> "Network Drive";
            case 5 -> "CD-ROM";
            case 6 -> "RAM Disk";
            default -> "Unknown";
        };
    }

    /**
     * Converts a byte value to a human-readable GB string.
     *
     * @param bytes Byte count
     * @return Readable GB string
     */
    private String formatBytes(long bytes) {
        double gb = bytes / (1024.0 * 1024.0 * 1024.0);
        return String.format("%.2f GB", gb);
    }
}
