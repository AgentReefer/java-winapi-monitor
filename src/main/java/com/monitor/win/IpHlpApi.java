package com.monitor.win;

import com.monitor.win.struct.WindowsStructures.IP_ADAPTER_INFO;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * JNA binding for Iphlpapi.dll to access GetAdaptersInfo.
 */
public interface IpHlpApi extends StdCallLibrary {
    IpHlpApi INSTANCE = Native.load("Iphlpapi", IpHlpApi.class);

    int GetAdaptersInfo(Pointer adapterInfo, IntByReference size);
}
