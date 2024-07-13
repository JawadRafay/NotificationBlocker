package noti.notificationblocker.blocknotification.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import noti.notificationblocker.blocknotification.ChatViewActivity;
import noti.notificationblocker.blocknotification.Databases.RoomDatabase;
import noti.notificationblocker.blocknotification.ModelClasses.ChatModel;
import noti.notificationblocker.blocknotification.ModelClasses.ContactModel;
import noti.notificationblocker.blocknotification.ModelClasses.NotificationModel;
import noti.notificationblocker.blocknotification.R;
import noti.notificationblocker.blocknotification.Services.NotificationService;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    private static final String TAG = "ChatsAdapter";

    private final List<ContactModel> mData;
    private final LayoutInflater mInflater;
    private final RoomDatabase roomDatabase;
    private final Context context;
    private final LifecycleOwner owner;

    public ChatsAdapter(Context context, List<ContactModel> data, LifecycleOwner owner) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.owner = owner;
        roomDatabase = RoomDatabase.getInstance(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.chats_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ContactModel model = mData.get(position);
        holder.tvName.setText(model.getName());
        holder.tvMsg.setText(model.getText());
        try {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
                for (NotificationModel notification : NotificationService.notificationModels){
                    if (notification.getName().equals(model.getName())){
                        holder.ivLogo.setImageIcon(notification.getIcon());
                        break;
                    }
                }
            }else
                holder.ivLogo.setImageURI(Uri.parse(model.getLogo()));
        }catch (Exception e){
            holder.ivLogo.setImageDrawable(context.getResources().getDrawable(R.drawable.profile));
        }

        roomDatabase.chatDao().getUnSeenCounter(model.getName(),false).observe(owner, new Observer<List<ChatModel>>() {
            @Override
            public void onChanged(List<ChatModel> chatModels) {
                int size = chatModels.size();
                if (size == 0){
                    holder.rlCounter.setVisibility(View.INVISIBLE);
                }
                else {
                    holder.rlCounter.setVisibility(View.VISIBLE);
                    holder.tvCounter.setText(String.valueOf(size));
                }
            }
        });

        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = formatter.format(new Date(model.getTime()));
        if (getDate().equals(dateFormatter.format(new Date(model.getTime()))))
            holder.tvTime.setText(dateString);
        else {
            holder.tvTime.setText(dateFormatter.format(new Date(model.getTime())));
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInflater.getContext()
                        .startActivity(new Intent(mInflater.getContext(), ChatViewActivity.class)
                                .putExtra("name",model.getName())
                                .putExtra("logo",model.getLogo()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final TextView tvMsg;
        private final TextView tvTime;
        private final TextView tvCounter;
        private final ImageView ivLogo;
        private final RelativeLayout layout;
        private final RelativeLayout rlCounter;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvMsg = itemView.findViewById(R.id.tvMsg);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivLogo = itemView.findViewById(R.id.ivLogo);
            layout = itemView.findViewById(R.id.layout);
            rlCounter = itemView.findViewById(R.id.rlCounter);
            tvCounter = itemView.findViewById(R.id.tvCounter);
        }

    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("h:mm a");
        return format.format(date);
    }

    private String getDate(){
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormatter.format(new Date(Calendar.getInstance().getTimeInMillis()));
    }
}