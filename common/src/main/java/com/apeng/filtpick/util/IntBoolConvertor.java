package com.apeng.filtpick.util;

public class IntBoolConvertor {
    public static boolean toBool(int i) {
        if (i != 0 && i != 1) {
            throw new RuntimeException("Conversion Error: int is neither 0 or 1");
        }
        return i == 1;
    }

    public static int toInt(boolean b) {
        if (b) return 1;
        return 0;
    }

}
