package com.lance.album.fragment;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lance.album.R;
import com.lance.album.bean.BucketBean;
import com.lance.album.service.PhotoAlbumService;
import com.lance.common.recyclerview.adapter.AbstractRecyclerViewAdapter;
import com.lance.common.recyclerview.adapter.CommonRecyclerViewAdapter;
import com.lance.common.recyclerview.adapter.base.CommonRecyclerViewHolder;
import com.lance.common.util.SDCardUtil;
import com.lance.common.util.ToastUtil;
import com.lance.common.widget.template.BaseFragment;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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
        bucketList = new ArrayList<>();
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
                return PhotoAlbumService.getInstance().getBucketList(fragment.getActivity());
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
                ImageView ivBucket = holder.getView(R.id.iv_bucket);
                String path = item.photoList.isEmpty() ? "" : item.photoList.get(0).path;
                Glide.with(BucketFragment.this).load(new File(path)).placeholder(R.mipmap.icon_notpic).error(R.mipmap.icon_notpic).centerCrop().into(ivBucket);
                holder.setText(R.id.tv_bucket_name, item.bucketName);
                holder.setText(R.id.tv_bucket_photo_count, getString(R.string.app_bucket_photo_count, item.photoList.size()));
            }
        };
        adapter.setOnItemClickListener(new AbstractRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                // TODO: 17-4-7
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public static BucketFragment newFragment(String title) {
        BucketFragment fragment = new BucketFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        fragment.setArguments(bundle);
        return fragment;
    }
}
