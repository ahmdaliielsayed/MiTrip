package com.mi.mitrip.MapPackage.map;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

public class UiHelper {

    public static MaterialDialog showAlwaysCircularProgressDialog(Context callingClassContext, String content) {
        return new MaterialDialog.Builder(callingClassContext)
                .content(content)
                .progress(true, 100)
                .cancelable(false)
                .show();
    }
}