package noti.notificationblocker.blocknotification;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import noti.notificationblocker.blocknotification.Databases.PrefManager;
import noti.notificationblocker.blocknotification.databinding.ActivityPermissionBinding;

public class PermissionActivity extends AppCompatActivity {

    public final int PERMISSION_CODE = 32141;
    public final int MY_IGNORE_OPTIMIZATION_REQUEST = 23232;
    private PowerManager pm;
    private PrefManager prefManager;
    private ActivityPermissionBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPermissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getApplicationContext().getResources().getColor(R.color.white));

        init();

        setRequestPermissions();
    }

    private void init(){
        prefManager = new PrefManager(this);
        pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);

        binding.allow.setOnClickListener(v -> onAllowClick());
        binding.next.setOnClickListener(v -> setRequestPermissions());
    }

    public void onAllowClick(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (pm.isIgnoringBatteryOptimizations(this.getPackageName()))
                notificationPermission();
            else
                batteryOptimisationIntent();
        }
    }

    private void notificationPermission(){
        if (prefManager.getIsPermissionSet()) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            finish();
        } else{
            if (Build.VERSION_CODES.LOLLIPOP_MR1 >= Build.VERSION.SDK_INT){
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else {
                Intent in = new Intent(
                        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(in);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (binding.textView6.getText().equals("Notification Listener"))
            if (prefManager.getIsPermissionSet())
                binding.allow.setVisibility(View.GONE);
    }

    private void batteryOptimisationIntent() {
        try {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:"+this.getPackageName()));
            startActivityForResult(intent, MY_IGNORE_OPTIMIZATION_REQUEST);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_IGNORE_OPTIMIZATION_REQUEST && resultCode == RESULT_OK) {
            binding.allow.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "Permission not given", Toast.LENGTH_SHORT).show();
        }
    }

    public void setRequestPermissions(){
        if (binding.textView6.getText().equals("Notification Listener"))
            if (prefManager.getIsPermissionSet())
                binding.allow.setVisibility(View.GONE);
            else
                binding.allow.setVisibility(View.VISIBLE);
        else
            binding.allow.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (pm.isIgnoringBatteryOptimizations(this.getPackageName())){
                if (prefManager.getIsPermissionSet()){
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    finish();
                }else{
                    binding.textView6.setText("Notification Listener");
                    binding.textView7.setText("You need to give Notification Listener permissions to work app properly and save all notification.");
                    binding.imageView5.setImageDrawable(this.getResources().getDrawable(R.drawable.smartphone));
                }
            }else{
                binding.textView6.setText("Battery Optimisations");
                binding.textView7.setText("You need to give Battery Optimisations permissions to prevent app from turn off.");
                binding.imageView5.setImageDrawable(this.getResources().getDrawable(R.drawable.battery_level));
            }
        }
    }
}