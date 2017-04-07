package com.lance.album.adapter;

import android.content.Context;
import android.view.View;
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
import java.util.List;

/**
 * Created by lindan on 17-4-6.
 * 照片单项适配器
 */
public class PhotoGridAdapter extends CommonRecyclerViewAdapter<PhotoBean> {
    private int spanCount;
    private int margins;

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
        //GlideImageLoader.loadFile((ImageView) holder.getView(R.id.iv_photo), new File(item.path));
        Glide.with(holder.getConvertView().getContext()).load(new File(item.path)).centerCrop().into((ImageView) holder.getView(R.id.iv_photo));
    }

    @Override
    public void onViewHolderCreated(CommonRecyclerViewHolder holder, View itemView) {
        int widthPixels = ScreenUtil.getScreenWidth(holder.itemView.getContext());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((widthPixels - margins) / spanCount, (widthPixels - margins) / spanCount);
        holder.getView(R.id.iv_photo).setLayoutParams(lp);
    }
}
