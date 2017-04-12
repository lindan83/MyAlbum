package com.lance.album.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lance.album.MainActivity;
import com.lance.album.PhotoViewActivity;
import com.lance.album.R;
import com.lance.album.adapter.PhotoGridAdapter;
import com.lance.album.bean.PhotoBean;
import com.lance.album.bean.SectionLabelBean;
import com.lance.album.service.PhotoBucketService;
import com.lance.album.util.ViewUtil;
import com.lance.album.widget.GridSpaceItemDecoration;
import com.lance.album.widget.SectionDecoration;
import com.lance.common.recyclerview.adapter.CommonRecyclerViewAdapter;
import com.lance.common.recyclerview.adapter.base.CommonRecyclerViewHolder;
import com.lance.common.util.DensityUtil;
import com.lance.common.util.SDCardUtil;
import com.lance.common.util.ToastUtil;
import com.lance.common.widget.template.BaseFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by lindan on 17-4-5.
 * 照片Fragment
 */
public class PhotoFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks {
    private static final int RC_EXTERNAL_STORAGE = 100;//请求码，外部存储器
    private static final int RC_PHOTO_VIEW_ACTIVITY = 101;//请求码，显示大图

    private int spanCount;
    private int margins;

    private RecyclerView rvPhoto;
    private CommonRecyclerViewAdapter<List<PhotoBean>> adapter;
    private List<PhotoGridAdapter> adapterList = new ArrayList<>();
    private List<List<PhotoBean>> photoList;
    private List<PhotoBean> photoViewList;

    private boolean isEditMode;
    private Set<PhotoBean> selectedPhotos = new HashSet<>();

    @Override
    public int getLayoutId() {
        return R.layout.fragment_photo;
    }

    @Override
    public void initViews() {
        rvPhoto = getView(R.id.rv_photo);
        rvPhoto.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvPhoto.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void initEventListeners() {

    }

    @Override
    public void initData() {
        spanCount = 4;
        margins = DensityUtil.dp2px(getActivity(), 4) * (spanCount + 1);
        photoList = new ArrayList<>();
        createAdapter(photoList);
        rvPhoto.setAdapter(adapter);
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
        new PhotoAsyncTask(this).execute();
    }

    @Override
    public void processClick(View v) {

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (perms != null && perms.size() > 0) {
            new PhotoAsyncTask(this).execute();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        ToastUtil.showShort(getActivity(), R.string.app_permission_request_external_storage_access_deny_msg);
    }

    private static class PhotoAsyncTask extends AsyncTask<Void, Integer, List<List<PhotoBean>>> {
        private WeakReference<PhotoFragment> ref;

        PhotoAsyncTask(PhotoFragment fragment) {
            ref = new WeakReference<>(fragment);
        }

        @Override
        protected List<List<PhotoBean>> doInBackground(Void... params) {
            PhotoFragment fragment = ref.get();
            if (fragment != null) {
                Map<SectionLabelBean, List<PhotoBean>> photoMap = PhotoBucketService.getInstance().getPhotoMap(fragment.getActivity());
                return PhotoBucketService.getInstance().getPhotoList(photoMap);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<List<PhotoBean>> photoList) {
            if (photoList != null) {
                final PhotoFragment fragment = ref.get();
                if (fragment != null) {
                    fragment.photoList.clear();
                    fragment.photoList.addAll(photoList);
                    fragment.photoViewList = PhotoBucketService.getInstance().mergePhotoList(fragment.photoList);
                    final List<SectionLabelBean> labelList = PhotoBucketService.getInstance().getPhotoLabelList(fragment.photoList);
                    if (fragment.adapter == null) {
                        fragment.rvPhoto.addItemDecoration(new SectionDecoration(labelList, DensityUtil.dp2px(fragment.getContext(), 28)));
                        fragment.createAdapter(fragment.photoList);
                        fragment.rvPhoto.setAdapter(fragment.adapter);
                    } else {
                        fragment.adapter.setData(fragment.photoList);
                    }
                }
            }
        }
    }

    private void createAdapter(List<List<PhotoBean>> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        adapter = new CommonRecyclerViewAdapter<List<PhotoBean>>(getContext(), R.layout.item_photo, data) {
            @Override
            protected void convert(CommonRecyclerViewHolder holder, List<PhotoBean> item, int position) {
                RecyclerView rvPhotoItem = holder.getView(R.id.rv_photo_item);
                PhotoGridAdapter adapter = (PhotoGridAdapter) rvPhotoItem.getAdapter();
                adapter.setData(item);
                int margin = margins / (spanCount + 1);
                ViewUtil.setRecyclerViewHeight(rvPhotoItem, (GridLayoutManager) rvPhotoItem.getLayoutManager(), margin);
            }

            @Override
            public void onViewHolderCreated(final CommonRecyclerViewHolder holder, final View itemView) {
                RecyclerView rvPhotoItem = holder.getView(R.id.rv_photo_item);
                rvPhotoItem.addItemDecoration(new GridSpaceItemDecoration(margins / (spanCount + 1)));
                GridLayoutManager gridLayoutManager = new GridLayoutManager(holder.itemView.getContext(), spanCount);
                rvPhotoItem.setLayoutManager(gridLayoutManager);
                final PhotoGridAdapter photoGridAdapter = new PhotoGridAdapter(PhotoFragment.this, spanCount, margins);
                adapterList.add(photoGridAdapter);
                photoGridAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                        PhotoBean item = photoGridAdapter.getData().get(position);
                        int index = PhotoBucketService.getInstance().getPhotoPosition(photoViewList, item.id);
                        PhotoViewActivity.showActivityForResultFromFragment(PhotoFragment.this, new ArrayList<>(photoViewList), index, RC_PHOTO_VIEW_ACTIVITY);
                    }

                    @Override
                    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                        //进入编辑模式
                        isEditMode = true;
                        PhotoBean selectedItem = photoGridAdapter.getData().get(position);
                        photoGridAdapter.addPhotoToSelected(selectedItem);
                        MainActivity mainActivity = (MainActivity) getActivity();
                        if (mainActivity != null) {
                            mainActivity.showOperationBar(true, selectedPhotos.size());
                        }
                        for (PhotoGridAdapter adapter : adapterList) {
                            adapter.notifyDataSetChanged();
                        }
                        return true;
                    }
                });
                rvPhotoItem.setAdapter(photoGridAdapter);
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_VIEW_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    ArrayList<String> deletedIds = data.getStringArrayListExtra(PhotoViewActivity.RESULT_ID);
                    if (deletedIds != null && !deletedIds.isEmpty()) {
                        new PhotoAsyncTask(PhotoFragment.this).execute();
                    }
                }
            }
        }
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
        new PhotoAsyncTask(this).execute();
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
        selectedPhotos.clear();
        for (PhotoGridAdapter adapter : adapterList) {
            adapter.clearSelectedPhotos();
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 通知照片选中项数量改变
     */
    public synchronized void notifyPhotoSelectedCountChange() {
        selectedPhotos.clear();
        for (PhotoGridAdapter adapter : adapterList) {
            selectedPhotos.addAll(adapter.getSelectedPhotos());
        }
        int selectedCount = selectedPhotos.size();
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.showOperationBar(true, selectedCount);
        }
    }

    public static PhotoFragment newFragment(String title) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        fragment.setArguments(bundle);
        return fragment;
    }
}
