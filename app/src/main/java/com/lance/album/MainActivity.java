package com.lance.album;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lance.album.adapter.AlbumPagerAdapter;
import com.lance.album.config.AppConfig;
import com.lance.album.fragment.BucketFragment;
import com.lance.album.fragment.PhotoFragment;
import com.lance.album.service.PhotoBucketService;
import com.lance.album.util.AppUtil;
import com.lance.album.widget.InputBucketNameDialog;
import com.lance.album.widget.MyViewPager;
import com.lance.common.util.SPUtil;
import com.lance.common.util.ToastUtil;
import com.lance.common.widget.dialog.IDialog;
import com.lance.common.widget.template.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private MyViewPager vpMain;
    private TabLayout tabMain;
    private PhotoFragment photoFragment;
    private BucketFragment bucketFragment;

    //操作栏相关
    private RelativeLayout rlOperationBar;
    private TextView tvSelected;
    private ImageView ivClose;

    //底部菜单相关
    private LinearLayout bottomMenu, editMenu;
    private TextView tvCreateBucket, tvMenu;
    private TextView tvShare, tvMoveInto, tvDelete, tvSelectAll, tvEditMenu;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews() {
        vpMain = getView(R.id.vp_main);
        tabMain = getView(R.id.tab_main);
        tvCreateBucket = getView(R.id.tv_bucket_create);
        tvMenu = getView(R.id.tv_menu);
        vpMain.setCurrentItem(0);
        tvCreateBucket.setVisibility(View.GONE);

        //菜单相关
        bottomMenu = getView(R.id.ll_bottom_menu);
        editMenu = getView(R.id.ll_edit_menu);
        tvShare = getView(R.id.tv_share);
        tvMoveInto = getView(R.id.tv_move_into);
        tvDelete = getView(R.id.tv_delete);
        tvSelectAll = getView(R.id.tv_select_all);
        tvEditMenu = getView(R.id.tv_edit_menu);

        //操作栏相关
        rlOperationBar = getView(R.id.rl_operation_bar);
        tvSelected = getView(R.id.tv_selected);
        ivClose = getView(R.id.iv_close);
    }

    @Override
    public void initEventListeners() {
        tvCreateBucket.setOnClickListener(this);
        vpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    tvCreateBucket.setVisibility(View.GONE);
                } else {
                    tvCreateBucket.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //操作栏相关
        ivClose.setOnClickListener(this);

        //菜单相关
        tvMenu.setOnClickListener(this);
        tvShare.setOnClickListener(this);
        tvMoveInto.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        tvSelectAll.setOnClickListener(this);
        tvEditMenu.setOnClickListener(this);
    }

    @Override
    public void initData() {
        List<Fragment> fragments = new ArrayList<>(2);
        fragments.add(photoFragment = PhotoFragment.newFragment(getString(R.string.app_pager_title_photo)));
        fragments.add(bucketFragment = BucketFragment.newFragment(getString(R.string.app_pager_title_album)));
        AlbumPagerAdapter pagerAdapter = new AlbumPagerAdapter(this, fragments);
        vpMain.setAdapter(pagerAdapter);
        vpMain.setPageMargin(6);
        tabMain.setupWithViewPager(vpMain);
    }

    @Override
    public void processClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.tv_bucket_create:
                showBucketDialog();
                break;
            case R.id.tv_menu:
                // TODO: 17-4-11
                break;
            case R.id.iv_close:
                showOperationBar(false, 0);
                if (vpMain.getCurrentItem() == 0) {
                    photoFragment.setEditMode(false);
                } else if (vpMain.getCurrentItem() == 1) {
                    bucketFragment.setEditMode(false);
                }
                break;
            case R.id.tv_share:

                break;
            case R.id.tv_move_into:

                break;
            case R.id.tv_delete:

                break;
            case R.id.tv_select_all:

                break;
            case R.id.tv_edit_menu:

                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (rlOperationBar.getVisibility() == View.VISIBLE) {
            showOperationBar(false, 0);
            if (vpMain.getCurrentItem() == 0) {
                photoFragment.setEditMode(false);
            } else if (vpMain.getCurrentItem() == 1) {
                bucketFragment.setEditMode(false);
            }
            return;
        }
        super.onBackPressed();
    }

    //显示新建相册对话框
    private void showBucketDialog() {
        final InputBucketNameDialog dialog = new InputBucketNameDialog(this);
        dialog.setTitle(R.string.app_bottom_menu_bucket_create);
        dialog.setMessage(R.string.app_bottom_menu_bucket_create);
        dialog.setPositiveButton(R.string.str_widget_dialog_button_ok);
        dialog.setNegativeButton(R.string.str_widget_dialog_button_cancel);
        dialog.setOnClickListener(new IDialog.OnClickListener() {
            @Override
            public void onClick(IDialog iDialog, int i) {
                iDialog.dismiss();
                if (i == IDialog.BUTTON_POSITIVE) {
                    String bucketName = dialog.getMessage();
                    if (TextUtils.isEmpty(bucketName)) {
                        ToastUtil.showShort(MainActivity.this, R.string.app_bucket_name_null);
                        dialog.requestFocus();
                        return;
                    }
                    boolean result = PhotoBucketService.getInstance().createNewBucket(MainActivity.this, bucketName);
                    if (result) {
                        ToastUtil.showShort(MainActivity.this, R.string.app_bucket_create_success);
                        saveNewBucketToSP(bucketName);
                        bucketFragment.refreshBucketList();
                    } else {
                        ToastUtil.showShort(MainActivity.this, R.string.app_bucket_create_fail);
                    }
                }
            }
        });
        dialog.show();
    }

    //将新建的相册保存到SharePreferences中
    private void saveNewBucketToSP(String bucketName) {
        String toSaveValue = AppUtil.saveNewBucketsToString((String) SPUtil.get(this, AppConfig.SP_KEY_BUCKET_USER_CREATE, ""), bucketName);
        SPUtil.put(this, AppConfig.SP_KEY_BUCKET_USER_CREATE, toSaveValue);
    }

    /**
     * 显示操作栏
     *
     * @param display           是否显示
     * @param selectedItemCount 选中项个数
     */
    public synchronized void showOperationBar(boolean display, int selectedItemCount) {
        boolean displayOld = rlOperationBar.getVisibility() == View.VISIBLE;
        if (displayOld != display) {
            if (display) {
                vpMain.setScrollable(false);
                tabMain.setVisibility(View.GONE);
                rlOperationBar.setVisibility(View.VISIBLE);
                tvSelected.setText(getString(R.string.app_operation_bar_title, selectedItemCount));
                tvSelected.setTag(selectedItemCount);
            } else {
                vpMain.setScrollable(true);
                tabMain.setVisibility(View.VISIBLE);
                rlOperationBar.setVisibility(View.GONE);
                tvSelected.setTag(0);
            }
            switchMenu(display);
        } else {
            if (display) {
                vpMain.setScrollable(false);
                tvSelected.setText(getString(R.string.app_operation_bar_title, selectedItemCount));
                tvSelected.setTag(selectedItemCount);
            } else {
                vpMain.setScrollable(true);
                tvSelected.setTag(0);
            }
        }
    }

    /**
     * 切换菜单显示
     *
     * @param editMode 是否编辑模式
     */
    public void switchMenu(boolean editMode) {
        if (editMode) {
            editMenu.setVisibility(View.VISIBLE);
            bottomMenu.setVisibility(View.GONE);
        } else {
            editMenu.setVisibility(View.GONE);
            bottomMenu.setVisibility(View.VISIBLE);
        }
    }
}
