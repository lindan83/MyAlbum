package com.lance.album;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lance.album.bean.PhotoBean;
import com.lance.album.service.PhotoAlbumService;
import com.lance.album.widget.GridSpaceItemDecoration;
import com.lance.common.recyclerview.adapter.AbstractRecyclerViewAdapter;
import com.lance.common.recyclerview.adapter.CommonRecyclerViewAdapter;
import com.lance.common.recyclerview.adapter.base.CommonRecyclerViewHolder;
import com.lance.common.util.DensityUtil;
import com.lance.common.util.ScreenUtil;
import com.lance.common.util.ToastUtil;
import com.lance.common.widget.template.BaseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 相册中的照片列表Grid
 */
public class PhotoListActivity extends BaseActivity {
    private static final byte TYPE_SORT_DATETIME = 11;
    private static final byte TYPE_SORT_SIZE = 12;
    private static final byte TYPE_SORT_NAME = 3;

    private RelativeLayout rlSortByDatetime, rlSortBySize, rlSortByName;
    private ImageView ivSortByDateTime, ivSortBySize, ivSortByName;
    private TextView tvSortByDateTime, tvSortBySize, tvSortByName;
    private CommonRecyclerViewAdapter<PhotoBean> adapter;
    private List<PhotoBean> photoList = new ArrayList<>();
    private String bucketId;
    private int itemMargin, spanCount = 4;

    int sortBy = PhotoComparator.SORT_BY_DATETIME;
    boolean[] asc = {PhotoComparator.SORT_DESC, PhotoComparator.SORT_ASC, PhotoComparator.SORT_ASC};

    @Override
    public int getLayoutId() {
        return R.layout.activity_photo_list;
    }

    @Override
    public void initViews() {
        itemMargin = DensityUtil.dp2px(this, 4);
        bucketId = getIntent().getStringExtra("bucket_id");
        String bucketName = getIntent().getStringExtra("bucket_name");
        if (TextUtils.isEmpty(bucketId) || TextUtils.isEmpty(bucketName)) {
            ToastUtil.showShort(this, getString(R.string.app_error_arguments, "bucketId or bucketName is null or empty!"));
            finish();
            return;
        }
        TextView tvBucketName = getView(R.id.tv_bucket_name);
        rlSortByDatetime = getView(R.id.rl_sort_by_datetime);
        rlSortBySize = getView(R.id.rl_sort_by_size);
        rlSortByName = getView(R.id.rl_sort_by_name);
        ivSortByDateTime = getView(R.id.iv_sort_by_datetime);
        ivSortBySize = getView(R.id.iv_sort_by_size);
        ivSortByName = getView(R.id.iv_sort_by_name);
        tvSortByDateTime = getView(R.id.tv_sort_by_datetime);
        tvSortBySize = getView(R.id.tv_sort_by_size);
        tvSortByName = getView(R.id.tv_sort_by_name);
        RecyclerView rvPhotoGrid = getView(R.id.rv_photo_grid);
        rvPhotoGrid.setLayoutManager(new GridLayoutManager(this, spanCount));
        rvPhotoGrid.setItemAnimator(new DefaultItemAnimator());
        rvPhotoGrid.addItemDecoration(new GridSpaceItemDecoration(itemMargin));
        adapter = new CommonRecyclerViewAdapter<PhotoBean>(this, R.layout.item_photo_grid, photoList) {
            @Override
            protected void convert(CommonRecyclerViewHolder holder, PhotoBean item, int position) {
                Glide.with(PhotoListActivity.this).load(new File(item.path)).error(R.mipmap.icon_notpic).placeholder(R.mipmap.icon_notpic).centerCrop().into((ImageView) holder.getView(R.id.iv_photo));
            }

            @Override
            public void onViewHolderCreated(CommonRecyclerViewHolder holder, View itemView) {
                int widthPixels = ScreenUtil.getScreenWidth(PhotoListActivity.this);
                int margins = itemMargin * spanCount;
                int size = (widthPixels - margins) / spanCount;
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(size, size);
                holder.getView(R.id.iv_photo).setLayoutParams(lp);
            }
        };
        rvPhotoGrid.setAdapter(adapter);
        tvBucketName.setText(bucketName);
        rlSortByDatetime.setSelected(true);
        rlSortBySize.setSelected(false);
        rlSortByName.setSelected(false);
        tvSortByDateTime.setTextColor(Color.WHITE);
        tvSortBySize.setTextColor(Color.BLACK);
        tvSortByName.setTextColor(Color.BLACK);
    }

    @Override
    public void initEventListeners() {
        rlSortByDatetime.setOnClickListener(this);
        rlSortBySize.setOnClickListener(this);
        rlSortByName.setOnClickListener(this);
        adapter.setOnItemClickListener(new AbstractRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                PhotoViewActivity.showActivity(PhotoListActivity.this, photoList, position);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    @Override
    public void initData() {
        List<PhotoBean> photoBeanList = PhotoAlbumService.getInstance().getPhotoList(this, bucketId);
        if (photoBeanList != null && !photoBeanList.isEmpty()) {
            photoList.addAll(photoBeanList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void processClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.rl_sort_by_datetime:
                processSort(TYPE_SORT_DATETIME, !asc[0]);
                break;
            case R.id.rl_sort_by_size:
                processSort(TYPE_SORT_SIZE, !asc[1]);
                break;
            case R.id.rl_sort_by_name:
                processSort(TYPE_SORT_NAME, !asc[2]);
                break;
        }
    }

    private void processSort(int type, boolean asc) {
        if (type == TYPE_SORT_DATETIME) {
            this.sortBy = PhotoComparator.SORT_BY_DATETIME;
            this.asc[0] = asc;
            ivSortByDateTime.setImageResource(asc ? R.mipmap.ic_arrow_up : R.mipmap.ic_arrow_down);
            rlSortByDatetime.setSelected(true);
            rlSortBySize.setSelected(false);
            rlSortByName.setSelected(false);
            tvSortByDateTime.setTextColor(Color.WHITE);
            tvSortBySize.setTextColor(Color.BLACK);
            tvSortByName.setTextColor(Color.BLACK);
        } else if (type == TYPE_SORT_SIZE) {
            this.sortBy = PhotoComparator.SORT_BY_SIZE;
            this.asc[1] = asc;
            ivSortBySize.setImageResource(asc ? R.mipmap.ic_arrow_up : R.mipmap.ic_arrow_down);
            rlSortByDatetime.setSelected(false);
            rlSortBySize.setSelected(true);
            rlSortByName.setSelected(false);
            tvSortByDateTime.setTextColor(Color.BLACK);
            tvSortBySize.setTextColor(Color.WHITE);
            tvSortByName.setTextColor(Color.BLACK);
        } else if (type == TYPE_SORT_NAME) {
            this.sortBy = PhotoComparator.SORT_BY_NAME;
            this.asc[2] = asc;
            ivSortByName.setImageResource(asc ? R.mipmap.ic_arrow_up : R.mipmap.ic_arrow_down);
            rlSortByDatetime.setSelected(false);
            rlSortBySize.setSelected(false);
            rlSortByName.setSelected(true);
            tvSortByDateTime.setTextColor(Color.BLACK);
            tvSortBySize.setTextColor(Color.BLACK);
            tvSortByName.setTextColor(Color.WHITE);
        }
        Collections.sort(photoList, new PhotoComparator(this.sortBy, asc));
        adapter.notifyDataSetChanged();
    }

    //照片排序比较器
    private static class PhotoComparator implements Comparator<PhotoBean> {
        static final int SORT_BY_DATETIME = 1;
        static final int SORT_BY_SIZE = 2;
        static final int SORT_BY_NAME = 3;
        static final boolean SORT_ASC = true;
        static final boolean SORT_DESC = false;
        int sortBy = SORT_BY_DATETIME;
        boolean asc = SORT_DESC;

        PhotoComparator(int sortBy, boolean asc) {
            if (sortBy != SORT_BY_DATETIME && sortBy != SORT_BY_SIZE && sortBy != SORT_BY_NAME) {
                this.sortBy = SORT_BY_DATETIME;
            } else {
                this.sortBy = sortBy;
            }
            this.asc = asc;
        }

        @Override
        public int compare(PhotoBean o1, PhotoBean o2) {
            switch (sortBy) {
                case SORT_BY_DATETIME:
                    if (asc) {
                        return -o1.date.compareTo(o2.date);
                    } else {
                        return o1.date.compareTo(o2.date);
                    }
                case SORT_BY_SIZE:
                    if (asc) {
                        if (o1.size > o2.size) {
                            return -1;
                        }
                        if (o1.size == o2.size) {
                            return 0;
                        }
                        return 1;
                    } else {
                        if (o1.size > o2.size) {
                            return 1;
                        }
                        if (o1.size == o2.size) {
                            return 0;
                        }
                        return -1;
                    }
                case SORT_BY_NAME:
                    if (asc) {
                        return -o1.name.compareTo(o2.name);
                    } else {
                        return o1.name.compareTo(o2.name);
                    }
                default:
                    return 0;
            }
        }
    }

    public static void showActivity(Context context, String bucketId, String bucketName) {
        if (TextUtils.isEmpty(bucketId) || TextUtils.isEmpty(bucketName)) {
            ToastUtil.showShort(context, context.getString(R.string.app_error_arguments, "bucketId or bucketName is null or empty!"));
            return;
        }
        Intent intent = new Intent(context, PhotoListActivity.class);
        intent.putExtra("bucket_id", bucketId);
        intent.putExtra("bucket_name", bucketName);
        context.startActivity(intent);
    }
}
