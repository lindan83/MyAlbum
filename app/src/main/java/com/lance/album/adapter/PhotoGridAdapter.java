package com.lance.album.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lance.album.R;
import com.lance.album.bean.PhotoBean;
import com.lance.album.fragment.PhotoFragment;
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
    private PhotoFragment photoFragment;

    public PhotoGridAdapter(PhotoFragment photoFragment, List<PhotoBean> data, int spanCount, int margins) {
        super(photoFragment.getActivity(), R.layout.item_photo_grid, data);
        this.spanCount = spanCount;
        this.margins = margins;
        this.photoFragment = photoFragment;
    }

    public PhotoGridAdapter(PhotoFragment photoFragment, int spanCount, int margins) {
        this(photoFragment, new ArrayList<PhotoBean>(), spanCount, margins);
    }

    @Override
    protected void convert(CommonRecyclerViewHolder holder, PhotoBean item, int position) {
        Glide.with(holder.getConvertView().getContext()).load(new File(item.path)).centerCrop().placeholder(R.mipmap.icon_notpic).error(R.mipmap.ic_tulie).into((ImageView) holder.getView(R.id.iv_photo));
        CheckBox checkBox = holder.getView(R.id.cb_check_button);
        boolean isEditMode = photoFragment.isEditMode();
        if (isEditMode) {
            checkBox.setChecked(selectedPhotos.contains(item));
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewHolderCreated(final CommonRecyclerViewHolder holder, View itemView) {
        int widthPixels = ScreenUtil.getScreenWidth(holder.itemView.getContext());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((widthPixels - margins) / spanCount, (widthPixels - margins) / spanCount);
        holder.getView(R.id.iv_photo).setLayoutParams(lp);

        final CheckBox checkBox = holder.getView(R.id.cb_check_button);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PhotoBean selectedItem = getData().get(holder.getAdapterPosition());
                if (isChecked) {
                    if (!selectedPhotos.contains(selectedItem)) {
                        selectedPhotos.add(selectedItem);
                    }
                } else {
                    if (selectedPhotos.contains(selectedItem)) {
                        selectedPhotos.remove(selectedItem);
                    }
                }
                photoFragment.notifyPhotoSelectedCountChange();
            }
        });
    }

    /**
     * 清除所有选中项
     */
    public void clearSelectedPhotos() {
        selectedPhotos.clear();
        notifyDataSetChanged();
    }

    /**
     * 将某一照片加入到选中项集合中
     *
     * @param photoBean 照片
     */
    public void addPhotoToSelected(PhotoBean photoBean) {
        if (!selectedPhotos.contains(photoBean)) {
            selectedPhotos.add(photoBean);
            notifyDataSetChanged();
        }
    }

    /**
     * 将某一照片从选中项集合中移除
     *
     * @param photoBean 照片
     */
    public void removePhotoFromSelected(PhotoBean photoBean) {
        if (selectedPhotos.contains(photoBean)) {
            selectedPhotos.remove(photoBean);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取选中照片集合
     *
     * @return 集合
     */
    public Set<PhotoBean> getSelectedPhotos() {
        return selectedPhotos;
    }
}
