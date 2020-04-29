package com.example.mvcsocketserver;

import android.content.Context;
import android.widget.Toast;

public class ShowUtils {

    public  static Context context;

    public ShowUtils(Context context){
        this.context = context;
    }

    public static void toastTextShow(String str){
        Toast.makeText(context,str,Toast.LENGTH_SHORT).show();

    }
}
