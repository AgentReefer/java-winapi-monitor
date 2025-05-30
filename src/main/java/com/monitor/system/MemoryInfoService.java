package com.monitor.system;

import com.monitor.util.Formatter;
import com.monitor.util.MemoryTypeMapper;
import com.monitor.win.Kernel32;
import com.monitor.win.struct.WindowsStructures.MEMORYSTATUSEX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Retrieves memory info using WinAPI and PowerShell (SMBIOSMemoryType).
 */
public class MemoryInfoService implements SystemInfoProvider {

    private static final Logger logger = LoggerFactory.getLogger(MemoryInfoService.class);

    @Override
    public String getInfo() {
        MEMORYSTATUSEX memStatus = new MEMORYSTATUSEX();
        boolean success = Kernel32.INSTANCE.GlobalMemoryStatusEx(memStatus);
        if (!success) {
            logger.warn("GlobalMemoryStatusEx failed.");
            return Formatter.formatBlock("Memory Info", Map.of("Status", "Unavailable"));
        }

        long totalPhysMem = memStatus.ullTotalPhys.longValue();

        String speed = getPowerShellValue("(Get-CimInstance Win32_PhysicalMemory | Select-Object -First 1 Speed).Speed");
        String typeCode = getPowerShellValue("(Get-CimInstance Win32_PhysicalMemory | Select-Object -First 1 SMBIOSMemoryType).SMBIOSMemoryType");

        String memoryType = "Unknown";
        try {
            int code = Integer.parseInt(typeCode.trim());
            memoryType = MemoryTypeMapper.getTypeName(code);
        } catch (NumberFormatException e) {
            logger.warn("Failed to parse memory type code: {}", typeCode);
        }

        logger.info("Collected memory info: total={} | speed={} | type={}", formatSize(totalPhysMem), speed, memoryType);

        Map<String, String> data = Map.of(
                "Total Memory", formatSize(totalPhysMem),
                "Speed (MHz)", speed,
                "Type", memoryType
        );

        return Formatter.formatBlock("Memory Information", data);
    }

    private String getPowerShellValue(String command) {
        try {
            Process process = new ProcessBuilder("powershell.exe", "-Command", command)
                    .redirectErrorStream(true)
                    .start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n")).trim();
            }
        } catch (Exception e) {
            logger.error("PowerShell command failed: {}", command, e);
            return "N/A";
        }
    }

    private String formatSize(long bytes) {
        return String.format("%d GB", bytes / (1024 * 1024 * 1024));
    }
}
