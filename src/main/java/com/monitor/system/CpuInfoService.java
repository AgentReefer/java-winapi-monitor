package com.monitor.system;

import com.monitor.util.Formatter;
import com.monitor.win.Kernel32;
import com.monitor.win.struct.WindowsStructures.SYSTEM_INFO;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Retrieves detailed CPU information using Windows registry and WinAPI (Kernel32 + Advapi32).
 */
public class CpuInfoService implements SystemInfoProvider {

    private static final Logger logger = LoggerFactory.getLogger(CpuInfoService.class);

    private static final String CPU_KEY = "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0";
    private static final String PROCESSOR_NAME = "ProcessorNameString";
    private static final String CPU_MHZ = "~MHz";

    @Override
    public String getInfo() {
        try {
            String name = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, CPU_KEY, PROCESSOR_NAME);
            int mhz = Advapi32Util.registryGetIntValue(WinReg.HKEY_LOCAL_MACHINE, CPU_KEY, CPU_MHZ);

            SYSTEM_INFO sysInfo = new SYSTEM_INFO();
            Kernel32.INSTANCE.GetSystemInfo(sysInfo);
            int cores = sysInfo.dwNumberOfProcessors;

            logger.info("Read CPU info: model='{}', MHz={}, cores={}", name, mhz, cores);

            Map<String, String> data = Map.of(
                    "Model", name.trim(),
                    "Frequency (MHz)", String.valueOf(mhz),
                    "Cores", String.valueOf(cores)
            );

            return Formatter.formatBlock("CPU Information (WinAPI + Registry)", data);
        } catch (Exception e) {
            logger.error("Failed to read CPU info", e);
            return "Failed to retrieve CPU information.";
        }
    }
}
