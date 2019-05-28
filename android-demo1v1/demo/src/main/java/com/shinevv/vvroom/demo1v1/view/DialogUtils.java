package com.shinevv.vvroom.demo1v1.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.shinevv.vvroom.demo1v1.MainActivity;
import com.shinevv.vvroom.demo1v1.R;

public class DialogUtils {

    public static Activity mmActivity;
    private static DialogUtils dialogUtils;
    private static AlertDialog.Builder normalDialog;

    public void showNormalDialog(String message, final boolean isFinish){
        normalDialog.setTitle("友情提示");
        normalDialog.setMessage(message);
        normalDialog.setPositiveButton("知道了",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(isFinish){
                            mmActivity.finish();
                        }
                    }
                });
        // 显示
        normalDialog.create().show();
    }


    public static DialogUtils getDialogInstance(Activity mActivity){
        if(dialogUtils==null){
            mmActivity = mActivity;
            dialogUtils = new DialogUtils();
            normalDialog = new AlertDialog.Builder(mActivity);
        }

        return dialogUtils;
    }


    public void disMiss(){
        normalDialog.create().dismiss();
        normalDialog=null;
        dialogUtils=null;
    }

}
