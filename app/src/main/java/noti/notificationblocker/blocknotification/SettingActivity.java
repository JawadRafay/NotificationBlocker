package noti.notificationblocker.blocknotification;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import noti.notificationblocker.blocknotification.databinding.ActivitySettingBinding;


public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    ActivitySettingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getApplicationContext().getResources().getColor(R.color.dashboard_bg));
        binding.back.setOnClickListener(this);
        binding.subscriptions.setOnClickListener(this);
        binding.subscriptionsTap.setOnClickListener(this);
        binding.rateUs.setOnClickListener(this);
        binding.rateUsTap.setOnClickListener(this);
        binding.share.setOnClickListener(this);
        binding.shareTap.setOnClickListener(this);
        binding.feedback.setOnClickListener(this);
        binding.feedbackTap.setOnClickListener(this);
        binding.moreApps.setOnClickListener(this);
        binding.moreAppsTap.setOnClickListener(this);
    }
    
    public void onBackClick(){
        finish();
    }
    
    public void onSubscriptionClick(){
        startActivity(new Intent(getApplicationContext(),AllSubscription.class));
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    public void onFeedbackClick(){
        startActivity(new Intent(getApplicationContext(),Feedback.class));
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    public void onMoreAppsClick(){
        moreApps();
    }

    private void rateTheApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + this.getPackageName())));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }

    private void shareApp() {
        Intent sharelink = new Intent(Intent.ACTION_SEND);
        sharelink.setType("text/*");
        sharelink.putExtra(Intent.EXTRA_SUBJECT,getApplicationContext().getResources().getString(R.string.app_name)+" (Download it From play store)");
        sharelink.putExtra(Intent.EXTRA_TEXT,"http://play.google.com/store/apps/details?id="+getPackageName());
        startActivity(Intent.createChooser(sharelink,"Share with friends via "));
    }

    private void moreApps(){
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:"+getResources().getString(R.string.dev_id))));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=pub:"+getResources().getString(R.string.dev_id))));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back)
        {
            onBackClick();
        } else if (id == R.id.subscriptions || id == R.id.subscriptions_tap) {
            onSubscriptionClick();
        } else if (id == R.id.rateUs || id == R.id.rateUs_tap) {
            rateTheApp();
        } else if (id == R.id.share || id == R.id.share_tap) {
            shareApp();
        }else if (id == R.id.feedback || id == R.id.feedback_tap) {
            onFeedbackClick();
        }else if (id == R.id.moreApps || id == R.id.moreApps_tap) {
            onMoreAppsClick();
        }
    }
}