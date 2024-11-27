package com.example.speedyserve;

import android.content.Context;
import android.content.DialogInterface;
import android.app.AlertDialog;

public class ReusableCodeForAll {
    public static void ShowAlert(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}
