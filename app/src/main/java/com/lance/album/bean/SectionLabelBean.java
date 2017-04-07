package com.lance.album.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lindan on 17-4-6.
 * 悬浮头Section Bean
 */
public class SectionLabelBean implements Parcelable {
    public String label;

    public SectionLabelBean(String label) {
        this.label = label;
    }

    public SectionLabelBean() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SectionLabelBean that = (SectionLabelBean) o;

        return label != null ? label.equals(that.label) : that.label == null;

    }

    @Override
    public int hashCode() {
        return label != null ? label.hashCode() : 0;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.label);
    }

    protected SectionLabelBean(Parcel in) {
        this.label = in.readString();
    }

    public static final Parcelable.Creator<SectionLabelBean> CREATOR = new Parcelable.Creator<SectionLabelBean>() {
        @Override
        public SectionLabelBean createFromParcel(Parcel source) {
            return new SectionLabelBean(source);
        }

        @Override
        public SectionLabelBean[] newArray(int size) {
            return new SectionLabelBean[size];
        }
    };
}
