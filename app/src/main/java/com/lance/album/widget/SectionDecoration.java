package com.lance.album.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import com.lance.album.bean.SectionLabelBean;

import java.util.List;

/**
 * Created by lindan on 17-4-6.
 * 悬浮头Section组件
 */
public class SectionDecoration extends RecyclerView.ItemDecoration {
    private List<SectionLabelBean> dataList;
    private TextPaint textPaint;
    private Paint paint;
    private int headerHeight;

    public SectionDecoration(List<SectionLabelBean> dataList, int headerHeight) {
        this.dataList = dataList;
        //悬浮栏高度
        this.headerHeight = headerHeight;
        //文本的大小
        int headerTextSize = headerHeight / 2;
        //设置悬浮栏画笔
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        //设置悬浮栏文本画笔
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(headerTextSize);
        textPaint.setColor(Color.DKGRAY);
        textPaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
        if (pos >= 0) {
            //第一项必有title
            if (pos == 0) {
                outRect.set(0, headerHeight, 0, 0);
            } else {
                String currentItem = dataList.get(pos).label;
                if (TextUtils.equals("-1", currentItem)) {
                    return;
                }
                String previousItem = dataList.get(pos - 1).label;
                if (!TextUtils.equals(currentItem, previousItem)) {
                    outRect.set(0, headerHeight, 0, 0);
                } else {
                    outRect.set(0, 0, 0, 0);
                }
            }
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            if (position >= 0) {
                String textLine = dataList.get(position).label;
                if (TextUtils.equals("-1", textLine)) {
                    return;
                }
                if (TextUtils.equals("", textLine)) {
                    float top = view.getTop();
                    float bottom = view.getTop();
                    c.drawRect(left, top, right, bottom, paint);
                } else {
                    if (position == 0 || isFirstInGroup(position)) {
                        float top = view.getTop() - headerHeight;
                        float bottom = view.getTop();
                        //绘制悬浮栏
                        c.drawRect(left, top, right, bottom, paint);
                    }
                }
            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int itemCount = state.getItemCount();
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        String preGroupId;
        String groupId = "-1";
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);

            preGroupId = groupId;
            groupId = dataList.get(position).label;
            if (TextUtils.equals("-1", groupId) || TextUtils.equals(groupId, preGroupId)) {
                continue;
            }

            int viewBottom = view.getBottom();
            float textY = Math.max(headerHeight, view.getTop());
            //下一个和当前不一样移动当前
            if (position + 1 < itemCount) {
                String nextGroupId = dataList.get(position + 1).label;
                //组内最后一个view进入了header
                if (!TextUtils.equals(nextGroupId, groupId) && viewBottom < textY) {
                    textY = viewBottom;
                }
            }
            //textY - headerHeight决定了悬浮栏绘制的高度和位置
            c.drawRect(left, textY - headerHeight, right, textY, paint);
            Rect textRect = new Rect();
            textPaint.getTextBounds(groupId, 0, groupId.length(), textRect);
            c.drawText(groupId, left, textY - (headerHeight - textRect.height()) / 2, textPaint);
        }
    }

    //判断是否组内第一个位置
    private boolean isFirstInGroup(int position) {
        if (position == 0) {
            return true;
        }
        //根据字符串内容是否相同来判断是否同组
        String prevGroupId = dataList.get(position - 1).label;
        String groupId = dataList.get(position).label;
        return !TextUtils.equals(groupId, prevGroupId);
    }
}
