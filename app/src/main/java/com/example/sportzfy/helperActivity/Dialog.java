package com.example.sportzfy.helperActivity;

import android.app.Activity;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;

import com.example.sportzfy.R;

public class Dialog {
    private Activity activity;
    private AlertDialog dialog;

    public Dialog(Activity myActivity) {
        activity = myActivity;
    }

    public void startLoadingDialog() {

        // adding ALERT Dialog builder object and passing activity as parameter
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // layout inflater object and use activity to get layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    // dismiss method
    public void dismissDialog() {
        dialog.dismiss();
    }
}
