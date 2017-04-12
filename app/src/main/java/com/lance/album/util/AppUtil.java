package com.lance.album.util;

import android.text.TextUtils;

import com.lance.album.bean.BucketBean;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lindan on 17-4-10.
 */
public class AppUtil {
    /**
     * 计算文件字节大小，返回合适的数值单位字符串
     *
     * @param bytes 总字节数
     * @return Size String
     */
    public static String calculateFileSize(long bytes) {
        double bytesInDouble = bytes;
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2);
        if (bytesInDouble > 1024d * 1024 * 1024 * 1024) {
            return numberFormat.format(bytesInDouble / (1024d * 1024 * 1024 * 1024)) + "TB";
        } else if (bytesInDouble > 1024d * 1024 * 1024) {
            return numberFormat.format(bytesInDouble / (1024d * 1024 * 1024)) + "GB";
        } else if (bytesInDouble > 1024d * 1024) {
            return numberFormat.format(bytesInDouble / (1024d * 1024)) + "MB";
        } else if (bytesInDouble > 1024) {
            return numberFormat.format(bytesInDouble / 1024d) + "KB";
        } else {
            return bytes + "byte";
        }
    }

    /**
     * 将保存在SharePreferences中的用户创建相册信息字符串转换为实体集合
     * 由于用户创建相册只是建立一个空文件夹，如果没有图片存储在其中，不会被系统相册侦测到，所以建立相册时需要自行保存
     */
    public static List<BucketBean> obtainUserCreatedBuckets(String value) {
        //格式  bucketId=-1&bucketName=xxx,...
        List<BucketBean> bucketList = new ArrayList<>();
        if (TextUtils.isEmpty(value)) {
            return bucketList;
        }
        String[] itemArray = value.split(",");
        if (itemArray.length > 0) {
            for (int i = 0; i < itemArray.length; i++) {
                String[] itemValues = itemArray[i].split("&");
                if (itemValues.length == 0) {
                    continue;
                }
                BucketBean bucketBean = new BucketBean();
                for (int j = 0; j < itemValues.length; j++) {
                    String[] propertyItems = itemValues[j].split("=");
                    if (propertyItems.length != 2) {
                        break;
                    }
                    if (TextUtils.equals("bucketId", propertyItems[0])) {
                        bucketBean.bucketId = propertyItems[1];
                    } else if (TextUtils.equals("bucketName", propertyItems[0])) {
                        bucketBean.bucketName = propertyItems[1];
                    }
                }
                bucketList.add(bucketBean);
            }
        }
        return bucketList;
    }

    /**
     * 保存用户新建相册信息到SharePreferences中
     */
    public static String saveNewBucketsToString(String existBucketStrings, String newBucketName) {
        //格式  bucketId=-1&bucketName=xxx,...
        //如果已有同名相册，直接返回原值
        if (!TextUtils.isEmpty(existBucketStrings) && existBucketStrings.contains("bucketName=" + newBucketName)) {
            return existBucketStrings;
        }
        StringBuilder value = new StringBuilder(100);
        if (!TextUtils.isEmpty(existBucketStrings)) {
            value.append(existBucketStrings).append(",");
        }
        value.append("bucketId=-1&bucketName=").append(newBucketName);
        return value.toString();
}
}
