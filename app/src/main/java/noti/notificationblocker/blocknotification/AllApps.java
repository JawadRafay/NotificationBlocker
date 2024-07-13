package noti.notificationblocker.blocknotification;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import noti.notificationblocker.blocknotification.Adapters.AppAdapter;
import noti.notificationblocker.blocknotification.Adapters.SelectedAppAdapter;
import noti.notificationblocker.blocknotification.Databases.RoomDatabase;
import noti.notificationblocker.blocknotification.ModelClasses.AllNotifications;
import noti.notificationblocker.blocknotification.ModelClasses.AppInfo;
import noti.notificationblocker.blocknotification.databinding.ActivityAllAppsBinding;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
public class AllApps extends AppCompatActivity {


    private PackageManager packageManager;
    private RoomDatabase database;
    private LinkedList<ApplicationInfo> loaded_data, selected_apps;
    private AppAdapter adapter;
    private SelectedAppAdapter selectedAppAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<String> system_apps;

    ActivityAllAppsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllAppsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getApplicationContext().getResources().getColor(R.color.white));

        init();

        database.appDao().getAppsPackages().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> appInfos) {
                AppAdapter.SELECTED_APPS.clear();
                AppAdapter.SELECTED_APPS.addAll(appInfos);
            }
        });

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                readInstalledApp();
            }
        });
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    private void init() {
        binding.shimmerRecyclerAllApps.showShimmerAdapter();
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.recyclerAllApps.setLayoutManager(linearLayoutManager);
        binding.recyclerSelectedApps.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerAllApps.setHasFixedSize(true);
        database = RoomDatabase.getInstance(AllApps.this);
        loaded_data = new LinkedList<>();
        selected_apps = new LinkedList<>();

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


        binding.backApps.setOnClickListener(v -> onBackClick());
    }


    public void onBackClick() {
        finish();
    }

    public void readInstalledApp() {
        packageManager = AllApps.this.getPackageManager();
        List<ApplicationInfo> applicationInfos = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        if (applicationInfos != null && applicationInfos.size() > 0) {
            for (ApplicationInfo applicationInfo : applicationInfos) {
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    if (system_apps.contains(applicationInfo.packageName)) {
                        if (AppAdapter.SELECTED_APPS.contains(applicationInfo.packageName))
                            selected_apps.add(applicationInfo);
                        loaded_data.add(applicationInfo);
                    }
                } else if (!applicationInfo.packageName.equals(AllApps.this.getPackageName())) {
                    if (AppAdapter.SELECTED_APPS.contains(applicationInfo.packageName))
                        selected_apps.add(applicationInfo);
                    loaded_data.add(applicationInfo);
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter = new AppAdapter(loaded_data, AllApps.this);
                    binding.recyclerAllApps.setAdapter(adapter);
                    adapter.setOnItemClickListener(new AppAdapter.onItemClickListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onAdd(ApplicationInfo app) {
                            AppInfo info = new AppInfo();
                            info.setName(app.loadLabel(packageManager).toString());
                            info.setPackageName(app.packageName);
                            database.appDao().addApp(info);
                            selected_apps.add(app);
                            selectedAppAdapter.notifyDataSetChanged();
                        }

                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onRemove(ApplicationInfo app) {
                            if (!database.appDao().getPackages().isEmpty()) {
                                database.appDao().deleteApp(app.packageName);
                                selected_apps.remove(app);
                                selectedAppAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                    selectedAppAdapter = new SelectedAppAdapter(selected_apps, AllApps.this);
                    binding.recyclerSelectedApps.setAdapter(selectedAppAdapter);
                    selectedAppAdapter.setOnItemClickListener(new SelectedAppAdapter.onItemClickListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onRemove(ApplicationInfo app) {
                            database.appDao().deleteApp(app.packageName);
                            selectedAppAdapter.notifyDataSetChanged();
                        }
                    });
                    binding.shimmerRecyclerAllApps.hideShimmerAdapter();
                }
            });
        }
    }


}