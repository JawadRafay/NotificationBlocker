package noti.notificationblocker.blocknotification.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import noti.notificationblocker.blocknotification.ModelClasses.ChatModel;
import noti.notificationblocker.blocknotification.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private static final String TAG = "ChatListAdapter";
    private final List<ChatModel> mData;
    private final LayoutInflater mInflater;
    private final ClipboardManager clipboard;
    private final Context context;

    public ChatListAdapter(Context context, List<ChatModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.chat_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ChatModel chat = mData.get(position);

        holder.tvMsg.setText(chat.getText());
        holder.tvMsgMe.setText(chat.getText());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvMsg.setText(Html.fromHtml(chat.getText(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvMsg.setText(Html.fromHtml(chat.getText()));
        }
        Linkify.addLinks(holder.tvMsg, Linkify.ALL);
        holder.tvMsg.setMovementMethod(LinkMovementMethod.getInstance());

        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        String dateString = formatter.format(new Date(chat.getTime()));
        holder.tvTime.setText(dateString);
        holder.tvTimeMe.setText(dateString);

        if (chat.getType().equals("me")){
            holder.tvMsg.setVisibility(View.GONE);
            holder.tvTime.setVisibility(View.GONE);

            holder.tvMsgMe.setVisibility(View.VISIBLE);
            holder.tvTimeMe.setVisibility(View.VISIBLE);
        }else{
            holder.tvMsgMe.setVisibility(View.GONE);
            holder.tvTimeMe.setVisibility(View.GONE);

            holder.tvMsg.setVisibility(View.VISIBLE);
            holder.tvTime.setVisibility(View.VISIBLE);
        }

        holder.tvMsgMe.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData clip = ClipData.newPlainText("message", chat.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "text copied to clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        holder.tvMsg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData clip = ClipData.newPlainText("message", chat.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "text copied to clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvMsg;
        private final TextView tvTime;
        private final TextView tvMsgMe;
        private final TextView tvTimeMe;

        ViewHolder(View itemView) {
            super(itemView);
            tvMsg = itemView.findViewById(R.id.tvMsg);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvMsgMe = itemView.findViewById(R.id.tvMsgMe);
            tvTimeMe = itemView.findViewById(R.id.tvTimeMe);
        }
    }
}
