package com.alibaba.cloudpushdemo.application;

import android.app.AlertDialog;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import com.alibaba.cloudpushdemo.bizactivity.MainActivity;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;

public class MainApplication extends Application {
    private static final String TAG = "Init";
    private static MainActivity mainActivity = null;
    private CloudPushService mPushService;
    @Override
    public void onCreate() {
        super.onCreate();
        initCloudChannel(this);
        //测试一下
    }

    /**
     * 初始化云推送通道
     * @param applicationContext
     */
    private void initCloudChannel(final Context applicationContext) {
        // 创建notificaiton channel
        this.createNotificationChannel();
        PushServiceFactory.init(applicationContext);
        mPushService = PushServiceFactory.getCloudPushService();
        mPushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.i(TAG, "----------- init cloudchannel success = "+mPushService.getDeviceId());
                turnOnPush();
                bindTag("13788885555-1" );
                setConsoleText("init cloudchannel success");
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.e(TAG, "-----------  init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
                setConsoleText("init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });

        /*
        MiPushRegister.register(applicationContext, "XIAOMI_ID", "XIAOMI_KEY"); // 初始化小米辅助推送
        HuaWeiRegister.register(applicationContext); // 接入华为辅助推送
        GcmRegister.register(applicationContext, "send_id", "application_id"); // 接入FCM/GCM初始化推送
        */
    }

    /**
     * 绑定标签接口:CloudPushService.bindTag调用示例
     * 1. 标签可以绑定到设备、账号和别名上,此处demo展示的是绑定标签到设备
     * 2. 绑定标签完成后,即可通过标签推送消息
     */
    private void bindTag(final String tag) {
        mPushService.bindTag(CloudPushService.DEVICE_TARGET, new String[]{tag}, null, new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                Log.i(TAG, "---- bind tag " + tag + " success\n");
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                Log.i(TAG, "---- bind tag " + tag + " failed." + "errorCode: " + errorCode + ", errorMsg:" + errorMsg + "\n");
            }
        });
    }

    /**
     * 打开推送通道接口:CloudPushService.turnOnPushChannel调用示例
     * 1. 推送通道默认是打开的,如果没有调用turnOffPushChannel接口关闭推送通道,无法调用该接口
     */
    private void turnOnPush() {
        mPushService.turnOnPushChannel(new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                Log.i(TAG, "---- turn on push channel success\n");
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                Log.i(TAG, "---- turn on push channel failed." + "errorCode: " + errorCode + ", errorMsg:" + errorMsg + "\n");
            }
        });
    }


    public static void setMainActivity(MainActivity activity) {
        mainActivity = activity;
    }

    public static void setConsoleText(String text) {
        if (mainActivity != null && text != null) {
            mainActivity.appendConsoleText(text);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知渠道的id
            String id = "1";
            // 用户可以看到的通知渠道的名字.
            CharSequence name = "notification channel";
            // 用户可以看到的通知渠道的描述
            String description = "notification description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //最后在notificationmanager中创建该通知渠道
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }
}
