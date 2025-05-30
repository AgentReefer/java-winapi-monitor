package com.monitor.util;

import java.util.Map;

/**
 * Maps WinAPI MemoryType codes to readable RAM type names.
 */
public class MemoryTypeMapper {

    private static final Map<Integer, String> TYPE_MAP = Map.ofEntries(
            Map.entry(0, "Unknown"),
            Map.entry(1, "Other"),
            Map.entry(2, "DRAM"),
            Map.entry(3, "Synchronous DRAM"),
            Map.entry(4, "Cache DRAM"),
            Map.entry(5, "EDO"),
            Map.entry(6, "EDRAM"),
            Map.entry(7, "VRAM"),
            Map.entry(8, "SRAM"),
            Map.entry(9, "RAM"),
            Map.entry(10, "ROM"),
            Map.entry(11, "Flash"),
            Map.entry(12, "EEPROM"),
            Map.entry(13, "FEPROM"),
            Map.entry(14, "EPROM"),
            Map.entry(15, "CDRAM"),
            Map.entry(16, "3DRAM"),
            Map.entry(17, "SDRAM"),
            Map.entry(18, "SGRAM"),
            Map.entry(19, "RDRAM"),
            Map.entry(20, "DDR"),
            Map.entry(21, "DDR2"),
            Map.entry(22, "DDR2 FB-DIMM"),
            Map.entry(24, "DDR3"),
            Map.entry(25, "FBD2"),
            Map.entry(26, "DDR4")
    );

    public static String getTypeName(int code) {
        return TYPE_MAP.getOrDefault(code, "Unknown (" + code + ")");
    }
}
