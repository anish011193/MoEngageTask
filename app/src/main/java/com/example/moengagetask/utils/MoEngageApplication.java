package com.example.moengagetask.utils;

import static com.example.moengagetask.utils.Constant.MoEAppId;

import android.app.Application;
import android.util.Log;

import com.example.moengagetask.R;
import com.moengage.core.DataCenter;
import com.moengage.core.MoEngage;
import com.moengage.core.config.FcmConfig;
import com.moengage.core.config.NotificationConfig;
import com.moengage.firebase.MoEFireBaseHelper;
import com.moengage.pushbase.listener.TokenAvailableListener;
import com.moengage.pushbase.model.Token;

public class MoEngageApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        initializeMoEngageSDK();
        initMoEngageNotification();
        MoEFireBaseHelper.getInstance().addTokenListener(new TokenAvailableListener() {
            @Override
            public void onTokenAvailable(Token token) {
                MoEFireBaseHelper.getInstance().passPushToken(getApplicationContext(),token.getPushToken());
                Log.e("Roger", "MoEngage Token Pushed");
            }
        });
    }


    private void initializeMoEngageSDK() {
        MoEngage moEngage = new MoEngage.Builder(this, MoEAppId)
                .setDataCenter(DataCenter.DATA_CENTER_1).build();
        MoEngage.initialiseDefaultInstance(moEngage);
        Log.e("Roger", "MoEngage SDK Initialized");
    }

    private void initMoEngageNotification() {
        NotificationConfig notificationConfig = new NotificationConfig(R.drawable.small_icon,
                R.drawable.large_icon, R.color.notification_color,true,true,true);
        MoEngage moEngage = new MoEngage.Builder(this, Constant.MoEAppId)
                .configureNotificationMetaData(notificationConfig)
                .configureFcm(new FcmConfig(false))
                .build();
        MoEngage.initialiseDefaultInstance(moEngage);
        Log.e("Roger", "MoEngage Notification Initialized");
    }
}
