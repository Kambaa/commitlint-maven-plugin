package dev.kambaabi;

import java.util.List;

public class Utils {
    public static boolean isNonEmptyArray(List list) {
        return null != list && list.size() > 0;
    }

    public static boolean stringNotEmpty(String str) {
        return null != str && !str.equalsIgnoreCase("");
    }

}
