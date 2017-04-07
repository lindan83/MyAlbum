package com.lance.album.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lindan on 17-4-7.
 * 相册Bean
 */
public class BucketBean implements Parcelable {
    public String bucketId;//相册ID
    public String bucketName;//相册名
    public List<PhotoBean> photoList = new ArrayList<>();//相册中图片列表

    public void addPhoto(PhotoBean photoBean) {
        if (!photoList.contains(photoBean)) {
            photoList.add(photoBean);
        }
    }

    public PhotoBean getPhoto(String id) {
        for (PhotoBean photo : photoList) {
            if (TextUtils.equals(id, photo.id)) {
                return photo;
            }
        }
        return null;
    }

    public int getPhotoIndex(String id) {
        for (int i = 0, count = photoList.size(); i < count; i++) {
            PhotoBean photo = photoList.get(i);
            if (TextUtils.equals(id, photo.id)) {
                return i;
            }
        }
        return -1;
    }

    public int getPhotoIndex(PhotoBean photoBean) {
        for (int i = 0, count = photoList.size(); i < count; i++) {
            PhotoBean photo = photoList.get(i);
            if (TextUtils.equals(photoBean.id, photo.id)) {
                return i;
            }
        }
        return -1;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bucketId);
        dest.writeString(this.bucketName);
        dest.writeTypedList(this.photoList);
    }

    public BucketBean() {
    }

    protected BucketBean(Parcel in) {
        this.bucketId = in.readString();
        this.bucketName = in.readString();
        this.photoList = in.createTypedArrayList(PhotoBean.CREATOR);
    }

    public static final Parcelable.Creator<BucketBean> CREATOR = new Parcelable.Creator<BucketBean>() {
        @Override
        public BucketBean createFromParcel(Parcel source) {
            return new BucketBean(source);
        }

        @Override
        public BucketBean[] newArray(int size) {
            return new BucketBean[size];
        }
    };
}
