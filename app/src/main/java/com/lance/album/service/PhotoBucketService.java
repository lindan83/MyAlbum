package com.lance.album.service;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.lance.album.bean.AddressBean;
import com.lance.album.bean.BucketBean;
import com.lance.album.bean.PhotoBean;
import com.lance.album.bean.SectionLabelBean;
import com.lance.common.util.DateUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lindan on 17-4-5.
 * 相册照片查询业务类
 */
public class PhotoBucketService {
    private static PhotoBucketService instance;

    /**
     * 获取唯一实例
     *
     * @return PhotoAlbumService对象
     */
    public static PhotoBucketService getInstance() {
        if (instance == null) {
            synchronized (PhotoBucketService.class) {
                if (instance == null) {
                    instance = new PhotoBucketService();
                }
            }
        }
        return instance;
    }

    /**
     * 获取本机照片，并以创建日期进行分类
     *
     * @param context Context
     * @return 照片集合
     */
    public synchronized Map<SectionLabelBean, List<PhotoBean>> getPhotoMap(Context context) {
        String[] projections = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.LONGITUDE,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.DATE_ADDED
        };
        HashMap<SectionLabelBean, List<PhotoBean>> photoMap = new HashMap<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projections,
                null,
                null,
                null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        PhotoBean photoBean = new PhotoBean();
                        photoBean.id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                        photoBean.name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        photoBean.path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        AddressBean addressBean = new AddressBean();
                        addressBean.longitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE));
                        addressBean.latitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE));
                        photoBean.address = addressBean;
                        photoBean.bucketId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
                        photoBean.date = new Date(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)) * 1000);
                        photoBean.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                        photoBean.label = new SectionLabelBean(getDateLabel(photoBean.date));
                        List<PhotoBean> photoList = photoMap.get(photoBean.label);
                        if (photoList == null) {
                            photoList = new ArrayList<>();
                            photoMap.put(photoBean.label, photoList);
                        }
                        photoList.add(photoBean);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return photoMap;
    }

    /**
     * 获取本机照片，并以创建日期进行分类
     *
     * @param photoMap 照片集合
     * @return 照片集合
     */
    public synchronized List<List<PhotoBean>> getPhotoList(Map<SectionLabelBean, List<PhotoBean>> photoMap) {
        List<List<PhotoBean>> photoList = new ArrayList<>();
        if (photoMap != null && !photoMap.isEmpty()) {
            List<SectionLabelBean> labelList = new ArrayList<>(photoMap.keySet());
            Collections.sort(labelList, new Comparator<SectionLabelBean>() {
                @Override
                public int compare(SectionLabelBean o1, SectionLabelBean o2) {
                    return o1.label.compareTo(o2.label);
                }
            });
            for (SectionLabelBean label : labelList) {
                List<PhotoBean> photoBeanList = photoMap.get(label);
                photoList.add(photoBeanList);
            }
        }
        return photoList;
    }

    /**
     * 合并照片集合，并以创建日期进行分类
     *
     * @param photoMap 照片集合
     * @return 照片集合
     */
    public synchronized List<PhotoBean> mergePhotoList(Map<SectionLabelBean, List<PhotoBean>> photoMap) {
        List<PhotoBean> photoList = new ArrayList<>();
        if (photoMap != null && !photoMap.isEmpty()) {
            Set<SectionLabelBean> keySet = photoMap.keySet();
            for (SectionLabelBean label : keySet) {
                List<PhotoBean> photoBeanList = photoMap.get(label);
                photoList.addAll(photoBeanList);
            }
        }
        Collections.sort(photoList, new Comparator<PhotoBean>() {
            @Override
            public int compare(PhotoBean o1, PhotoBean o2) {
                return o1.label.label.compareTo(o2.label.label);
            }
        });
        return photoList;
    }

    /**
     * 合并照片集合，并以创建日期进行分类
     *
     * @param photoList 照片集合
     * @return 照片集合
     */
    public synchronized List<PhotoBean> mergePhotoList(List<List<PhotoBean>> photoList) {
        List<PhotoBean> photoBeanList = new ArrayList<>();
        if (photoList != null && !photoList.isEmpty()) {
            for (List<PhotoBean> item : photoList) {
                photoBeanList.addAll(item);
            }
        }
        return photoBeanList;
    }

    /**
     * 获取有序照片集合的Label集合
     *
     * @param photoList 照片集合
     * @return Label集合
     */
    public synchronized List<SectionLabelBean> getPhotoLabelList(List<List<PhotoBean>> photoList) {
        List<SectionLabelBean> labelList = new ArrayList<>();
        if (photoList != null && !photoList.isEmpty()) {
            for (List<PhotoBean> photoBeanList : photoList) {
                if (photoBeanList != null && !photoBeanList.isEmpty()) {
                    labelList.add(photoBeanList.get(0).label);
                } else {
                    labelList.add(new SectionLabelBean("-1"));
                }
            }
        }
        return labelList;
    }

    /**
     * 获取有序照片集合的Label集合
     *
     * @param photoList 照片集合
     * @return Label集合
     */
    public synchronized List<SectionLabelBean> mergePhotoLabelList(List<PhotoBean> photoList) {
        List<SectionLabelBean> labelList = new ArrayList<>();
        if (photoList != null && !photoList.isEmpty()) {
            for (PhotoBean photoBean : photoList) {
                if (photoBean != null && photoBean.label != null) {
                    labelList.add(photoBean.label);
                } else {
                    labelList.add(new SectionLabelBean("-1"));
                }
            }
        }
        return labelList;
    }

    public synchronized int getPhotoPosition(List<PhotoBean> photoList, String photoId) {
        if (photoList == null || photoList.isEmpty()) {
            return -1;
        }
        for (int i = 0, count = photoList.size(); i < count; i++) {
            PhotoBean photoBean = photoList.get(i);
            if (TextUtils.equals(photoBean.id, photoId)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取日期对应的年/月/日字符串
     *
     * @param date Date对象
     * @return yyyy/M/d
     */
    private String getDateLabel(Date date) {
        if (date == null) {
            return "";
        }
        return DateUtil.getDate(date, "yyyy/M/d");
    }


    /**
     * 获取相册列表
     *
     * @param context Context
     * @return 相册列表
     */
    public synchronized List<BucketBean> getBucketList(Context context) {
        //HashMap作为临时容器，以相册名为键来对图片分类。最后转换会为List
        HashMap<String, BucketBean> bucketMap = new HashMap<>();
        //新建查询列
        String[] projections = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };
        //新建查询
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projections, null, null, MediaStore.Images.Media.DATE_ADDED + " desc");
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        String bucketID = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
                        String bucketName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                        //得到相册
                        BucketBean bucket = bucketMap.get(bucketID);
                        //如果没有该相册，则新建一个
                        if (bucket == null) {
                            bucket = new BucketBean();
                            bucket.bucketId = bucketID;
                            bucket.bucketName = bucketName;
                            bucketMap.put(bucketID, bucket);
                        }
                        //更新相册
                        PhotoBean photo = new PhotoBean();
                        photo.id = id;
                        photo.path = path;
                        photo.bucketId = bucketID;
                        photo.bucket = bucket;
                        bucket.addPhoto(photo);
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        //HashMap转List
        List<BucketBean> bucketList = new ArrayList<>();
        for (Map.Entry<String, BucketBean> entry : bucketMap.entrySet()) {
            bucketList.add(entry.getValue());
        }
        return bucketList;
    }

    /**
     * 获取指定相册中的照片
     *
     * @param context  Context
     * @param bucketId 相册ID
     * @return 照片列表
     */
    public synchronized List<PhotoBean> getPhotoList(Context context, String bucketId, String bucketName) {
        String[] projections = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.LONGITUDE,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.DATE_ADDED
        };
        List<PhotoBean> photoList = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projections,
                MediaStore.Images.Media.BUCKET_ID + "=? OR " + MediaStore.Images.Media.BUCKET_DISPLAY_NAME + "=?",
                new String[]{bucketId, bucketName},
                null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        PhotoBean photoBean = new PhotoBean();
                        photoBean.id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                        photoBean.name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        photoBean.path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        AddressBean addressBean = new AddressBean();
                        addressBean.longitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE));
                        addressBean.latitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE));
                        photoBean.address = addressBean;
                        photoBean.bucketId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
                        photoBean.date = new Date(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)) * 1000);
                        photoBean.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                        photoBean.label = new SectionLabelBean(getDateLabel(photoBean.date));
                        photoList.add(photoBean);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return photoList;
    }

    /**
     * 删除指定照片
     *
     * @param context Context
     * @param id      照片ID
     * @return true表示成功 false表示失败
     */
    public synchronized boolean deletePhoto(Context context, String id) {
        int count = context.getContentResolver().delete(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Images.Media._ID + "=?",
                new String[]{id});
        return count == 1;
    }

    /**
     * 查询相册是否已存在
     *
     * @param context    Context
     * @param bucketName 相册名称
     * @return exist == true  not exists == false
     */
    public synchronized boolean bucketExists(Context context, String bucketName) {
        boolean exists = false;
        //新建查询列
        String[] projections = new String[]{
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };
        //新建查询
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projections, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                        if (TextUtils.equals(bucketName, name)) {
                            exists = true;
                            break;
                        }
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return exists;
    }

    /**
     * 创建新相册
     *
     * @param bucketName 相册名称
     * @return boolean
     */
    public synchronized boolean createNewBucket(Context context, String bucketName) {
        if (bucketExists(context, bucketName)) {
            return false;
        }
        File pictureDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File bucketDir = new File(pictureDir, bucketName);
        if (bucketDir.exists()) {
            return true;
        }
        return bucketDir.mkdirs();
    }
}
