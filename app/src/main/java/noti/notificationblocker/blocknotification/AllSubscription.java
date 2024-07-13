package noti.notificationblocker.blocknotification;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import noti.notificationblocker.blocknotification.Databases.PrefManager;
import noti.notificationblocker.blocknotification.ModelClasses.Utils;
import noti.notificationblocker.blocknotification.databinding.ActivityAllSubscriptionBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AllSubscription extends AppCompatActivity implements View.OnClickListener {

    ActivityAllSubscriptionBinding binding;
    private String subs = "week";
    private PrefManager prefManager;
    private static final String TAG = "AllSubscription";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllSubscriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getApplicationContext().getResources().getColor(R.color.dashboard_bg));

        init();

    }

    private void init() {
        prefManager = new PrefManager(getApplicationContext());
        binding.lifeTime.setOnClickListener(this);
        binding.monthly.setOnClickListener(this);
        binding.yearly.setOnClickListener(this);
        binding.next.setOnClickListener(this);
        binding.cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.lifeTime) {
            onLifeTimeClick();
        } else if (id == R.id.monthly) {
            onMonthlyClick();
        } else if (id == R.id.yearly) {
            onYearlyClick();
        } else if (id == R.id.cancel) {
            onBackClick();
        } else if (id == R.id.next) {
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
           // onNextClick();
        }
    }


    public void onLifeTimeClick() {
        binding.lifeTime.setBackground(getResources().getDrawable(R.drawable.subscription_select_border));
        binding.monthly.setBackground(getResources().getDrawable(R.drawable.subscription_border));
        binding.yearly.setBackground(getResources().getDrawable(R.drawable.subscription_border));
        subs = "lifetime";
    }

    public void onMonthlyClick() {
        binding.monthly.setBackground(getResources().getDrawable(R.drawable.subscription_select_border));
        binding.yearly.setBackground(getResources().getDrawable(R.drawable.subscription_border));
        binding.lifeTime.setBackground(getResources().getDrawable(R.drawable.subscription_border));
        subs = "month";
    }

    public void onYearlyClick() {
        binding.yearly.setBackground(getResources().getDrawable(R.drawable.subscription_select_border));
        binding.monthly.setBackground(getResources().getDrawable(R.drawable.subscription_border));
        binding.lifeTime.setBackground(getResources().getDrawable(R.drawable.subscription_border));
        subs = "year";
    }



    public void onBackClick() {
        finish();
    }

    @SuppressLint("MissingPermission")
    private String getIMEI() {
        String myAndroidDeviceId = "";
        TelephonyManager mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        myAndroidDeviceId = mTelephony.getDeviceId();
        return myAndroidDeviceId;
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
    }


}