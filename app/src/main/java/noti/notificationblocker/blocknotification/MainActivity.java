package noti.notificationblocker.blocknotification;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.Observer;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.testing.FakeReviewManager;

import noti.notificationblocker.blocknotification.Databases.PrefManager;
import noti.notificationblocker.blocknotification.Databases.RoomDatabase;
import noti.notificationblocker.blocknotification.ModelClasses.AllNotifications;
import noti.notificationblocker.blocknotification.databinding.ActivityMainBinding;
import noti.notificationblocker.blocknotification.utils.InAppUpdate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PERMISSION_REQUEST_CODE = 123;
    ActivityMainBinding binding;
    private RoomDatabase database;
    private PrefManager prefManager;
    private RoomDatabase roomDatabase;
    private InAppUpdate inAppUpdate;

    private static final String TAG = "MainActivity";

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getApplicationContext().getResources().getColor(R.color.dashboard_bg));

        splashScreen.setOnExitAnimationListener(splashScreenView -> {

            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                }
            });
            prefManager = new PrefManager(this);
            database = RoomDatabase.getInstance(MainActivity.this);

            prefManager.setIsPremium(true);
            if (!prefManager.getIsPermissionSet()) {

                startActivity(new Intent(getApplicationContext(), PermissionActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            else
                splashScreenView.remove();


            splashScreenView.remove();

        });

        inAppUpdate = new InAppUpdate(this);

        inAppUpdate.checkForUpdate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
            }
        }


        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int launchCount = sharedPreferences.getInt("launchCount", 0);
        launchCount++;
        sharedPreferences.edit().putInt("launchCount", launchCount).apply();

        Log.d(TAG, "onCreate: "+launchCount);
        if (launchCount >= 5) {

            Log.d(TAG, "onCreate: Working");

        ReviewManager manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
            request.addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ReviewInfo reviewInfo = task.getResult();
                            Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
                            flow.addOnCompleteListener(flowTask -> {

                            });
                        }
                    });
        }

        init();

        binding.day.setText(getDay());
        binding.date.setText(getDate());

        if (isNightTime())
            binding.imageView3.setImageDrawable(this.getResources().getDrawable(R.drawable.moon));
        else
            binding.imageView3.setImageDrawable(this.getResources().getDrawable(R.drawable.sun));

        getTodayStartTime();

        binding.switcher.setChecked(prefManager.getBlockNotification(), false);

        binding.switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.switcher.setChecked(!binding.switcher.isChecked(), true);
                prefManager.setBlockNotification(binding.switcher.isChecked());
            }
        });

        database.notificationDao().getTodayNotification(getTodayStartTime()).observe(this, new Observer<List<AllNotifications>>() {
            @Override
            public void onChanged(List<AllNotifications> allNotifications) {
                binding.blocked.setText(String.valueOf(allNotifications.size()));
            }
        });

        database.notificationDao().getTodayApps(getTodayStartTime()).observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> allNotifications) {
                binding.apps.setText(String.valueOf(allNotifications.size()));
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        inAppUpdate.onActivityResult(requestCode, resultCode);
    }

    private void init() {
        database = RoomDatabase.getInstance(MainActivity.this);
        prefManager = new PrefManager(getApplicationContext());
        binding.blockNotifications.setOnClickListener(this);
        binding.blockSchNotifications.setOnClickListener(this);
        binding.blockedNotifications.setOnClickListener(this);
        binding.saveNotifications.setOnClickListener(this);
        binding.header.setOnClickListener(this);
        binding.socialMessages.setOnClickListener(this);
        binding.analytics.setOnClickListener(this);
        binding.settingIcon.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.blockNotifications) {
            moveToBlockNotification();
        } else if (id == R.id.blockSchNotifications) {
            moveToBlockScheduleNotification();
        }else if (id == R.id.blockedNotifications) {
            moveToBlockedNotification();
        }else if (id == R.id.saveNotifications) {
            moveToSaveNotification();
        }else if (id == R.id.header) {
            moveToTodayNotification();
        }else if (id == R.id.socialMessages) {
            moveToSocialMessages();
        }else if (id == R.id.analytics) {
            moveToAnalytics();
        }else if (id == R.id.settingIcon) {
            moveToSettings();
        }
    }

    public void moveToBlockNotification() {
        startActivity(new Intent(getApplicationContext(), AllApps.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }
    public void moveToBlockScheduleNotification()
    {
        if (prefManager.getIsPremium())
            startActivity(new Intent(getApplicationContext(), AllSchedules.class));
        else
            startActivity(new Intent(getApplicationContext(), AllSubscription.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void moveToBlockedNotification() {
        startActivity(new Intent(getApplicationContext(), NotificationListActivity.class)
                .putExtra("isSave", false)
                );
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void moveToSaveNotification() {
        if (prefManager.getIsPremium())
            startActivity(new Intent(getApplicationContext(), NotificationListActivity.class).putExtra("isSave", true));
        else
            startActivity(new Intent(getApplicationContext(), AllSubscription.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void moveToTodayNotification() {
        startActivity(new Intent(getApplicationContext(), NotificationListActivity.class)
                .putExtra("isSave", false)
                .putExtra("today", true));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void moveToSocialMessages() {
        if (prefManager.getIsPremium())
            startActivity(new Intent(getApplicationContext(), AllSocialMessages.class));
        else
            startActivity(new Intent(getApplicationContext(), AllSubscription.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void moveToAnalytics() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This feature will be available in next update");
        builder.setTitle("Coming soon");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    public void moveToSettings() {
        startActivity(new Intent(getApplicationContext(), SettingActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public String getDay() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
        return simpleDateFormat.format(currentTime);
    }

    public String getDate() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
        return simpleDateFormat.format(currentTime);
    }

    public boolean isNightTime() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            if (currentTime.after(timeFormat.parse(dateFormat.format(currentTime) + " 19:00")))
                return true;
            else
                return currentTime.before(timeFormat.parse(dateFormat.format(currentTime) + " 06:00"));
        } catch (Exception ignored) {
        }
        return false;
    }

    private long getTodayStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }


    private String getCurrentDate() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
        String outputText = mdformat.format(currentTime);
        return outputText;
    }

    private Boolean isExpired(String compareStringOne) {
        SimpleDateFormat inputParser = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar now = Calendar.getInstance();
        Date date = null;
        Date dateCompareOne = null;
        try {
            date = inputParser.parse(getCurrentDate());
            dateCompareOne = inputParser.parse(compareStringOne);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert date != null;
        return !date.before(dateCompareOne);
    }

    private String getExpireDate(int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, months);
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
        String outputText = mdformat.format(calendar.getTime());
        return outputText;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        inAppUpdate.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        inAppUpdate.onResume();
    }
}