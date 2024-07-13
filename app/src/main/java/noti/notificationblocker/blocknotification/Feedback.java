package noti.notificationblocker.blocknotification;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import noti.notificationblocker.blocknotification.ModelClasses.Utils;
import noti.notificationblocker.blocknotification.databinding.ActivityFeedbackBinding;

import java.util.HashMap;

public class Feedback extends AppCompatActivity {

    DatabaseReference reference;
    ActivityFeedbackBinding binding;
    private static final String TAG = "Feedback";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getApplicationContext().getResources().getColor(R.color.white));
        init();
    }

    private void init(){
        reference = FirebaseDatabase.getInstance().getReference().child("Feedbacks");
        binding.back.setOnClickListener(v -> finish());

        binding.submit.setOnClickListener(v -> {
            onSubmitClick();
        });

    }


    public void onSubmitClick(){

        if (!Utils.isEmailValid(Utils.getText(binding.email))){
            Utils.showErrorDialogue(Feedback.this,"Enter valid email ");
            return;
        }

        if (Utils.getText(binding.feedbackEt).length()<10){
            Utils.showErrorDialogue(Feedback.this,"Enter valid Feedback ");
            return;
        }

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("Feedback", Utils.getText(binding.feedbackEt));
        hashMap.put("email",Utils.getText(binding.email));
        reference.child(String.valueOf(System.currentTimeMillis())).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        Utils.showSuccessDialogue(Feedback.this,"Submit Successfully");
        binding.email.setText("");
        binding.feedbackEt.setText("");
    }

}