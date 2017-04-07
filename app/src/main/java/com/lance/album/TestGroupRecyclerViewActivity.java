package com.lance.album;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lance.album.bean.PhotoBean;
import com.lance.album.bean.SectionLabelBean;
import com.lance.album.service.PhotoAlbumService;
import com.lance.album.widget.GridSpaceItemDecoration;
import com.lance.album.widget.SectionDecoration;
import com.lance.common.recyclerview.adapter.CommonRecyclerViewAdapter;
import com.lance.common.recyclerview.adapter.base.CommonRecyclerViewHolder;
import com.lance.common.util.DensityUtil;
import com.lance.common.util.ScreenUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

public class TestGroupRecyclerViewActivity extends AppCompatActivity {
    private RecyclerView rvPhoto;
    private CommonRecyclerViewAdapter<PhotoBean> adapter;
    private int margin;
    private int spanCount = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_recyclerview);

        margin = DensityUtil.dp2px(this, 4);

        rvPhoto = (RecyclerView) findViewById(R.id.rv_photo);
        rvPhoto.setLayoutManager(new GridLayoutManager(this, spanCount, GridLayoutManager.VERTICAL, false));
        rvPhoto.addItemDecoration(new GridSpaceItemDecoration(margin));
        rvPhoto.setItemAnimator(new DefaultItemAnimator());
        new PhotoAsyncTask(this).execute();
    }

    private static class PhotoAsyncTask extends AsyncTask<Void, Integer, List<PhotoBean>> {
        private WeakReference<TestGroupRecyclerViewActivity> activityRef;

        PhotoAsyncTask(TestGroupRecyclerViewActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        protected List<PhotoBean> doInBackground(Void... params) {
            Activity activity = activityRef.get();
            if (activity != null) {
                Map<SectionLabelBean, List<PhotoBean>> photoMap = PhotoAlbumService.getInstance().getPhotoMap(activity);
                return PhotoAlbumService.getInstance().mergePhotoList(photoMap);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<PhotoBean> photoList) {
            if (photoList != null) {
                final TestGroupRecyclerViewActivity activity = activityRef.get();
                if (activity != null) {
                    final List<SectionLabelBean> labelList = PhotoAlbumService.getInstance().mergePhotoLabelList(photoList);
                    if (activity.adapter == null) {
                        activity.rvPhoto.addItemDecoration(new SectionDecoration(labelList, DensityUtil.dp2px(activity, 28)));
                        activity.adapter = new CommonRecyclerViewAdapter<PhotoBean>(activity, R.layout.item_photo_grid, photoList) {
                            @Override
                            protected void convert(CommonRecyclerViewHolder holder, PhotoBean item, int position) {
                                Glide.with(holder.getConvertView().getContext()).load(new File(item.path)).centerCrop().into((ImageView) holder.getView(R.id.iv_photo));
                            }

                            @Override
                            public void onViewHolderCreated(CommonRecyclerViewHolder holder, View itemView) {
                                super.onViewHolderCreated(holder, itemView);
                                int widthPixels = ScreenUtil.getScreenWidth(holder.itemView.getContext());
                                int margins = activity.margin * (activity.spanCount + 1);
                                int spanCount = activity.spanCount;
                                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((widthPixels - margins) / spanCount, (widthPixels - margins) / spanCount);
                                holder.getView(R.id.iv_photo).setLayoutParams(lp);
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
