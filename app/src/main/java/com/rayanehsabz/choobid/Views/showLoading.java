package com.rayanehsabz.choobid.Views;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.rayanehsabz.choobid.R;

public class showLoading {

    Dialog loading;
    Activity context;
    public showLoading(Activity a , int b) {

            this.context = a;
            // int  value
            // 1 -  loading


            loading = new Dialog(a);
            loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
            loading.setCanceledOnTouchOutside(false);

            loading.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    context.finish();
                }
            });

            if (b == 1) {
                loading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                loading.setContentView(R.layout.loading_dialog);

                GifImageView gifImageView = (GifImageView) loading.findViewById(R.id.gifLoading);
                gifImageView.setGifImageResource(R.drawable.choob_loop);
            }





    }

    public void show() {

        loading.show();
    }

    public void dismiss() {

        if (loading.isShowing()) {

            loading.dismiss();
        }
    }

    public boolean isShow() {

        if (loading.isShowing()) {

            return true;
        }

        return false;
    }




}