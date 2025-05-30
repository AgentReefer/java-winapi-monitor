package com.monitor.win;

import com.monitor.win.struct.WindowsStructures.OSVERSIONINFOEXW;
import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * JNA binding for ntdll.dll to access RtlGetVersion.
 */
public interface Ntdll extends Library {
    Ntdll INSTANCE = Native.load("ntdll", Ntdll.class);

    int RtlGetVersion(OSVERSIONINFOEXW versionInfo);
}
