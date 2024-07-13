package noti.notificationblocker.blocknotification;

import static noti.notificationblocker.blocknotification.Adapters.AppAdapter.SELECTED_APPS;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import noti.notificationblocker.blocknotification.Adapters.AppAdapter;
import noti.notificationblocker.blocknotification.Databases.RoomDatabase;
import noti.notificationblocker.blocknotification.ModelClasses.AppInfo;
import noti.notificationblocker.blocknotification.ModelClasses.TimeModel;
import noti.notificationblocker.blocknotification.ModelClasses.Utils;
import noti.notificationblocker.blocknotification.databinding.ActivityScheduleScreenBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;


public class ScheduleScreenActivity extends AppCompatActivity implements View.OnClickListener{

    private PackageManager packageManager;
    private RoomDatabase database;
    private RecyclerView recycler;
    private LinkedList<ApplicationInfo> loaded_data;
    private AppAdapter adapter;
    private ShimmerRecyclerView shimmerRecycler;
    private LinearLayoutManager linearLayoutManager;
    private TextView monday,tuesday,wednesday,thursday,friday,saturday,sunday,save;
    private View friday_circle,tuesday_circle,wednesday_circle,thursday_circle,saturday_circle,sunday_circle,monday_circle;
    private Calendar calendar;
    private TextView startTime,endTime;
    private String start,end;
    private ImageView back;
    private int hour,mint;
    private List<String> system_apps;
    private static final String TAG = "ScheduleScreen";
    ActivityScheduleScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScheduleScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getApplicationContext().getResources().getColor(R.color.white));

        init();

        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                readInstalledApp();
            }
        });
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();

    }

    private void init(){
        shimmerRecycler = findViewById(R.id.shimmer_recycler_view);
        shimmerRecycler.showShimmerAdapter();
        recycler = findViewById(R.id.recycler);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setHasFixedSize(true);

        database = RoomDatabase.getInstance(ScheduleScreenActivity.this);
        loaded_data = new LinkedList<>();

        system_apps = new ArrayList<>();
        system_apps.add("com.android.browser");
        system_apps.add("com.android.chrome");
        system_apps.add("com.android.settings");
        system_apps.add("com.google.android.youtube");
        system_apps.add("com.android.gallery3d");
        system_apps.add("com.android.providers.downloads.ui");
        system_apps.add("com.android.vending");
        system_apps.add("com.android.incallui");
        system_apps.add("com.android.mms");
        system_apps.add("com.android.providers.downloads");
        system_apps.add("com.google.android.apps.messaging");
        system_apps.add("com.whatsapp");

        back = findViewById(R.id.back);
        save = findViewById(R.id.save);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        monday = findViewById(R.id.monday);
        tuesday = findViewById(R.id.tuesday);
        wednesday = findViewById(R.id.wednesday);
        thursday = findViewById(R.id.thursday);
        friday = findViewById(R.id.friday);
        saturday = findViewById(R.id.saturday);
        sunday = findViewById(R.id.sunday);
        monday_circle = findViewById(R.id.monday_circle);
        tuesday_circle = findViewById(R.id.tuesday_circle);
        wednesday_circle = findViewById(R.id.wednesday_circle);
        thursday_circle = findViewById(R.id.thursday_circle);
        friday_circle = findViewById(R.id.friday_circle);
        saturday_circle = findViewById(R.id.saturday_circle);
        sunday_circle = findViewById(R.id.sunday_circle);

        calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("EEEE");

        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        monday.setText(String.valueOf(mdformat.format(calendar.getTime()).charAt(0)));

        calendar.add(Calendar.DAY_OF_MONTH,+1);
        tuesday.setText(String.valueOf(mdformat.format(calendar.getTime()).charAt(0)));

        calendar.add(Calendar.DAY_OF_MONTH,+1);
        wednesday.setText(String.valueOf(mdformat.format(calendar.getTime()).charAt(0)));

        calendar.add(Calendar.DAY_OF_MONTH,+1);
        thursday.setText(String.valueOf(mdformat.format(calendar.getTime()).charAt(0)));

        calendar.add(Calendar.DAY_OF_MONTH,+1);
        friday.setText(String.valueOf(mdformat.format(calendar.getTime()).charAt(0)));

        calendar.add(Calendar.DAY_OF_MONTH,+1);
        saturday.setText(String.valueOf(mdformat.format(calendar.getTime()).charAt(0)));

        calendar.add(Calendar.DAY_OF_MONTH,+1);
        sunday.setText(String.valueOf(mdformat.format(calendar.getTime()).charAt(0)));

        sunday.setOnClickListener(this);
        monday.setOnClickListener(this);
        tuesday.setOnClickListener(this);
        wednesday.setOnClickListener(this);
        thursday.setOnClickListener(this);
        friday.setOnClickListener(this);
        saturday.setOnClickListener(this);
        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);
        back.setOnClickListener(this);
        save.setOnClickListener(this);
    }

    public void onBackClick(){
        finish();
    }

    @Override
    public void onClick(View v) {
        TimePickerDialog timePickerDialog = null;
        int id = v.getId();
        if (id == R.id.saturday) {
            animateView(saturday_circle, saturday);
        } else if (id == R.id.sunday) {
            animateView(sunday_circle, sunday);
        } else if (id == R.id.monday) {
            animateView(monday_circle, monday);
        } else if (id == R.id.tuesday) {
            animateView(tuesday_circle, tuesday);
        } else if (id == R.id.wednesday) {
            animateView(wednesday_circle, wednesday);
        } else if (id == R.id.thursday) {
            animateView(thursday_circle, thursday);
        } else if (id == R.id.friday) {
            animateView(friday_circle, friday);
        } else if (id == R.id.startTime) {
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            mint = calendar.get(Calendar.MINUTE);
            timePickerDialog = new TimePickerDialog(ScheduleScreenActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            if (hourOfDay < 10 && minute < 10) {
                                start = "0" + hourOfDay + ":0" + minute;
                                startTime.setText(Utils.formatTime12(start));
                            } else if (hourOfDay < 10) {
                                start = "0" + hourOfDay + ":" + minute;
                                startTime.setText(Utils.formatTime12(start));
                            } else if (minute < 10) {
                                start = hourOfDay + ":0" + minute;
                                startTime.setText(Utils.formatTime12(start));
                            } else {
                                start = hourOfDay + ":" + minute;
                                startTime.setText(Utils.formatTime12(start));
                            }
                        }
                    }, hour, mint, false);
            timePickerDialog.show();
        } else if (id == R.id.endTime) {
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            mint = calendar.get(Calendar.MINUTE);
            timePickerDialog = new TimePickerDialog(ScheduleScreenActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            if (hourOfDay < 10 && minute < 10) {
                                end = "0" + hourOfDay + ":0" + minute;
                                endTime.setText(Utils.formatTime12(end));
                            } else if (hourOfDay < 10) {
                                end = "0" + hourOfDay + ":" + minute;
                                endTime.setText(Utils.formatTime12(end));
                            } else if (minute < 10) {
                                end = hourOfDay + ":0" + minute;
                                endTime.setText(Utils.formatTime12(end));
                            } else {
                                end = hourOfDay + ":" + minute;
                                endTime.setText(Utils.formatTime12(end));
                            }

                        }
                    }, hour, mint, false);
            timePickerDialog.show();
        } else if (id == R.id.back) {
            onBackClick();
        } else if (id == R.id.save) {
            saveDB();
        }
    }

    public void readInstalledApp() {
        packageManager = ScheduleScreenActivity.this.getPackageManager();
        List<ApplicationInfo> applicationInfos = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        if (applicationInfos != null && applicationInfos.size()>0){
            for (ApplicationInfo applicationInfo : applicationInfos) {
                if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0){
                    if (system_apps.contains(applicationInfo.packageName))
                        loaded_data.add(applicationInfo);
                }else
                if (!applicationInfo.packageName.equals(ScheduleScreenActivity.this.getPackageName()))
                    loaded_data.add(applicationInfo);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AppAdapter.SELECTED_APPS.clear();
                    adapter = new AppAdapter(loaded_data, ScheduleScreenActivity.this);
                    recycler.setAdapter(adapter);
                    adapter.setOnItemClickListener(new AppAdapter.onItemClickListener() {
                        @Override
                        public void onAdd(ApplicationInfo app) {
                            AppInfo info = new AppInfo();
                            info.setName(app.loadLabel(packageManager).toString());
                            info.setPackageName(app.packageName);

//                                database.appDao().addApp(info);
                                SELECTED_APPS.add(app.packageName);

                        }
                        @Override
                        public void onRemove(ApplicationInfo app) {
//                            database.appDao().deleteApp(app.packageName);
                            SELECTED_APPS.remove(app.packageName);
                        }
                    });
                    shimmerRecycler.hideShimmerAdapter();
                }
            });
        }
    }

    private void saveDB(){

        String days = "";
        if (monday.getCurrentTextColor() == getApplicationContext().getResources().getColor(R.color.black)){
            days = days+" "+monday.getText().toString();
        }
        if (tuesday.getCurrentTextColor() == getApplicationContext().getResources().getColor(R.color.black)){
            days = days+" "+tuesday.getText().toString();
        }
        if (wednesday.getCurrentTextColor() == getApplicationContext().getResources().getColor(R.color.black)){
            days = days+" "+wednesday.getText().toString();
        }
        if (thursday.getCurrentTextColor() == getApplicationContext().getResources().getColor(R.color.black)){
            days = days+" "+thursday.getText().toString();
        }
        if (friday.getCurrentTextColor() == getApplicationContext().getResources().getColor(R.color.black)){
            days = days+" "+friday.getText().toString();
        }
        if (saturday.getCurrentTextColor() == getApplicationContext().getResources().getColor(R.color.black)){
            days = days+" "+saturday.getText().toString();
        }
        if (sunday.getCurrentTextColor() == getApplicationContext().getResources().getColor(R.color.black)){
            days = days+" "+sunday.getText().toString();
        }
        Log.d(TAG, "saveDB:days "+days);
        if (days.isEmpty()) {
            Utils.showErrorDialogue(ScheduleScreenActivity.this,"Select days for blocking notification");
            return;
        }
        if (startTime.getText().toString().isEmpty()){
            Utils.showErrorDialogue(ScheduleScreenActivity.this,"Select start time");
            return;
        }
        if (endTime.getText().toString().isEmpty()){
            Utils.showErrorDialogue(ScheduleScreenActivity.this,"Select end time");
            return;
        }

        TimeModel model = new TimeModel();
        model.setApps(SELECTED_APPS);
        model.setIs_on(true);
        model.setDays(days);
        model.setStart_time(start);
        model.setEnd_time(end);

        database.timeDao().addSchedule(model);
        Utils.showSuccessDialogue(ScheduleScreenActivity.this,"Scheduled Successfully");
//        SELECTED_APPS.clear();

        finish();
    }

    private void animateView(View view, TextView textView){
        if (textView.getCurrentTextColor() == getApplicationContext().getResources().getColor(R.color.gray)){
            view.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.circle_black));
            ViewAnimator
                    .animate(view)
                    .waitForHeight()
                    .duration(400)
                    .dp().width(5,50)
                    .dp().height(5,50)
                    .start();
            ViewAnimator
                    .animate(textView)
                    .textColor(getApplicationContext().getResources().getColor(R.color.gray),getApplicationContext().getResources().getColor(R.color.black))
                    .duration(400)
                    .start();
        }
        else {
            ViewAnimator
                    .animate(view)
                    .waitForHeight()
                    .duration(400)
                    .dp().width(50,5)
                    .dp().height(50,5)
                    .onStop(new AnimationListener.Stop() {
                        @Override
                        public void onStop() {
                            view.setBackground(null);
                        }
                    })
                    .start();

            ViewAnimator
                    .animate(textView)
                    .textColor(getApplicationContext().getResources().getColor(R.color.black),getApplicationContext().getResources().getColor(R.color.gray))
                    .duration(400)
                    .start();
        }
    }


}