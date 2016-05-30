package com.envative.uno.comms;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by clay on 5/29/16.
 */
public class UNOUtil {

    private static UNOUtil instance;
    private Context context;
    private final SharedPreferences sharedPref;

    private UNOUtil(Context context){
        this.context  = context;
        sharedPref = context.getSharedPreferences("UNOPref", Context.MODE_PRIVATE);
    }

    public static UNOUtil get(Context context) {
        if (instance == null) {
            instance = new UNOUtil(context.getApplicationContext());
        }

        instance.context = context;
        return instance;
    }

}
