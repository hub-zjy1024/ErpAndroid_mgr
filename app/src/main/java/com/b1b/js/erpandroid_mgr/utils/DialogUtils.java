package com.b1b.js.erpandroid_mgr.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by 张建宇 on 2017/6/6.
 */

public class DialogUtils {
    public static AlertDialog createAlertDialog(Context mContext, String title, String
            msg, String
                                                        leftBtn, DialogInterface
            .OnClickListener lListener, String rightBtn,
                                                DialogInterface.OnClickListener
                                                        rListener, boolean canCancel) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        if (title != null) {
            builder.setTitle(title);
        }
        builder.setMessage(msg);
        if(leftBtn!=null){
            builder.setNegativeButton(leftBtn, lListener);
        }
        if (rightBtn != null) {
            builder.setPositiveButton(rightBtn, rListener);
        }
        builder.setCancelable(canCancel);
        return builder.create();
    }

    public static AlertDialog createAlertDialog(Context mContext, String msg, String
            leftBtn, DialogInterface.OnClickListener lListener, String rightBtn,
                                                DialogInterface.OnClickListener
                                                        rListener, boolean canCancel) {

        return createAlertDialog(mContext, "提示", msg, leftBtn, lListener, rightBtn,
                rListener, canCancel);
    }

    public static AlertDialog createAlertDialog(Context mContext, String msg, String
            leftBtn, DialogInterface.OnClickListener lListener, String rightBtn,
                                                DialogInterface.OnClickListener
                                                        rListener) {

        return createAlertDialog(mContext, "提示", msg, leftBtn, lListener, rightBtn,
                rListener, false);
    }
    public static AlertDialog createAlertDialog(Context mContext, String msg) {

        return createAlertDialog(mContext, "提示", msg, null, null, null,
                null, false);
    }

    public static AlertDialog createAlertDialog(Context mContext, String msg, String
            oneOkButton) {

        return createAlertDialog(mContext, "提示", msg, null, null, oneOkButton,
                null, false);
    }

    public static void dissmissDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static void cancelDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.cancel();
        }
    }

    public static void showDialog(Dialog dialog) {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }
}
