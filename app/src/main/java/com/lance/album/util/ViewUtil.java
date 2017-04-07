package com.lance.album.util;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.lance.common.util.ScreenUtil;

/**
 * Created by lindan on 17-4-6.
 */
public class ViewUtil {
    /**
     * 设置RecyclerView高度
     *
     * @param recyclerView      RecyclerView
     * @param gridLayoutManager 网格布局管理器
     * @param margin            每个Item之间的空隙
     */
    public static void setRecyclerViewHeight(RecyclerView recyclerView, GridLayoutManager gridLayoutManager, int margin) {
        int screenWidth = ScreenUtil.getScreenWidth(recyclerView.getContext());
        ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
        //计算行数
        int spanCount = gridLayoutManager.getSpanCount();
        int totalCount = recyclerView.getAdapter().getItemCount();
        int lineCount = totalCount / spanCount;
        if (totalCount % spanCount != 0) {
            lineCount++;
        }
        layoutParams.height = lineCount * (screenWidth - margin * (spanCount + 1)) / spanCount + margin * (lineCount + 1);
        recyclerView.setLayoutParams(layoutParams);
    }
}