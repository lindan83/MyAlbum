package com.lance.album;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lance.album.adapter.PhotoViewAdapter;
import com.lance.album.bean.PhotoBean;
import com.lance.album.service.PhotoAlbumService;
import com.lance.common.util.ToastUtil;
import com.lance.common.widget.PhotoViewPager;
import com.lance.common.widget.dialog.DialogUtil;
import com.lance.common.widget.dialog.IDialog;
import com.lance.common.widget.photoview.PhotoViewAttacher;
import com.lance.common.widget.template.BaseActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 查看照片大图
 */
public class PhotoViewActivity extends BaseActivity implements PhotoViewAttacher.OnViewTapListener {
    private static final int MSG_SHOW_MENU = 100;
    public static final String PHOTO_LIST = "photo_list";
    public static final String START_INDEX = "start_index";
    public static final String RESULT_ID = "result_id";
    private TextView tvPhotoLabel;
    private LinearLayout llMenu;
    private View ivInfo;
    private View tvDelete, tvEdit, tvMenu;
    private View llPhotoInfo;
    private PhotoViewPager vpPhotoView;
    private PhotoViewAdapter photoViewAdapter;
    private int currentIndex;
    private List<PhotoBean> photoList;
    private boolean hasDelete;
    private ArrayList<String> deletedPhotoIds = new ArrayList<>();
    private MainHandler mainHandler;
    private boolean isMenuDisplayed;

    private static class MainHandler extends Handler {
        WeakReference<PhotoViewActivity> ref;

        MainHandler(PhotoViewActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_SHOW_MENU) {
                PhotoViewActivity activity = ref.get();
                if (activity != null) {
                    activity.resetMenu();
                }
            }
        }
    }


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
        tvPhotoLabel = getView(R.id.tv_photo_label);
        vpPhotoView = getView(R.id.vp_photo_view);
        llMenu = getView(R.id.ll_menu);
        tvDelete = getView(R.id.tv_delete);
        tvEdit = getView(R.id.tv_edit);
        tvMenu = getView(R.id.tv_menu);
        ivInfo = getView(R.id.iv_info);
        llPhotoInfo = getView(R.id.ll_photo_info);
    }

    @Override
    public void initEventListeners() {
        vpPhotoView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
                tvPhotoLabel.setText(photoList.get(currentIndex).label.label);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tvDelete.setOnClickListener(this);
        tvEdit.setOnClickListener(this);
        tvMenu.setOnClickListener(this);
        ivInfo.setOnClickListener(this);
    }

    @Override
    public void initData() {
        photoList = (List<PhotoBean>) getIntent().getSerializableExtra(PHOTO_LIST);
        if (photoList == null || photoList.isEmpty()) {
            ToastUtil.showShort(this, getString(R.string.app_error_arguments, "photoList is null or empty!"));
            finish();
            return;
        }
        currentIndex = getIntent().getIntExtra(START_INDEX, 0);
        photoViewAdapter = new PhotoViewAdapter(this, this);
        photoViewAdapter.setData(photoList);
        vpPhotoView.setAdapter(photoViewAdapter);
        vpPhotoView.setCurrentItem(currentIndex);
        tvPhotoLabel.setText(photoList.get(currentIndex).label.label);
        mainHandler = new MainHandler(this);
    }

    @Override
    public void processClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.tv_delete:
                showDeleteConfirm();
                break;
            case R.id.tv_edit:

                break;
            case R.id.tv_menu:

                break;
            case R.id.iv_info:
                showPhotoInfo();
                break;
        }
    }

    private void showDeleteConfirm() {
        DialogUtil.showConfirmDialog(
                this,
                "",
                getString(R.string.app_photo_dialog_confirm_context),
                getString(R.string.app_photo_delete),
                getString(R.string.str_widget_dialog_button_cancel),
                new IDialog.OnClickListener() {
                    @Override
                    public void onClick(IDialog iDialog, int i) {
                        iDialog.dismiss();
                        if (i == IDialog.BUTTON_POSITIVE) {
                            boolean result = PhotoAlbumService.getInstance().deletePhoto(PhotoViewActivity.this, photoList.get(currentIndex).id);
                            if (result) {
                                deletedPhotoIds.add(photoList.get(currentIndex).id);
                                photoList.remove(currentIndex);
                                if (currentIndex >= photoList.size()) {
                                    currentIndex = photoList.size() - 1;
                                }
                                photoViewAdapter.setData(photoList);
                                vpPhotoView.setCurrentItem(currentIndex);
                                hasDelete = true;
                            } else {
                                ToastUtil.showShort(PhotoViewActivity.this, R.string.app_photo_delete_fail);
                            }
                        }
                    }
                });
    }

    //显示照片信息
    private void showPhotoInfo() {
        llPhotoInfo.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(llPhotoInfo, "alpha", 0.0f, 1.0f).setDuration(500).start();
    }

    @Override
    public void onViewTap(View view, float v, float v1) {
        mainHandler.removeMessages(MSG_SHOW_MENU);
        showMenu();
    }

    private synchronized void showMenu() {
        if (!isMenuDisplayed) {
            isMenuDisplayed = true;
            tvPhotoLabel.setVisibility(View.GONE);
            llMenu.setVisibility(View.VISIBLE);
            ivInfo.setVisibility(View.VISIBLE);
            mainHandler.sendEmptyMessageDelayed(MSG_SHOW_MENU, 2000);
        } else {
            resetMenu();
        }
    }

    private synchronized void resetMenu() {
        tvPhotoLabel.setVisibility(View.VISIBLE);
        llMenu.setVisibility(View.GONE);
        ivInfo.setVisibility(View.GONE);
        isMenuDisplayed = false;
    }

    @Override
    public void onBackPressed() {
        if (hasDelete) {
            Intent data = new Intent();
            data.putStringArrayListExtra(RESULT_ID, deletedPhotoIds);
            setResult(RESULT_OK, data);
        }
        super.onBackPressed();
    }

    public static void showActivityForResult(Activity activity, List<PhotoBean> photoList, int startIndex, int requestCode) {
        if (photoList == null || photoList.isEmpty()) {
            ToastUtil.showShort(activity, activity.getString(R.string.app_error_arguments, "photoList is null or empty!"));
            return;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        Intent intent = new Intent(activity, PhotoViewActivity.class);
        intent.putExtra(PHOTO_LIST, new ArrayList<>(photoList));
        intent.putExtra(START_INDEX, startIndex);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void showActivityForResultFromFragment(Fragment fragment, List<PhotoBean> photoList, int startIndex, int requestCode) {
        if (photoList == null || photoList.isEmpty()) {
            ToastUtil.showShort(fragment.getActivity(), fragment.getActivity().getString(R.string.app_error_arguments, "photoList is null or empty!"));
            return;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        Intent intent = new Intent(fragment.getActivity(), PhotoViewActivity.class);
        intent.putExtra(PHOTO_LIST, new ArrayList<>(photoList));
        intent.putExtra(START_INDEX, startIndex);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void showActivity(Activity activity, List<PhotoBean> photoList, int startIndex) {
        if (photoList == null || photoList.isEmpty()) {
            ToastUtil.showShort(activity, activity.getString(R.string.app_error_arguments, "photoList is null or empty!"));
            return;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        Intent intent = new Intent(activity, PhotoViewActivity.class);
        intent.putExtra(PHOTO_LIST, new ArrayList<>(photoList));
        intent.putExtra(START_INDEX, startIndex);
        activity.startActivity(intent);
    }
}
