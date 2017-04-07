package com.lance.album;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lance.album.adapter.PhotoGridAdapter;
import com.lance.album.bean.PhotoBean;
import com.lance.album.bean.SectionLabelBean;
import com.lance.album.service.PhotoAlbumService;
import com.lance.album.util.ViewUtil;
import com.lance.album.widget.GridSpaceItemDecoration;
import com.lance.album.widget.SectionDecoration;
import com.lance.common.recyclerview.adapter.CommonRecyclerViewAdapter;
import com.lance.common.recyclerview.adapter.base.CommonRecyclerViewHolder;
import com.lance.common.util.DensityUtil;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

/**
 * 测试嵌套RecyclerView实现相册分类
 * 略有卡顿
 */
public class TestNestedRecyclerViewActivity extends AppCompatActivity {
    private RecyclerView rvPhoto;
    private CommonRecyclerViewAdapter<List<PhotoBean>> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_recyclerview);

        rvPhoto = (RecyclerView) findViewById(R.id.rv_photo);
        rvPhoto.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvPhoto.setItemAnimator(new DefaultItemAnimator());
        new PhotoAsyncTask(this).execute();
    }

    private static class PhotoAsyncTask extends AsyncTask<Void, Integer, List<List<PhotoBean>>> {
        private WeakReference<TestNestedRecyclerViewActivity> activityRef;

        PhotoAsyncTask(TestNestedRecyclerViewActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        protected List<List<PhotoBean>> doInBackground(Void... params) {
            Activity activity = activityRef.get();
            if (activity != null) {
                Map<SectionLabelBean, List<PhotoBean>> photoMap = PhotoAlbumService.getInstance().getPhotoMap(activity);
                return PhotoAlbumService.getInstance().getPhotoList(photoMap);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<List<PhotoBean>> photoList) {
            if (photoList != null) {
                final TestNestedRecyclerViewActivity activity = activityRef.get();
                if (activity != null) {
                    final List<SectionLabelBean> labelList = PhotoAlbumService.getInstance().getPhotoLabelList(photoList);
                    if (activity.adapter == null) {
                        activity.rvPhoto.addItemDecoration(new SectionDecoration(labelList, DensityUtil.dp2px(activity, 28)));
                        activity.adapter = new CommonRecyclerViewAdapter<List<PhotoBean>>(activity, R.layout.item_photo, photoList) {
                            int spanCount;
                            int margins;

                            @Override
                            protected void convert(CommonRecyclerViewHolder holder, List<PhotoBean> item, int position) {
                                RecyclerView rvPhotoItem = holder.getView(R.id.rv_photo_item);
                                PhotoGridAdapter adapter = (PhotoGridAdapter) rvPhotoItem.getAdapter();
                                adapter.setData(item);
                                int margin = margins / (spanCount + 1);
                                ViewUtil.setRecyclerViewHeight(rvPhotoItem, (GridLayoutManager) rvPhotoItem.getLayoutManager(), margin);
                            }

                            @Override
                            public void onViewHolderCreated(CommonRecyclerViewHolder holder, View itemView) {
                                RecyclerView rvPhotoItem = holder.getView(R.id.rv_photo_item);
                                rvPhotoItem.addItemDecoration(new GridSpaceItemDecoration(DensityUtil.dp2px(holder.itemView.getContext(), 4)));
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(holder.itemView.getContext(), 4);
                                rvPhotoItem.setLayoutManager(gridLayoutManager);
                                spanCount = gridLayoutManager.getSpanCount();
                                margins = DensityUtil.dp2px(itemView.getContext(), 4) * (spanCount + 1);
                                PhotoGridAdapter photoGridAdapter = new PhotoGridAdapter(itemView.getContext(), spanCount, margins);
                                rvPhotoItem.setAdapter(photoGridAdapter);

                            }
                        };
                        activity.rvPhoto.setAdapter(activity.adapter);
                    } else {
                        activity.adapter.setData(photoList);
                    }
                }
            }
        }
    }
}
