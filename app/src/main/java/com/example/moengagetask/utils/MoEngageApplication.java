package com.example.moengagetask.utils;

import static com.example.moengagetask.utils.Constant.MoEAppId;

import android.app.Application;
import android.util.Log;

import com.moengage.core.DataCenter;
import com.moengage.core.MoEngage;

public class MoEngageApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initializeMoEngageSDK();

    }


    private void initializeMoEngageSDK() {
        MoEngage moEngage = new MoEngage.Builder(this, MoEAppId)
                .setDataCenter(DataCenter.DATA_CENTER_1).build();
        MoEngage.initialiseDefaultInstance(moEngage);
        Log.e("Roger", "MoEngage Initialized");

    }
}
