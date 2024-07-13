package noti.notificationblocker.blocknotification.utils;

import android.app.Application;

import com.onesignal.OneSignal;

public class NotificationBlocker extends Application {

    private static final String ONESIGNAL = "619a75d4-dedb-4f26-a6a0-21086c0a7a4c";

    @Override
    public void onCreate() {
        super.onCreate();

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE,OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL);
//        OneSignal.getNotifications().requestPermission(true, Continue.with(r -> {
//            if (r.isSuccess()) {
//                if (r.getData()) {
//                }
//                else {
//                }
//            }
//            else {
//            }
//        }));


    }
}
