package noti.notificationblocker.blocknotification.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.gson.Gson;
import noti.notificationblocker.blocknotification.Databases.PrefManager;
import noti.notificationblocker.blocknotification.Databases.RoomDatabase;
import noti.notificationblocker.blocknotification.ModelClasses.AllNotifications;
import noti.notificationblocker.blocknotification.ModelClasses.AppInfo;
import noti.notificationblocker.blocknotification.ModelClasses.ChatModel;
import noti.notificationblocker.blocknotification.ModelClasses.ContactModel;
import noti.notificationblocker.blocknotification.ModelClasses.NotificationModel;
import noti.notificationblocker.blocknotification.ModelClasses.TimeModel;
import noti.notificationblocker.blocknotification.utils.TimeRangeChecker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressLint("OverrideAbstract")
public class NotificationService extends NotificationListenerService implements LifecycleEventObserver

{

    private Context context;
    private PrefManager prefManager;
    private RoomDatabase roomDatabase;
    private NotificationManager notificationManager;
    private List<String> blockNotificationsApps;
    private List<TimeModel> allSchedules;
    private SimpleDateFormat dateParser;
    private Observer<List<TimeModel>> observer;
    private final String regx2 = "(.[0-9].(messages).)";
    private final String regx = "^[0-9]+\\snew\\smessages$";
    private String text = "";
    private String title = "";
    private String logo = "";
    private long postTime;
    private Icon icon;
    private PackageManager packageManager;
    public static MutableLiveData<List<StatusBarNotification>> onGoingNotification = new MutableLiveData<>();
    private static final String TAG = "NotificationService";
    private ContactModel model;
    private ChatModel chatModel;
    private List<String> system_apps;
    public static List<NotificationModel> notificationModels;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        roomDatabase = RoomDatabase.getInstance(context);

        prefManager = new PrefManager(context);
        packageManager = context.getPackageManager();
        blockNotificationsApps = new ArrayList<>();
        allSchedules = new ArrayList<>();
        notificationModels = new ArrayList<>();
        system_apps = new ArrayList<>();

        system_apps.add("com.skype.raider");
        system_apps.add("com.viber.voip");
        system_apps.add("com.zhiliaoapp.musically");

        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        blockNotificationsApps = new ArrayList<>();


        observer = new Observer<List<TimeModel>>() {
            @Override
            public void onChanged(List<TimeModel> timeModels) {
                Log.d(TAG, "onChanged: Data received with size: " + timeModels.size());

                for (TimeModel model : timeModels) {
                    Log.d(TAG, "onChanged: TimeModel: " + model.toString());
                }
//
//                allSchedules.clear();
//                allSchedules.addAll(timeModels);
            }
        };
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onListenerConnected() {
        Log.d(TAG, "onListenerConnected: ");
        prefManager.setIsPermissionSet(true);
        roomDatabase.timeDao().getOnSchedules(true, getDay().toLowerCase()).observeForever(observer);
    }

    @Override
    public void onListenerDisconnected()
    {
        Log.d(TAG, "onListenerDisconnected: ");
        prefManager.setIsPermissionSet(false);
        roomDatabase.timeDao().getOnSchedules(true, getDay().toLowerCase()).removeObserver(observer);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
     //   super.onNotificationPosted(sbn);

        blockNotificationsApps.clear();
        dateParser = new SimpleDateFormat("HH:mm", Locale.US);

        String pack = sbn.getPackageName();
        int notification_id = sbn.getId();
        String notification_key = sbn.getKey();
        postTime = sbn.getNotification().when;
        Bundle extras = sbn.getNotification().extras;
        title = extras.getString("android.title");



        roomDatabase.timeDao().getAllSchedules().observeForever(new Observer<List<TimeModel>>() {
            @Override
            public void onChanged(List<TimeModel> timeModels) {
                for(TimeModel model : timeModels){
                    Log.d(TAG, "onChanged: TimeModel: "+"startTime" + model.getStart_time()+" endTime "+model.getEnd_time()+"isTimeInRange "+ TimeRangeChecker.isCurrentTimeInRange(model.getStart_time(),model.getEnd_time())+"isTimerOn"+model.isIs_on());
                    Log.d(TAG, "onChanged: TimeModel: "+"getDays" + model.getDays());
                    Log.d(TAG, "onChanged: TimeModel: "+"CurrentDay" + getDayFirstWord());

                    if(TimeRangeChecker.isCurrentTimeInRange(model.getStart_time(),model.getEnd_time())){
//                        blockNotificationsApps.addAll(model.getApps());
                        for(String app : model.getApps()){
                            AppInfo appInfo = new AppInfo();
                            appInfo.setPackageName(app);

                            if (model.getDays().contains(getDayFirstWord())) {

                                Log.d(TAG, "onChanged: TimeModel: "+"CurrentDays: " + getDayFirstWord() + "Model Day: " + model.getDays());


                                if (model.isIs_on()) {

                                    Log.d(TAG, "onChanged: TimeModel: "+"Notification Blocked: ");

//                                blockNotificationsApps.add(app);
                                    notificationManager.cancel(notification_id);
                                    NotificationService.this.cancelNotification(notification_key);
//                                roomDatabase.appDao().addApp(appInfo);

                                }

                            }
                            else{
//                                if(blockNotificationsApps.contains(app)){
//                                    blockNotificationsApps.remove(app);
//                                }
//                                List<String> packages = roomDatabase.appDao().getPackages();
//                                if(packages.contains(app))
//                                roomDatabase.appDao().deleteApp(app);
                            }
                        }
                    }
                    else {
//                        for(String app : model.getApps()){
//                            Log.e(TAG, "onChanged: getApps(): app : "+app );
//                            AppInfo appInfo = new AppInfo();
//                            appInfo.setPackageName(app);
//                            if(blockNotificationsApps.contains(app)){
//                                blockNotificationsApps.remove(app);
//                            }
//                                List<String> packages = roomDatabase.appDao().getPackages();
//                                if(packages.contains(app))
//                                    roomDatabase.appDao().deleteApp(app);
//                        }

                    }
                }


            }
        });

        try {
            text = extras.getCharSequence("android.text").toString();
        } catch (Exception e) {
            text = "";
        }

        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(pack, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName = (String) (applicationInfo != null ?
                packageManager.getApplicationLabel(applicationInfo) : "(unknown)");

        AllNotifications notifications = new AllNotifications(postTime, title, text, applicationName, "", pack, postTime, false);
        Log.d(TAG, "onNotificationPosted: "+new Gson().toJson(notifications));

        Pattern pattern = Pattern.compile(regx);
        if (text != null) {
            Matcher matcher = pattern.matcher(text);
            if (!sbn.isOngoing())
                if (!matcher.matches() && !text.equals("New message"))
                    if (!(pack.equals("com.whatsapp") && title.equals("WhatsApp")))
                    {
                        Log.d(TAG, "whatsapp: " +new Gson().toJson(notifications));
                        roomDatabase.notificationDao().addNotification(notifications);
                    }
        } else {
            text = "";
        }


            List<String> packages = roomDatabase.appDao().getPackages();
            if (packages != null) {
                blockNotificationsApps.addAll(packages);
            } else {
                Toast.makeText(context, "Database is null", Toast.LENGTH_SHORT).show();
            }



        Bitmap bmp = null;
        try {
            if (extras.containsKey(Notification.EXTRA_LARGE_ICON)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    icon = (Icon) extras.get(Notification.EXTRA_LARGE_ICON);
                else
                    bmp = (Bitmap) extras.get(Notification.EXTRA_LARGE_ICON);
            }
        } catch (Exception e) {
            icon = (Icon) extras.get(Notification.EXTRA_LARGE_ICON);
        }

        if (blockNotificationsApps.contains(pack))
        {
            notificationManager.cancel(notification_id);
            NotificationService.this.cancelNotification(notification_key);
        }

        if (pack.equals("com.whatsapp") || pack.equals("com.whatsapp.w4b")) {
            Pattern pattern2 = Pattern.compile(regx2);
            Matcher matcher2 = pattern2.matcher(title);
            if (title.contains(":")) {
                String tt = matcher2.replaceFirst("");
                String[] tt2 = tt.split(":");
                title = tt2[0].replace("(", "").replace(")", "").trim();
                if (tt2[1].trim().equals("You"))
                    return;
                text = tt2[1].trim() + ": " + text;
            } else {
                if (title.equals("You"))
                    return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (icon != null) {
                    boolean isAvailable = false;
                    for (NotificationModel model : notificationModels) {
                        if (model.getName().equals(title)) {
                            isAvailable = true;
                            break;
                        }
                    }

                    if (!isAvailable) {
                        notificationModels.add(new NotificationModel(title, icon, sbn.getNotification()));
                    }
                    logo = "";
                }
            } else {
                if (bmp != null)
                {
                    boolean isAvailable = false;
                    for (NotificationModel model : notificationModels) {
                        if (model.getName().equals(title)) {
                            isAvailable = true;
                            break;
                        }
                    }

                    if (!isAvailable) {
                        notificationModels.add(new NotificationModel(title, null, sbn.getNotification()));
                    }
                    logo = saveProfile(bmp, title);
                }
            }

            model = new ContactModel(title,
                    logo, text, postTime, "other", pack);
            chatModel = new ChatModel(title, postTime, text, "other", false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (icon != null) {
                    roomDatabase.contactDao().addContact(model);
                    roomDatabase.chatDao().addChat(chatModel);
                }
            } else {
                if (bmp != null) {
                    roomDatabase.contactDao().addContact(model);
                    roomDatabase.chatDao().addChat(chatModel);
                }
            }
        } else if (pack.equals("com.instagram.android")) {
            int start = text.indexOf(")");
            start = start + 2;
            String[] data = text.substring(start).split(":");
            if (data.length == 2) {
                title = data[0].trim();
                text = data[1].trim();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (icon != null) {
                        boolean isAvailable = false;
                        for (NotificationModel model : notificationModels) {
                            if (model.getName().equals(title)) {
                                isAvailable = true;
                                break;
                            }
                        }

                        if (!isAvailable) {
                            notificationModels.add(new NotificationModel(title, icon, sbn.getNotification()));
                        }
                        logo = "";
                    }
                } else {
                    if (bmp != null) {
                        boolean isAvailable = false;
                        for (NotificationModel model : notificationModels) {
                            if (model.getName().equals(title)) {
                                isAvailable = true;
                                break;
                            }
                        }

                        if (!isAvailable) {
                            notificationModels.add(new NotificationModel(title, null, sbn.getNotification()));
                        }
                        logo = saveProfile(bmp, title);
                    }
                }
                model = new ContactModel(title,
                        logo, text, postTime, "other", pack);
                chatModel = new ChatModel(title, postTime, text, "other", false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (icon != null) {
                        roomDatabase.contactDao().addContact(model);
                        roomDatabase.chatDao().addChat(chatModel);
                    }
                } else {
                    if (bmp != null) {
                        roomDatabase.contactDao().addContact(model);
                        roomDatabase.chatDao().addChat(chatModel);
                    }
                }
            }
        } else {
            if (system_apps.contains(pack)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (icon != null) {
                        boolean isAvailable = false;
                        for (NotificationModel model : notificationModels) {
                            if (model.getName().equals(title)) {
                                isAvailable = true;
                                break;
                            }
                        }

                        if (!isAvailable) {
                            notificationModels.add(new NotificationModel(title, icon, sbn.getNotification()));
                        }
                        logo = "";
                    }
                } else {
                    if (bmp != null) {
                        boolean isAvailable = false;
                        for (NotificationModel model : notificationModels) {
                            if (model.getName().equals(title)) {
                                isAvailable = true;
                                break;
                            }
                        }

                        if (!isAvailable) {
                            notificationModels.add(new NotificationModel(title, null, sbn.getNotification()));
                        }
                        logo = saveProfile(bmp, title);
                    }
                }
                model = new ContactModel(title,
                        logo, text, postTime, "other", pack);
                chatModel = new ChatModel(title, postTime, text, "other", false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (icon != null) {
                        roomDatabase.contactDao().addContact(model);
                        roomDatabase.chatDao().addChat(chatModel);
                    }
                } else {
                    if (bmp != null) {
                        roomDatabase.contactDao().addContact(model);
                        roomDatabase.chatDao().addChat(chatModel);
                    }
                }
            }
        }



    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d("Msg", "Notification Removed");

    }

    private String getDay() {
        Date currentTime = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat mdformat = new SimpleDateFormat("EEEE");
        String outputText = mdformat.format(currentTime);
        return outputText;
    }

    private String getDayFirstWord() {
        Date currentTime = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat mdformat = new SimpleDateFormat("EEEE");
        String outputText = String.valueOf(mdformat.format(currentTime).charAt(0));
        return outputText;
    }

    private String getTime() {
        Date currentTime = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
        String outputText = mdformat.format(currentTime);
        return outputText;
    }

    private String saveProfile(Bitmap btp, String file) {
        if (btp != null) {
            String filename = file;
            File f = new File(getApplicationContext().getCacheDir(), filename);
            if (f.exists()) {
                return f.getAbsolutePath();
            }
            FileOutputStream fos = null;
            try {
                f.createNewFile();
                Bitmap bitmap = btp;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] bitmapdata = bos.toByteArray();
                fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
                return f.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "saveProfile: " + e.getMessage());
            }
        }
        return null;
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {

    }


}
