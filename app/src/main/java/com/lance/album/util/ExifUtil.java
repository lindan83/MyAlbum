package com.lance.album.util;

import android.media.ExifInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lindan on 17-4-11.
 * 照片Exif信息获取工具
 * 目前Android SDK定义的Tag有:
 * TAG_DATETIME 时间日期
 * TAG_FLASH 闪光灯
 * TAG_GPS_LATITUDE 纬度
 * TAG_GPS_LATITUDE_REF 纬度参考
 * TAG_GPS_LONGITUDE 经度
 * TAG_GPS_LONGITUDE_REF 经度参考
 * TAG_IMAGE_LENGTH 图片长
 * TAG_IMAGE_WIDTH 图片宽
 * TAG_MAKE 设备制造商
 * TAG_MODEL 设备型号
 * TAG_ORIENTATION 方向
 * TAG_WHITE_BALANCE 白平衡
 */
public class ExifUtil {

    /**
     * 获取照片Exif信息
     *
     * @param path 照片文件路径
     * @return Map
     */
    public static Map<String, String> getImageExifInfo(String path) {
        Map<String, String> info = new HashMap<>();
        try {
            //android读取图片EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            String model = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
            String width = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            String height = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            String datetime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
            String flash = exifInterface.getAttribute(ExifInterface.TAG_FLASH);
            String latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String make = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
            String orientation = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
            String whiteBalance = exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
            String focalLength = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);

            info.put(ExifInterface.TAG_MODEL, model);
            info.put(ExifInterface.TAG_IMAGE_WIDTH, width);
            info.put(ExifInterface.TAG_IMAGE_LENGTH, height);
            info.put(ExifInterface.TAG_DATETIME, datetime);
            info.put(ExifInterface.TAG_FLASH, flash);
            info.put(ExifInterface.TAG_GPS_LATITUDE, latitude);
            info.put(ExifInterface.TAG_GPS_LONGITUDE, longitude);
            info.put(ExifInterface.TAG_MAKE, make);
            info.put(ExifInterface.TAG_ORIENTATION, orientation);
            info.put(ExifInterface.TAG_WHITE_BALANCE, whiteBalance);
            info.put(ExifInterface.TAG_FOCAL_LENGTH, focalLength);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                String iso = exifInterface.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS);
                String s = exifInterface.getAttribute(ExifInterface.TAG_SHUTTER_SPEED_VALUE);
                String f = exifInterface.getAttribute(ExifInterface.TAG_APERTURE_VALUE);
                String ev = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_BIAS_VALUE);
                info.put(ExifInterface.TAG_ISO_SPEED_RATINGS, iso);
                info.put(ExifInterface.TAG_SHUTTER_SPEED_VALUE, s);
                info.put(ExifInterface.TAG_APERTURE_VALUE, f);
                info.put(ExifInterface.TAG_EXPOSURE_BIAS_VALUE, ev);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }
}
