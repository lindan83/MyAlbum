package com.lance.album.widget;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by lindan on 17-4-6.
 * GridLayoutManager各Item之间的间距
 * 注意：item view layout不需要设置margin
 */
public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public GridSpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        //不是第一个的格子都设一个左边和底部的间距
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            outRect.left = space;
            outRect.bottom = space;
            outRect.top = space;
            outRect.right = space;
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            int spanCount = gridLayoutManager.getSpanCount();
            int position = parent.getChildLayoutPosition(view);
            //如果不是末列，去掉left
            if (position % spanCount != 0) {
                outRect.left = 0;
            }
            //如果不是首行，去掉top
            if (position >= spanCount) {
                outRect.top = 0;
            }
//            Log.d("GridSpaceItemDecoration", "getItemOffsets: position = " + position);
//            Log.d("GridSpaceItemDecoration", "getItemOffsets: left = " + outRect.left);
//            Log.d("GridSpaceItemDecoration", "getItemOffsets: top = " + outRect.top);
//            Log.d("GridSpaceItemDecoration", "getItemOffsets: right = " + outRect.right);
//            Log.d("GridSpaceItemDecoration", "getItemOffsets: bottom = " + outRect.bottom);
//            Log.d("GridSpaceItemDecoration", "getItemOffsets: -----------------------------------");
        }
    }
}