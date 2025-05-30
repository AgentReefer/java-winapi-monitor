package com.monitor.util;

import java.util.Map;

/**
 * Utility class for formatting structured output blocks.
 */
public class Formatter {

    /**
     * Formats a titled block of key-value data.
     *
     * @param title Block title
     * @param data  Map of key → value
     * @return formatted string
     */
    public static String formatBlock(String title, Map<String, String> data) {
        StringBuilder sb = new StringBuilder("=== ").append(title).append(" ===\n");

        data.forEach((key, value) ->
                sb.append(String.format("%-15s: %s%n", key, value))
        );

        sb.append("--------------------------\n");
        return sb.toString();
    }
}
