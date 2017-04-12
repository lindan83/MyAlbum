package com.lance.album.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lance.album.R;
import com.lance.album.bean.PhotoBean;
import com.lance.common.recyclerview.adapter.CommonRecyclerViewAdapter;
import com.lance.common.recyclerview.adapter.base.CommonRecyclerViewHolder;
import com.lance.common.util.ScreenUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lindan on 17-4-6.
 * 照片单项适配器
 */
public class PhotoGridAdapter extends CommonRecyclerViewAdapter<PhotoBean> {
    private int spanCount;
    private int margins;
    private Set<PhotoBean> selectedPhotos = new HashSet<>();
    private boolean isEditMode;

    public PhotoGridAdapter(Context context, List<PhotoBean> data, int spanCount, int margins) {
        super(context, R.layout.item_photo_grid, data);
        this.spanCount = spanCount;
        this.margins = margins;
    }

    public PhotoGridAdapter(Context context, int spanCount, int margins) {
        this(context, new ArrayList<PhotoBean>(), spanCount, margins);
    }

    @Override
    protected void convert(CommonRecyclerViewHolder holder, PhotoBean item, int position) {
        Glide.with(holder.getConvertView().getContext()).load(new File(item.path)).centerCrop().placeholder(R.mipmap.icon_notpic).error(R.mipmap.ic_tulie).into((ImageView) holder.getView(R.id.iv_photo));
        CheckBox checkBox = holder.getView(R.id.cb_check_button);
        if (selectedPhotos.contains(item)) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
        checkBox.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onViewHolderCreated(final CommonRecyclerViewHolder holder, View itemView) {
        int widthPixels = ScreenUtil.getScreenWidth(holder.itemView.getContext());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((widthPixels - margins) / spanCount, (widthPixels - margins) / spanCount);
        holder.getView(R.id.iv_photo).setLayoutParams(lp);

        CheckBox checkBox = holder.getView(R.id.cb_check_button);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PhotoBean selectedItem = getData().get(holder.getAdapterPosition());
                if (selectedPhotos.contains(selectedItem)) {
                    selectedPhotos.remove(selectedItem);
                } else {
                    selectedPhotos.add(selectedItem);
                }
                notifyDataSetChanged();
            }
        });
    }

    public void clearSelectedPhotos() {
        selectedPhotos.clear();
        notifyDataSetChanged();
    }

    public void addPhotoToSelected(PhotoBean photoBean) {
        if (!selectedPhotos.contains(photoBean)) {
            selectedPhotos.add(photoBean);
            notifyDataSetChanged();
        }
    }

    public void removePhotoFromSelected(PhotoBean photoBean) {
        if (selectedPhotos.contains(photoBean)) {
            selectedPhotos.remove(photoBean);
            notifyDataSetChanged();
        }
    }
}
