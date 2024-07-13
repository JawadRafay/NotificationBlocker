package noti.notificationblocker.blocknotification;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import noti.notificationblocker.blocknotification.Adapters.NotificationAdapter;
import noti.notificationblocker.blocknotification.Adapters.SaveNotificationAdapter;
import noti.notificationblocker.blocknotification.Databases.Dao.NotificationDao;
import noti.notificationblocker.blocknotification.Databases.PrefManager;
import noti.notificationblocker.blocknotification.Databases.RoomDatabase;
import noti.notificationblocker.blocknotification.ModelClasses.AdModel;
import noti.notificationblocker.blocknotification.ModelClasses.AllNotifications;
import noti.notificationblocker.blocknotification.ModelClasses.SaveNotifications;
import noti.notificationblocker.blocknotification.databinding.ActivityNotificationListBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;


public class NotificationListActivity extends AppCompatActivity {
    NotificationAdapter adapter;
    SaveNotificationAdapter saveAdapter;
    RoomDatabase roomDatabase;
    NotificationDao notificationDao;
    List<Object> data;
    boolean isSave;
    PrefManager prefManager;
    ActivityNotificationListBinding binding;
    private final int pageSize = 10;
    private int currentPage = 0;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getApplicationContext().getResources().getColor(R.color.white));

        isSave = getIntent().getBooleanExtra("isSave", false);

        init();


        loadData(currentPage);


    }

    private void loadData(int page) {
        if (!isLoading) {
            isLoading = true;
            if (isSave) {
                binding.block.setVisibility(View.GONE);
                binding.alertLayout.setVisibility(View.GONE);

                roomDatabase.notificationDao().getSaveNotification().observe(this, new Observer<List<SaveNotifications>>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChanged(List<SaveNotifications> saveNotifications) {
                        data.clear();
                        int i = 1;

                        for (SaveNotifications notifications : saveNotifications) {
                            if (!prefManager.getIsPremium())
//                                if (i % 7 == 0)
                                if (data.size() % pageSize == 0) {
                                    data.add(new AdModel());
                                }
                            data.add(notifications);
                            binding.empty.setVisibility(saveNotifications.isEmpty()?View.VISIBLE: View.GONE);

                            if (data != null){
                                binding.clearAll.setVisibility(View.VISIBLE);
                            }
                            i++;

                        }
                        binding.shimmerRecycler.hideShimmerAdapter();
                        saveAdapter.notifyDataSetChanged();
                        currentPage++;
                    }
                });
            }
         else {

                boolean today = getIntent().getBooleanExtra("today", false);
                if (today) {

                    binding.save.setVisibility(View.GONE);
                    roomDatabase.notificationDao().getTodayNotification(getTodayStartTime()).observe(this, new Observer<List<AllNotifications>>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onChanged(List<AllNotifications> allNotifications) {
                            data.clear();
                            int i = 1;
                            for (AllNotifications notifications : allNotifications) {
                                if (!prefManager.getIsPremium())
//                                    if (i % 7 == 0)
                                    if (data.size() % pageSize == 0) {
                                        data.add(new AdModel());
                                    }
                                data.add(notifications);
                                binding.empty.setVisibility(allNotifications.isEmpty()?View.VISIBLE: View.GONE);

                                if (data != null){
                                    binding.clearAll.setVisibility(View.VISIBLE);
                                }
                                i++;
                            }
                            binding.shimmerRecycler.hideShimmerAdapter();
                            adapter.notifyDataSetChanged();
                            currentPage++;
                        }
                    });
                } else


                    binding.save.setVisibility(View.GONE);
                roomDatabase.notificationDao().getNotification(pageSize, page * pageSize).observe(this, new Observer<List<AllNotifications>>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChanged(List<AllNotifications> allNotifications) {
                        if (allNotifications != null && allNotifications.size() > 0) {
                            int i = 1;
                            for (AllNotifications notifications : allNotifications) {
                                if (!prefManager.getIsPremium())
//                                    if (i % 7 == 0)
                                    if (data.size() % pageSize == 0) {
                                        data.add(new AdModel());
                                    }
                                data.add(notifications);
                                binding.empty.setVisibility(allNotifications.isEmpty()?View.VISIBLE: View.GONE);

                                i++;
                                if (data != null){
                                    binding.clearAll.setVisibility(View.VISIBLE);
                                }
                            }
                            binding.shimmerRecycler.hideShimmerAdapter();
                            currentPage++;
                            adapter.notifyDataSetChanged();

                        }
                        isLoading = false;
                    }
                });

            }

        }
    }

    private void init(){
        binding.shimmerRecycler.showShimmerAdapter();
        prefManager = new PrefManager(getApplicationContext());
        roomDatabase = RoomDatabase.getInstance(this);
        notificationDao = roomDatabase.notificationDao();

        data = new ArrayList<>();
        adapter = new NotificationAdapter(NotificationListActivity.this,data);

        saveAdapter = new SaveNotificationAdapter(NotificationListActivity.this,data);
        binding.notificationRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        if(isSave)
            binding.notificationRecycler.setAdapter(saveAdapter);

        else {
            binding.notificationRecycler.setAdapter(adapter);
            binding.shimmerRecycler.hideShimmerAdapter();

        }

        binding.backNotification.setOnClickListener(v -> onBackClick());
        binding.clearAll.setOnClickListener(v -> onClearAllClick());

        binding.notificationRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    loadData(currentPage);
                }
            }
        });
    }



    public void onBackClick(){
        finish();
    }

    public void onClearAllClick(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete all notifications");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (isSave) {
                    roomDatabase.notificationDao().deleteAllSaveNotification();
                    data.clear();
                    adapter.notifyDataSetChanged();
                    binding.clearAll.setVisibility(View.INVISIBLE);
                    binding.empty.setVisibility(View.VISIBLE);
                }else
                    roomDatabase.notificationDao().deleteAllNotification();
                data.clear();
                adapter.notifyDataSetChanged();
                binding.clearAll.setVisibility(View.INVISIBLE);
                binding.empty.setVisibility(View.VISIBLE);



            }
        });
        builder.setNegativeButton("No",null);
        builder.create().show();
    }

    private long getTodayStartTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        return calendar.getTimeInMillis();
    }
}