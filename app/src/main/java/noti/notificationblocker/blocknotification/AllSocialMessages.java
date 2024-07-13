package noti.notificationblocker.blocknotification;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import noti.notificationblocker.blocknotification.Adapters.CategoryAppAdapter;
import noti.notificationblocker.blocknotification.Adapters.ChatsAdapter;
import noti.notificationblocker.blocknotification.Databases.RoomDatabase;
import noti.notificationblocker.blocknotification.ModelClasses.AppInfo;
import noti.notificationblocker.blocknotification.ModelClasses.ContactModel;
import noti.notificationblocker.blocknotification.databinding.ActivityAllSocialMessagesBinding;

import java.util.ArrayList;
import java.util.List;

public class AllSocialMessages extends AppCompatActivity {
    private RoomDatabase roomDatabase;
    private List<AppInfo> appInfos;
    private int position = 0;
    private ChatsAdapter chatsAdapter;

    ActivityAllSocialMessagesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllSocialMessagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getApplicationContext().getResources().getColor(R.color.white));

        init();

        setCategoryAppAdapter();

        setContactAdapter(appInfos.get(0).getPackageName());
    }

    private void init(){
        roomDatabase = RoomDatabase.getInstance(this);
        appInfos = new ArrayList<>();
        binding.contactRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.recyclerApps.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        appInfos.add(new AppInfo("WhatsApp","com.whatsapp"));
        appInfos.add(new AppInfo("WhatsApp Business","com.whatsapp.w4b"));
        appInfos.add(new AppInfo("Instagram","com.instagram.android"));
        appInfos.add(new AppInfo("Skype","com.skype.raider"));
        appInfos.add(new AppInfo("Viber","com.viber.voip"));
        appInfos.add(new AppInfo("TikTok","com.zhiliaoapp.musically"));
        binding.backMessages.setOnClickListener(v -> onBackClick());
    }

    private void setCategoryAppAdapter(){
        CategoryAppAdapter adapter = new CategoryAppAdapter(getApplicationContext(), appInfos);
        adapter.setOnItemClickListener(new CategoryAppAdapter.onItemClickListener()
        {
            @Override
            public void onClick(AppInfo model, int pos) {
                position = pos;
                setCategoryAppAdapter();
                setContactAdapter(model.getPackageName());
            }
        });
        binding.recyclerApps.setAdapter(adapter);
        binding.recyclerApps.getLayoutManager().scrollToPosition(position);
    }

    private void setContactAdapter(String packageName)
    {
        roomDatabase.contactDao().getChatByPackageName(packageName).observe(this, new Observer<List<ContactModel>>() {
            @Override
            public void onChanged(List<ContactModel> contactModels) {
                chatsAdapter = new ChatsAdapter(AllSocialMessages.this,contactModels,AllSocialMessages.this);
                binding.contactRecycler.setAdapter(chatsAdapter);
            }
        });
    }

    public void onBackClick(){
        finish();
    }
}