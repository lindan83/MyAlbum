package com.lance.album;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.lance.album.adapter.PhotoViewAdapter;
import com.lance.album.bean.PhotoBean;
import com.lance.common.util.ToastUtil;
import com.lance.common.widget.PhotoViewPager;
import com.lance.common.widget.photoview.PhotoViewAttacher;
import com.lance.common.widget.template.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 查看照片大图
 */
public class PhotoViewActivity extends BaseActivity implements PhotoViewAttacher.OnViewTapListener {
    private PhotoViewPager vpPhotoView;
    private int currentIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_photo_view;
    }

    @Override
    public void initViews() {
        vpPhotoView = getView(R.id.vp_photo_view);
        vpPhotoView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void initEventListeners() {

    }

    @Override
    public void initData() {
        List<PhotoBean> photoList = (List<PhotoBean>) getIntent().getSerializableExtra("photo_list");
        currentIndex = getIntent().getIntExtra("start_index", 0);
        if (photoList == null || photoList.isEmpty()) {
            ToastUtil.showShort(this, getString(R.string.app_error_arguments, "photoList is null or empty!"));
            finish();
            return;
        }
        PhotoViewAdapter photoViewAdapter = new PhotoViewAdapter(this, this);
        photoViewAdapter.setData(photoList);
        vpPhotoView.setAdapter(photoViewAdapter);
        vpPhotoView.setCurrentItem(currentIndex);
    }

    @Override
    public void processClick(View view) {

    }

    @Override
    public void onViewTap(View view, float v, float v1) {

    }

    public static void showActivity(Context context, List<PhotoBean> photoList, int startIndex) {
        if (photoList == null || photoList.isEmpty()) {
            ToastUtil.showShort(context, context.getString(R.string.app_error_arguments, "photoList is null or empty!"));
            return;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        Intent intent = new Intent(context, PhotoViewActivity.class);
        intent.putExtra("photo_list", new ArrayList<>(photoList));
        intent.putExtra("start_index", startIndex);
        context.startActivity(intent);
    }
}
