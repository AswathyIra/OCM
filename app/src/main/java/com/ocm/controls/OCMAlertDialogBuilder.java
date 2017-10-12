package com.ocm.controls;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ocm.R;


public class OCMAlertDialogBuilder extends AlertDialog.Builder {

    private CharSequence title = null;
    private float buttonTextSize = 0;
    private DialogInterface.OnShowListener mOnShowListener = null;
    public OCMAlertDialogBuilder(Context context) {
        super(context, R.style.OCMAlertDialog);
    }

    @Override
    public AlertDialog create() {
        final AlertDialog dialog = super.create();

        if(title != null){
            View titleView = dialog.getLayoutInflater().inflate(R.layout.ocm_alert_dialog_title, null);
            ((TextView)titleView.findViewById(R.id.title)).setText(title);
            dialog.setCustomTitle(titleView);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                //TODO done here because on Kitkat and below ,theme was not working
                setListViewBackgroundColorAndRemoveDivider(dialog);
                setMessageTextColor(dialog);
                setBodyBackgroundColor(dialog);
                setButtonAppearance(dialog);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    setTitleDividerColor(dialog, R.color.white);
                }

                if(mOnShowListener != null){
                    mOnShowListener.onShow(dialogInterface);
                }
            }
        });
        return dialog;
    }

    private void setMessageTextColor(AlertDialog dialog) {
        TextView messageTextView = (TextView) dialog.findViewById(dialog.getContext().getResources().getIdentifier("message","id","android"));
        if (messageTextView != null){
            messageTextView.setTextColor(getContext().getResources().getColor(R.color.black));
        }
    }

    private void setBodyBackgroundColor(AlertDialog dialog) {
        View body = dialog.findViewById(dialog.getContext().getResources().getIdentifier("scrollView","id","android"));
        if (body != null){
            body.setBackgroundColor(getContext().getResources().getColor(R.color.white));
            body.setMinimumWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 200, dialog.getContext().getResources().getDisplayMetrics()));
            body.setMinimumHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 64, dialog.getContext().getResources().getDisplayMetrics()));
        }

        View customBody = dialog.findViewById(dialog.getContext().getResources().getIdentifier("custom","id","android"));
        if (customBody != null){
            customBody.setBackgroundColor(getContext().getResources().getColor(R.color.white));
            customBody.setMinimumWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 200, dialog.getContext().getResources().getDisplayMetrics()));
            customBody.setMinimumHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 64, dialog.getContext().getResources().getDisplayMetrics()));
        }
    }

    private void setListViewBackgroundColorAndRemoveDivider(AlertDialog dialog) {
        View listView = dialog.getListView();
        if(listView != null){
            listView.setBackgroundColor(getContext().getResources().getColor(R.color.white));
            ((ListView)listView).setDivider(null);

            View contentPanel = dialog.findViewById(dialog.getContext().getResources().getIdentifier("contentPanel","id","android"));
            if(contentPanel != null){
                contentPanel.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    public void setButtonsTextSize(float buttonTextSize){
        this.buttonTextSize = buttonTextSize;
    }

    private void setTitleDividerColor(AlertDialog dialog, int color) {
        int dividerId = dialog.getContext().getResources().getIdentifier("android:id/titleDivider",   null, null);
        View divider = dialog.findViewById(dividerId);
        if(divider!=null) {
            divider.setBackgroundColor(color);
        }
    }

    private void setButtonAppearance(AlertDialog dialog) {
        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button neutralButton = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);

        positiveButton.setTextColor(getContext().getResources().getColor(R.color.black));
        positiveButton.setBackgroundColor(getContext().getResources().getColor(R.color.white));
        negativeButton.setTextColor(getContext().getResources().getColor(R.color.black));
        negativeButton.setBackgroundColor(getContext().getResources().getColor(R.color.white));
        neutralButton.setTextColor(getContext().getResources().getColor(R.color.black));
        neutralButton.setBackgroundColor(getContext().getResources().getColor(R.color.white));

        if(buttonTextSize != 0){
            positiveButton.setTextSize(buttonTextSize);
            negativeButton.setTextSize(buttonTextSize);
            neutralButton.setTextSize(buttonTextSize);
        }
    }

    public void setOnShowListener(DialogInterface.OnShowListener listener) {
        if (listener != null) {
            mOnShowListener = listener;
        }
    }

    @Override
    public AlertDialog.Builder setTitle(CharSequence title) {
        AlertDialog.Builder builder = super.setTitle(title);
        this.title = title;
        return builder;
    }

    @Override
    public AlertDialog.Builder setTitle(int titleId) {
        AlertDialog.Builder builder = super.setTitle(titleId);
        this.title = getContext().getResources().getString(titleId);
        return builder;
    }
}
