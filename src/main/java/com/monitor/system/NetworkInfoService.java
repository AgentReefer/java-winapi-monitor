package com.monitor.system;

import com.monitor.util.Formatter;
import com.monitor.win.IpHlpApi;
import com.monitor.win.struct.WindowsStructures.IP_ADAPTER_INFO;
import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Retrieves network adapter information using WinAPI GetAdaptersInfo.
 */
public class NetworkInfoService implements SystemInfoProvider {

    private static final Logger logger = LoggerFactory.getLogger(NetworkInfoService.class);

    @Override
    public String getInfo() {
        IntByReference bufferLength = new IntByReference();
        IpHlpApi.INSTANCE.GetAdaptersInfo(null, bufferLength);

        Memory buffer = new Memory(bufferLength.getValue());
        int result = IpHlpApi.INSTANCE.GetAdaptersInfo(buffer, bufferLength);

        if (result != 0) {
            logger.warn("GetAdaptersInfo failed with code {}", result);
            return "Failed to retrieve network adapter info.";
        }

        StringBuilder sb = new StringBuilder();
        IP_ADAPTER_INFO adapter = new IP_ADAPTER_INFO(buffer);

        while (adapter != null) {
            String description = adapter.getDescription();
            String adapterName = adapter.getAdapterName();
            String ip = adapter.getIpAddress();
            String mac = getMacAddress(adapter.getMacBytes(), adapter.AddressLength);

            Map<String, String> data = new LinkedHashMap<>();
            if (description == null || description.isBlank()) {
                description = "Unknown Adapter";
            }

            data.put("MAC Address", mac);
            data.put("IP Address", ip);
            data.put("Adapter Name", adapterName); // Optional: comment out if not needed

            logger.info("Adapter: {} | MAC: {} | IP: {}", description, mac, ip);

            sb.append(Formatter.formatBlock(description, data)).append("\n");

            if (adapter.Next == null) break;
            adapter = new IP_ADAPTER_INFO(adapter.Next);
        }

        return sb.toString();
    }

    private String getMacAddress(byte[] mac, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length && i < mac.length; i++) {
            sb.append(String.format("%02X", mac[i]));
            if (i < length - 1) sb.append("-");
        }
        return sb.toString();
    }
}
