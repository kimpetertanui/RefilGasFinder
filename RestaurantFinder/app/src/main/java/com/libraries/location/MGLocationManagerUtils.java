package com.libraries.location;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by mg on 24/10/16.
 */
public class MGLocationManagerUtils {

    OnAlertListener mCallback;

    public interface OnAlertListener {
        public void onPositiveTapped();
        public void onNegativeTapped();
    }

    public void setOnAlertListener(OnAlertListener listener) {
        try {
            mCallback = (OnAlertListener) listener;
        } catch (ClassCastException e)  {
            throw new ClassCastException(this.toString() + " must implement OnAlertListener");
        }
    }

    public void showAlertView(Activity act, int resIdTitle, int resIdMessage, int resIdPositiveButton, int resIdNegativeButton) {
        AlertDialog.Builder alert = new AlertDialog.Builder(act);
        alert.setTitle(resIdTitle);
        alert.setMessage(resIdMessage);
        alert.setPositiveButton(act.getResources().getString(resIdPositiveButton),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        if(mCallback != null)
                            mCallback.onPositiveTapped();
                    }
                });

        alert.setNegativeButton(act.getResources().getString(resIdNegativeButton),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        if(mCallback != null)
                            mCallback.onNegativeTapped();
                    }
                });

        alert.create();
        alert.show();
    }
}
