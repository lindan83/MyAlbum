package com.lance.album.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.lance.album.R;
import com.lance.album.bean.PhotoBean;
import com.lance.common.widget.photoview.PhotoView;
import com.lance.common.widget.photoview.PhotoViewAttacher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lindan on 17-4-7.
 * 大图Adapter
 */
public class PhotoViewAdapter extends PagerAdapter {
    private List<PhotoBean> data = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;
    private PhotoViewAttacher.OnViewTapListener mOnViewTapListener;

    public void setData(List<PhotoBean> data) {
        if (data != null) {
            this.data = data;
            notifyDataSetChanged();
        }
    }

    public PhotoViewAdapter(Context context, PhotoViewAttacher.OnViewTapListener onViewTapListener) {
        this.context = context;
        this.mOnViewTapListener = onViewTapListener;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (data == null) return 0;
        return data.size();
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = inflater.inflate(R.layout.item_pager_photo, container, false);
        if (view != null) {
            final PhotoView imageView = (PhotoView) view.findViewById(R.id.pv_photo);
            imageView.setOnViewTapListener(this.mOnViewTapListener);

            //loading
            final ProgressBar loading = new ProgressBar(context);
            FrameLayout.LayoutParams loadingLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            loadingLayoutParams.gravity = Gravity.CENTER;
            loading.setLayoutParams(loadingLayoutParams);
            ((FrameLayout) view).addView(loading);

            final String path = data.get(position).path;

            Glide.with(context)
                    .load(new File(path))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存多个尺寸
                    .thumbnail(0.1f)//先显示缩略图  缩略图为原图的1/10
                    .error(R.mipmap.notpic)
                    .into(new GlideDrawableImageViewTarget(imageView) {
                        @Override
                        public void onLoadStarted(Drawable placeholder) {
                            super.onLoadStarted(placeholder);
                            loading.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            loading.setVisibility(View.GONE);
                        }

                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                            super.onResourceReady(resource, animation);
                            loading.setVisibility(View.GONE);
                        }
                    });

            container.addView(view, 0);
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}