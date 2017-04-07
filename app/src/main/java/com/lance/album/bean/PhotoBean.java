package com.lance.album.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by lindan on 17-4-5.
 * 照片实体
 */
public class PhotoBean implements Parcelable {
    public String id;
    public String name;
    public Date date;
    public AddressBean address;
    public String path;
    public String bucketId;
    public BucketBean bucket;
    public SectionLabelBean label;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeParcelable(this.address, flags);
        dest.writeString(this.path);
        dest.writeString(bucketId);
        dest.writeParcelable(this.bucket, flags);
        dest.writeParcelable(this.label, flags);
    }

    public PhotoBean() {
    }

    protected PhotoBean(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.address = in.readParcelable(AddressBean.class.getClassLoader());
        this.path = in.readString();
        this.bucketId = in.readString();
        this.bucket = in.readParcelable(BucketBean.class.getClassLoader());
        this.label = in.readParcelable(SectionLabelBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<PhotoBean> CREATOR = new Parcelable.Creator<PhotoBean>() {
        @Override
        public PhotoBean createFromParcel(Parcel source) {
            return new PhotoBean(source);
        }

        @Override
        public PhotoBean[] newArray(int size) {
            return new PhotoBean[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhotoBean photoBean = (PhotoBean) o;

        return id != null ? id.equals(photoBean.id) : photoBean.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
