package com.monitor.system;

import com.monitor.util.Formatter;
import com.monitor.win.Kernel32;
import com.monitor.win.struct.WindowsStructures.OSVERSIONINFOEXW;
import com.monitor.win.struct.WindowsStructures.SYSTEM_INFO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Retrieves Windows OS version and architecture info.
 * Uses PowerShell for accurate version and GetSystemInfo for architecture.
 */
public class OsInfoService implements SystemInfoProvider {

    private static final Logger logger = LoggerFactory.getLogger(OsInfoService.class);

    private static final String[] ARCHITECTURE_MAP = {
            "x86", "MIPS", "Alpha", "PPC", "SHX", "ARM",
            "IA64", "Alpha64", "MSIL", "AMD64", "IA32_ON_WIN64", "Neutral", "ARM64"
    };

    @Override
    public String getInfo() {
        String version = getVersionViaPowerShell();
        String arch = getArchitecture();

        Map<String, String> data = new LinkedHashMap<>();
        data.put("Version", version);
        data.put("Architecture", arch);

        String result = Formatter.formatBlock("OS Info", data);
        logger.info("Formatted OS Info:\n{}", result);
        return result;
    }

    private String getVersionViaPowerShell() {
        String command =
                "$info = Get-ComputerInfo | Select-Object -First 1 WindowsProductName, WindowsVersion, OsBuildNumber; " +
                        "Write-Output \"Product=$($info.WindowsProductName)\"; " +
                        "Write-Output \"Version=$($info.WindowsVersion)\"; " +
                        "Write-Output \"Build=$($info.OsBuildNumber)\";";

        try {
            Process process = new ProcessBuilder("powershell.exe", "-Command", command)
                    .redirectErrorStream(true)
                    .start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {

                Map<String, String> data = reader.lines()
                        .filter(line -> line.contains("="))
                        .map(line -> line.split("=", 2))
                        .collect(Collectors.toMap(kv -> kv[0].trim(), kv -> kv[1].trim()));

                String name = data.getOrDefault("Product", "Windows");
                String version = data.getOrDefault("Version", "?");
                String build = data.getOrDefault("Build", "?");

                try {
                    int buildNumber = Integer.parseInt(build);
                    if (name.contains("Windows 10") && buildNumber >= 22000) {
                        logger.info("Windows 11 detected via build number {}. Overriding product name '{}'.", buildNumber, name);
                        name = name.replace("Windows 10", "Windows 11");
                    }
                } catch (NumberFormatException e) {
                    logger.warn("Unable to parse build number '{}', skipping Windows 11 substitution.", build);
                }

                return String.format("%s %s (Build %s)", name, version, build);
            }

        } catch (Exception e) {
            logger.error("PowerShell version detection failed. Falling back to WinAPI.", e);
        }

        OSVERSIONINFOEXW versionInfo = new OSVERSIONINFOEXW();
        if (Kernel32.INSTANCE.GetVersionExW(versionInfo)) {
            return String.format("Windows %d.%d (Build %d)",
                    versionInfo.dwMajorVersion,
                    versionInfo.dwMinorVersion,
                    versionInfo.dwBuildNumber);
        }

        return "Unknown";
    }

    private String getArchitecture() {
        try {
            SYSTEM_INFO info = new SYSTEM_INFO();
            Kernel32.INSTANCE.GetSystemInfo(info);
            short code = info.processorArchitecture.wProcessorArchitecture;

            String arch = (code >= 0 && code < ARCHITECTURE_MAP.length)
                    ? ARCHITECTURE_MAP[code]
                    : "Unknown (" + code + ")";

            logger.info("Detected architecture: code={}, resolved={}", code, arch);
            return arch;

        } catch (Throwable t) {
            logger.warn("GetSystemInfo failed, using fallback.", t);
            String fallback = System.getProperty("os.arch");
            logger.info("Fallback architecture: {}", fallback);
            return fallback;
        }
    }
}
