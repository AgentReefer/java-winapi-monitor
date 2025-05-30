package com.monitor.win;

import com.monitor.win.struct.WindowsStructures.*;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.ptr.LongByReference;

/**
 * JNA binding for kernel32.dll
 */
public interface Kernel32 extends Library {
    Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

    int GetDriveTypeW(WString lpRootPathName);

    void GetSystemInfo(SYSTEM_INFO result);

    boolean GlobalMemoryStatusEx(MEMORYSTATUSEX result);

    boolean GetDiskFreeSpaceExW(WString lpDirectoryName,
                                LongByReference lpFreeBytesAvailable,
                                LongByReference lpTotalNumberOfBytes,
                                LongByReference lpTotalNumberOfFreeBytes);

    boolean GetVersionExW(OSVERSIONINFOEXW result);
}
