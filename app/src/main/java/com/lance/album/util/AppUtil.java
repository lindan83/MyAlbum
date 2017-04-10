package com.lance.album.util;

/**
 * Created by lindan on 17-4-10.
 */

public class AppUtil {
    public static final String TB = "TB";
    public static final String GB = "GB";
    public static final String MB = "MB";
    public static final String KB = "KB";
    public static final String BYTE = "byte";

    public static String calculateFileSize(long bytes) {
        double bytesInDouble = bytes;
        if (bytesInDouble > 1024d * 1024 * 1024 * 1024) {
            return bytesInDouble / (1024d * 1024 * 1024 * 1024) + TB;
        } else if (bytesInDouble > 1024d * 1024 * 1024) {
            return bytesInDouble / (1024d * 1024 * 1024) + GB;
        } else if (bytesInDouble > 1024d * 1024) {
            return bytesInDouble / (1024d * 1024) + MB;
        } else if (bytesInDouble > 1024) {
            return bytesInDouble / 1024d + KB;
        } else {
            return bytesInDouble + BYTE;
        }
    }
}
