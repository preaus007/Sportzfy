package com.example.sportzfy.helperClasses;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import com.example.sportzfy.R;

public class CustomLoading {

    private Activity activity;
    private Dialog dialog;

    public CustomLoading(Activity myActivity) {
        activity = myActivity;
    }

    public void startLoadingDialog(String title) {

        // adding ALERT Dialog builder object and passing activity as parameter
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.custom_loading_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView loadingTitle = dialog.findViewById(R.id.loadingText);
        loadingTitle.setText(title);

        dialog.setCancelable(false);
        dialog.create();
        dialog.show();
    }

    // dismiss method
    public void dismissDialog() {
        dialog.dismiss();
    }
}
