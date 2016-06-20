package com.envative.uno.activities;

import android.content.Intent;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.envative.uno.R;
import com.envative.uno.comms.UNOUtil;
import io.fabric.sdk.android.Fabric;


/**
 * Created by clay on 1/22/16.
 */
public class UNOSplashScreen extends android.app.Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(1000); // 2 second delay ( ideally want to do this when the app loads the account data )
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    if(UNOUtil.get(getApplicationContext()).checkLoggedIn()){
                        startActivity(new Intent(UNOSplashScreen.this,UNOActivity.class));
                    }else{
                        startActivity(new Intent(UNOSplashScreen.this,LoginActivity.class));
                    }
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}
