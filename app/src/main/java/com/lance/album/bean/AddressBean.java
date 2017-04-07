package com.lance.album.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lindan on 17-4-5.
 * 地址Bean
 */
public class AddressBean implements Parcelable {
    /**
     * 经度
     */
    public double longitude;
    /**
     * 纬度
     */
    public double latitude;
    /**
     * 地址描述
     */
    public String address;

    public AddressBean(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public AddressBean() {

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
        dest.writeString(this.address);
    }

    protected AddressBean(Parcel in) {
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.address = in.readString();
    }

    public static final Parcelable.Creator<AddressBean> CREATOR = new Parcelable.Creator<AddressBean>() {
        @Override
        public AddressBean createFromParcel(Parcel source) {
            return new AddressBean(source);
        }

        @Override
        public AddressBean[] newArray(int size) {
            return new AddressBean[size];
        }
    };
}
