package com.lance.album.fragment;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lance.album.MainActivity;
import com.lance.album.PhotoListActivity;
import com.lance.album.R;
import com.lance.album.bean.BucketBean;
import com.lance.album.config.AppConfig;
import com.lance.album.service.PhotoBucketService;
import com.lance.album.util.AppUtil;
import com.lance.common.recyclerview.adapter.AbstractRecyclerViewAdapter;
import com.lance.common.recyclerview.adapter.CommonRecyclerViewAdapter;
import com.lance.common.recyclerview.adapter.base.CommonRecyclerViewHolder;
import com.lance.common.util.SDCardUtil;
import com.lance.common.util.SPUtil;
import com.lance.common.util.ToastUtil;
import com.lance.common.widget.template.BaseFragment;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by lindan on 17-4-5.
 * 相册Fragment
 */
public class BucketFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks {
    private static final int RC_EXTERNAL_STORAGE = 100;//请求码，外部存储器
    private RecyclerView rvAlbum;
    private CommonRecyclerViewAdapter<BucketBean> adapter;
    private List<BucketBean> bucketList = new ArrayList<>();
    private boolean isEditMode;
    private Set<BucketBean> selectedBuckets = new HashSet<>();

    @Override
    public int getLayoutId() {
        return R.layout.fragment_photo;
    }

    @Override
    public void initViews() {
        rvAlbum = getView(R.id.rv_photo);
        rvAlbum.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvAlbum.setItemAnimator(new DefaultItemAnimator());
        rvAlbum.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void initEventListeners() {

    }

    @Override
    public void initData() {
        createAdapter(bucketList);
        rvAlbum.setAdapter(adapter);
        if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            getPhotoMap();
        } else {
            if (SDCardUtil.isSDCardEnable()) {
                EasyPermissions.requestPermissions(this,
                        getString(R.string.app_permission_request_external_storage_access),
                        RC_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
                ToastUtil.showShort(getActivity(), R.string.app_external_storage_device_unmounted);
            }
        }
    }

    //读取用户创始的相册
    //由于用户创建相册只是建立一个空文件夹，如果没有图片存储在其中，不会被系统相册侦测到，所以建立相册时需要自行保存
    private List<BucketBean> obtainUserCreateBuckets() {
        //格式  bucketId=-1&bucketName=xxx,...
        return AppUtil.obtainUserCreatedBuckets((String) SPUtil.get(getActivity(), AppConfig.SP_KEY_BUCKET_USER_CREATE, ""));
    }

    private void getPhotoMap() {
        new BucketTask(this).execute();
    }

    @Override
    public void processClick(View v) {

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (perms != null && perms.size() > 0) {
            new BucketTask(this).execute();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        ToastUtil.showShort(getActivity(), R.string.app_permission_request_external_storage_access_deny_msg);
    }

    //获取相册列表Task
    private static class BucketTask extends AsyncTask<Void, Integer, List<BucketBean>> {
        WeakReference<BucketFragment> ref;

        BucketTask(BucketFragment fragment) {
            ref = new WeakReference<>(fragment);
        }

        @Override
        protected List<BucketBean> doInBackground(Void... params) {
            BucketFragment fragment = ref.get();
            if (fragment != null) {
                return PhotoBucketService.getInstance().getBucketList(fragment.getActivity());
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<BucketBean> result) {
            if (result != null && !result.isEmpty()) {
                BucketFragment fragment = ref.get();
                if (fragment != null) {
                    fragment.bucketList.clear();
                    fragment.bucketList.addAll(result);
                    List<BucketBean> bucketListUserCreated = fragment.obtainUserCreateBuckets();
                    if (!bucketListUserCreated.isEmpty()) {
                        fragment.bucketList.addAll(bucketListUserCreated);
                    }
                    if (fragment.adapter == null) {
                        fragment.createAdapter(fragment.bucketList);
                        fragment.rvAlbum.setAdapter(fragment.adapter);
                    } else {
                        fragment.adapter.setData(fragment.bucketList);
                    }
                }
            }
        }
    }

    private void createAdapter(List<BucketBean> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        adapter = new CommonRecyclerViewAdapter<BucketBean>(getContext(), R.layout.item_bucket, data) {
            @Override
            protected void convert(CommonRecyclerViewHolder holder, BucketBean item, int position) {
                CheckBox checkBox = holder.getView(R.id.cb_check_button);
                ImageView ivEnter = holder.getView(R.id.iv_enter);
                ImageView ivBucket = holder.getView(R.id.iv_bucket);
                if (isEditMode) {
                    checkBox.setVisibility(View.VISIBLE);
                    ivEnter.setVisibility(View.GONE);
                    if (selectedBuckets.contains(item)) {
                        checkBox.setChecked(true);
                    } else {
                        checkBox.setChecked(false);
                    }
                } else {
                    checkBox.setVisibility(View.GONE);
                    ivEnter.setVisibility(View.VISIBLE);
                }
                String path = item.photoList.isEmpty() ? "" : item.photoList.get(0).path;
                if (TextUtils.isEmpty(path)) {
                    Glide.with(BucketFragment.this).load(R.mipmap.icon_notpic).centerCrop().into(ivBucket);
                } else {
                    Glide.with(BucketFragment.this).load(new File(path)).placeholder(R.mipmap.icon_notpic).error(R.mipmap.icon_notpic).centerCrop().into(ivBucket);
                }
                holder.setText(R.id.tv_bucket_name, item.bucketName);
                holder.setText(R.id.tv_bucket_photo_count, getString(R.string.app_bucket_photo_count, item.photoList.size()));
            }
        };
        adapter.setOnItemClickListener(new AbstractRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                if (isEditMode) {
                    //编辑模式单击为选中
                    if (selectedBuckets.contains(bucketList.get(position))) {
                        selectedBuckets.remove(bucketList.get(position));
                    } else {
                        selectedBuckets.add(bucketList.get(position));
                    }
                    MainActivity mainActivity = (MainActivity) getActivity();
                    if (mainActivity != null) {
                        mainActivity.showOperationBar(true, selectedBuckets.size());
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    //普通模式单击进入照片列表页
                    BucketBean item = bucketList.get(position);
                    PhotoListActivity.showActivity(getActivity(), item.bucketId, item.bucketName);
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                //进入编辑模式
                isEditMode = true;
                BucketBean selectedItem = bucketList.get(position);
                selectedBuckets.add(selectedItem);
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    mainActivity.showOperationBar(true, selectedBuckets.size());
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 刷新相册列表
     */
    public void refreshBucketList() {
        new BucketTask(this).execute();
    }

    /**
     * 当前是否编辑模式
     *
     * @return
     */
    public boolean isEditMode() {
        return isEditMode;
    }

    /**
     * 设置模式
     *
     * @param isEditMode true为编辑模式
     */
    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
        selectedBuckets.clear();
        adapter.notifyDataSetChanged();
    }

    public static BucketFragment newFragment(String title) {
        BucketFragment fragment = new BucketFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        fragment.setArguments(bundle);
        return fragment;
    }
}
