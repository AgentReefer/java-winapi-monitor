package com.monitor.win.struct;

import com.sun.jna.*;
import com.sun.jna.platform.win32.WinDef.ULONGLONG;
import java.util.Arrays;
import java.util.List;

/**
 * A container for WinAPI-related structures used across Kernel32 and Iphlpapi calls.
 * This allows reusability and better modularity across system services.
 */
public interface WindowsStructures {

    /**
     * Represents the SYSTEM_INFO structure used by GetSystemInfo.
     * Contains architecture, memory, processor, and address range data.
     */
    class SYSTEM_INFO extends Structure {
        public PROCESSOR_ARCHITECTURE_UNION processorArchitecture;
        public int dwPageSize;
        public Pointer lpMinimumApplicationAddress;
        public Pointer lpMaximumApplicationAddress;
        public Pointer dwActiveProcessorMask;
        public int dwNumberOfProcessors;
        public int dwProcessorType;
        public int dwAllocationGranularity;
        public short wProcessorLevel;
        public short wProcessorRevision;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(
                    "processorArchitecture", "dwPageSize", "lpMinimumApplicationAddress",
                    "lpMaximumApplicationAddress", "dwActiveProcessorMask",
                    "dwNumberOfProcessors", "dwProcessorType", "dwAllocationGranularity",
                    "wProcessorLevel", "wProcessorRevision"
            );
        }

        /**
         * Processor architecture sub-structure.
         */
        public static class PROCESSOR_ARCHITECTURE_UNION extends Structure {
            public short wProcessorArchitecture;
            public short wReserved;

            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("wProcessorArchitecture", "wReserved");
            }
        }
    }

    /**
     * Represents the MEMORYSTATUSEX structure used by GlobalMemoryStatusEx.
     * Holds system memory statistics such as total and available memory.
     */
    class MEMORYSTATUSEX extends Structure {
        public int dwLength = size();
        public int dwMemoryLoad;
        public ULONGLONG ullTotalPhys;
        public ULONGLONG ullAvailPhys;
        public ULONGLONG ullTotalPageFile;
        public ULONGLONG ullAvailPageFile;
        public ULONGLONG ullTotalVirtual;
        public ULONGLONG ullAvailVirtual;
        public ULONGLONG ullAvailExtendedVirtual;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(
                    "dwLength", "dwMemoryLoad", "ullTotalPhys", "ullAvailPhys",
                    "ullTotalPageFile", "ullAvailPageFile",
                    "ullTotalVirtual", "ullAvailVirtual", "ullAvailExtendedVirtual"
            );
        }
    }

    /**
     * Represents OSVERSIONINFOEXW structure used by GetVersionExW or RtlGetVersion.
     * Provides version, build, and service pack info for the Windows OS.
     */
    class OSVERSIONINFOEXW extends Structure {
        public int dwOSVersionInfoSize;
        public int dwMajorVersion;
        public int dwMinorVersion;
        public int dwBuildNumber;
        public int dwPlatformId;
        public char[] szCSDVersion = new char[128];
        public short wServicePackMajor;
        public short wServicePackMinor;
        public short wSuiteMask;
        public byte wProductType;
        public byte wReserved;

        public OSVERSIONINFOEXW() {
            super();
            this.dwOSVersionInfoSize = this.size();
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(
                    "dwOSVersionInfoSize",
                    "dwMajorVersion",
                    "dwMinorVersion",
                    "dwBuildNumber",
                    "dwPlatformId",
                    "szCSDVersion",
                    "wServicePackMajor",
                    "wServicePackMinor",
                    "wSuiteMask",
                    "wProductType",
                    "wReserved"
            );
        }
    }

    /**
     * Represents a single IP address entry for an adapter.
     * Used as part of IP_ADAPTER_INFO.
     */
    class IP_ADDR_STRING extends Structure {
        public Pointer Next;
        public byte[] IpAddress = new byte[16];
        public byte[] IpMask = new byte[16];
        public int Context;

        public IP_ADDR_STRING() {}

        public IP_ADDR_STRING(Pointer p) {
            super(p);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("Next", "IpAddress", "IpMask", "Context");
        }
    }

    /**
     * Represents adapter information used by GetAdaptersInfo.
     * Provides MAC address, IP configuration, DHCP info, etc.
     */
    class IP_ADAPTER_INFO extends Structure {
        public Pointer Next;
        public int ComboIndex;
        public byte[] AdapterName = new byte[260];
        public byte[] Description = new byte[132];
        public int AddressLength;
        public byte[] Address = new byte[8];
        public int Index;
        public int Type;
        public int DhcpEnabled;
        public Pointer CurrentIpAddress;
        public IP_ADDR_STRING IpAddressList;
        public IP_ADDR_STRING GatewayList;
        public IP_ADDR_STRING DhcpServer;
        public boolean HaveWins;
        public IP_ADDR_STRING PrimaryWinsServer;
        public IP_ADDR_STRING SecondaryWinsServer;
        public int LeaseObtained;
        public int LeaseExpires;

        public IP_ADAPTER_INFO(Pointer p) {
            super(p);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(
                    "Next", "ComboIndex", "AdapterName", "Description", "AddressLength", "Address",
                    "Index", "Type", "DhcpEnabled", "CurrentIpAddress", "IpAddressList",
                    "GatewayList", "DhcpServer", "HaveWins", "PrimaryWinsServer", "SecondaryWinsServer",
                    "LeaseObtained", "LeaseExpires"
            );
        }

        public String getAdapterName() {
            return Native.toString(AdapterName);
        }

        public String getDescription() {
            return Native.toString(Description);
        }

        public String getIpAddress() {
            return Native.toString(IpAddressList.IpAddress);
        }

        public byte[] getMacBytes() {
            return Arrays.copyOf(Address, AddressLength);
        }
    }
}
