package com.monitor.win;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface User32 extends Library {
    User32 INSTANCE = Native.load("user32", User32.class);

    int MessageBoxA(int hWnd, String lpText, String lpCaption, int uType);

    static void showMessageBox(String title, String message) {
        INSTANCE.MessageBoxA(0, message, title, 0);
    }
}
