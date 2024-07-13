package noti.notificationblocker.blocknotification;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import noti.notificationblocker.blocknotification.Adapters.ChatListAdapter;
import noti.notificationblocker.blocknotification.Databases.RoomDatabase;
import noti.notificationblocker.blocknotification.ModelClasses.ChatModel;
import noti.notificationblocker.blocknotification.ModelClasses.NotificationModel;
import noti.notificationblocker.blocknotification.Services.NotificationService;
import noti.notificationblocker.blocknotification.databinding.ActivityChatViewBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatViewActivity extends AppCompatActivity {



    ActivityChatViewBinding binding;
    private RoomDatabase roomDatabase;
    private String name,logo;
    private List<ChatModel> list;
    private ChatListAdapter adapter;
    private Notification notification;
    private static final String TAG = "ChatViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getApplicationContext().getResources().getColor(R.color.white));

        init();

        Intent intent = getIntent();
        if (getIntent().hasExtra("name")){
            name = intent.getStringExtra("name");
            roomDatabase.chatDao().readChat(name,true);
            binding.tvName.setText(name);
            try {
                logo = intent.getStringExtra("logo");
                if (logo != null)
                    binding.ivLogo.setImageURI(Uri.parse(logo));
            }catch (Exception ignored){}


            if (NotificationService.notificationModels !=null)
                for (NotificationModel model : NotificationService.notificationModels){
                    if (model.getName().equals(name)){
                        notification = model.getNotification();
                        binding.sendLayout.setVisibility(View.VISIBLE);
                        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q)
                            binding.ivLogo.setImageIcon(model.getIcon());
                        break;
                    }
                }
        }

        roomDatabase.chatDao().getChatByID(name).observe(this, new Observer<List<ChatModel>>() {
            @Override
            public void onChanged(List<ChatModel> chatModels) {
                if (chatModels.size() != 0){
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    binding.rvChat.setLayoutManager(layoutManager);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    layoutManager.setSmoothScrollbarEnabled(true);
                    layoutManager.setStackFromEnd(true);
                    adapter = new ChatListAdapter(getApplicationContext(), chatModels);
                    binding.rvChat.setAdapter(adapter);
                    Objects.requireNonNull(binding.rvChat.getLayoutManager()).scrollToPosition(list.size()-1);
                }
            }
        });

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.message.getText().toString().trim().isEmpty()){
                    return;
                }

                if (notification != null){
                    ChatModel bean = new ChatModel();
                    bean.setText(binding.message.getText().toString().trim());
                    bean.setTime(System.currentTimeMillis());
                    bean.setType("me");
                    bean.setRead(true);
                    bean.setChatID(name);
                    bean.setTime(System.currentTimeMillis());
                    list.add(bean);
                    adapter.notifyDataSetChanged();

                    roomDatabase.chatDao().addChat(bean);
                    Objects.requireNonNull(binding.rvChat.getLayoutManager()).scrollToPosition(list.size()-1);

                    Notification.Action[] actions = notification.actions;
                    if (actions != null){
                        for (Notification.Action act : actions) {
                            if (act != null && act.getRemoteInputs() != null) {
                                if (act.title.toString().contains("Reply")) {
                                    if (act.getRemoteInputs() != null) {
                                        Log.d(TAG, "onClick:isSent " + sendNativeIntent(getApplicationContext(), act,binding.message.getText().toString().trim()));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    binding.message.setText("");
                    Objects.requireNonNull(binding.rvChat.getLayoutManager()).scrollToPosition(list.size()-1);
                }

            }
        });
    }
    private void init(){
        roomDatabase = RoomDatabase.getInstance(this);
        list = new ArrayList<>();
        binding.sendLayout.setVisibility(View.GONE);
        binding.backIV.setOnClickListener(v -> onBackClick());
    }
    public void onBackClick(){
        finish();
    }

    private boolean sendNativeIntent(Context context, Notification.Action action, String msg) {
        for (android.app.RemoteInput rem : action.getRemoteInputs()) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putCharSequence(rem.getResultKey(), msg);
            android.app.RemoteInput.addResultsToIntent(action.getRemoteInputs(), intent, bundle);
            try {
                action.actionIntent.send(context, 0, intent);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }
}