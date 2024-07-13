package noti.notificationblocker.blocknotification;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import noti.notificationblocker.blocknotification.Adapters.TimeAdapter;
import noti.notificationblocker.blocknotification.Databases.RoomDatabase;
import noti.notificationblocker.blocknotification.ModelClasses.TimeModel;
import noti.notificationblocker.blocknotification.databinding.ActivityAllSchedulesBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AllSchedules extends AppCompatActivity {

    TimeAdapter adapter;
    RoomDatabase roomDatabase;
    List<TimeModel> data;
    ActivityAllSchedulesBinding binding;
    private SimpleDateFormat dateParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllSchedulesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getApplicationContext().getResources().getColor(R.color.white));

        init();

        roomDatabase.timeDao().getAllSchedules().observe(this, new Observer<List<TimeModel>>() {
            @Override
            public void onChanged(List<TimeModel> timeModels) {
                data.clear();
                data.addAll(timeModels);
                binding.empty.setVisibility(timeModels.isEmpty()?View.VISIBLE: View.GONE);
                adapter.notifyDataSetChanged();

            }
        });

    }


    private void init(){
        roomDatabase = RoomDatabase.getInstance(this);
        binding.scheduleRecycler.setLayoutManager(new LinearLayoutManager(this));
        data = new ArrayList<>();
        adapter = new TimeAdapter(data,this);
        binding.scheduleRecycler.setAdapter(adapter);
        binding.backSchedules.setOnClickListener(v -> onBackClick());
        binding.addSchedule.setOnClickListener(v -> moveToAdd());

    }

    public void onBackClick(){
        finish();
    }

    public void moveToAdd(){
        roomDatabase.timeDao().updateAllStatus(false);
        startActivity(new Intent(getApplicationContext(),ScheduleScreenActivity.class));
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}