package com.lance.album.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.lance.album.R;
import com.lance.common.widget.dialog.IDialog;

/**
 * Created by lindan on 17-4-11.
 * 新建相册对话框
 */
public class InputBucketNameDialog implements IDialog, View.OnClickListener {
    private Context context;
    private AlertDialog ad;
    private TextView titleView;
    private EditText etDialogContent;
    private TextView tvPositiveButton, tvNegativeButton;
    private com.lance.common.widget.dialog.IDialog.OnClickListener onClickListener;
    private OnShowListener onShowListener;
    private OnDismissListener onDismissListener;
    private OnCancelListener onCancelListener;

    public InputBucketNameDialog(Context context) {
        this(context, false, false);
    }

    public InputBucketNameDialog(Context context, boolean cancelable, boolean canceledOnTouchOutside) {
        this.context = context;
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        View view = LayoutInflater.from(this.context).inflate(R.layout.dialog_bucket_create, null, false);
        this.titleView = (TextView) view.findViewById(R.id.tv_dialog_title);
        this.etDialogContent = (EditText) view.findViewById(R.id.et_dialog_content);
        this.tvPositiveButton = (TextView) view.findViewById(R.id.tv_positive_button);
        this.tvPositiveButton.setOnClickListener(this);
        this.tvNegativeButton = (TextView) view.findViewById(R.id.tv_negative_button);
        this.tvNegativeButton.setOnClickListener(this);
        builder.setView(view);
        this.ad = builder.create();
        this.ad.setCancelable(cancelable);
        this.ad.setCanceledOnTouchOutside(canceledOnTouchOutside);
        Window window = this.ad.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

    }

    public void setTitle(int resId) {
        String text = this.context.getString(resId);
        this.setTitle(text);
    }

    public void setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            this.titleView.setVisibility(View.GONE);
        } else {
            this.titleView.setVisibility(View.VISIBLE);
            this.titleView.setText(title);
        }

    }

    @Override
    public void setMessage(int resId) {
        String text = this.context.getString(resId);
        this.setMessage(text);
    }

    @Override
    public void setMessage(String message) {
        if (TextUtils.isEmpty(message)) {
            this.etDialogContent.setText("");
        } else {
            this.etDialogContent.setText(message);
        }
    }

    public void setPositiveButton(String text) {
        if (TextUtils.isEmpty(text)) {
            this.tvPositiveButton.setVisibility(View.GONE);
        } else {
            this.tvPositiveButton.setVisibility(View.VISIBLE);
            this.tvPositiveButton.setText(text);
        }
    }

    public void setPositiveButton(int resId) {
        String text = this.context.getString(resId);
        this.setPositiveButton(text);
    }

    public void setNegativeButton(String text) {
        if (TextUtils.isEmpty(text)) {
            this.tvNegativeButton.setVisibility(View.GONE);
        } else {
            this.tvNegativeButton.setVisibility(View.VISIBLE);
            this.tvNegativeButton.setText(text);
        }
    }

    public void setNegativeButton(int resId) {
        String text = this.context.getString(resId);
        this.setNegativeButton(text);
    }

    public void setNeutralButton(String text) {
    }

    public void setNeutralButton(int resId) {
    }

    public void setCancelable(boolean cancelable) {
        this.ad.setCancelable(cancelable);
    }

    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        this.ad.setCanceledOnTouchOutside(canceledOnTouchOutside);
    }

    public void dismiss() {
        if (this.ad.isShowing()) {
            this.ad.dismiss();
            if (this.onDismissListener != null) {
                this.onDismissListener.onDismiss(this);
            }
        }
    }

    public void show() {
        if (!this.ad.isShowing()) {
            this.ad.show();
            if (this.onShowListener != null) {
                this.onShowListener.onShow(this);
            }
        }
    }

    public void cancel() {
        if (this.ad.isShowing()) {
            this.ad.cancel();
            if (this.onCancelListener != null) {
                this.onCancelListener.onCancel(this);
            }
        }
    }

    public void setOnCancelListener(OnCancelListener listener) {
        this.onCancelListener = listener;
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.onDismissListener = listener;
    }

    public void setOnShowListener(OnShowListener listener) {
        this.onShowListener = listener;
    }

    public void setOnClickListener(com.lance.common.widget.dialog.IDialog.OnClickListener listener) {
        this.onClickListener = listener;
    }

    public void setOnKeyListener(OnKeyListener listener) {
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_positive_button && this.onClickListener != null) {
            this.onClickListener.onClick(this, IDialog.BUTTON_POSITIVE);
        } else if (id == R.id.tv_negative_button && this.onClickListener != null) {
            this.onClickListener.onClick(this, IDialog.BUTTON_NEGATIVE);
        }
    }

    public String getMessage() {
        return etDialogContent.getText().toString();
    }

    public void requestFocus() {
        if (etDialogContent != null) {
            etDialogContent.requestFocus();
        }
    }
}
