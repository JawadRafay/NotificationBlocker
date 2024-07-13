package noti.notificationblocker.blocknotification.Adapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bitvale.switcher.SwitcherX;
import noti.notificationblocker.blocknotification.R;

import java.util.LinkedList;

public class SelectedAppAdapter extends RecyclerView.Adapter<SelectedAppAdapter.MyChildHolder> {

    private final LinkedList<ApplicationInfo> allApps;
    private final Context context;
    private final PackageManager packageManager;
    private SelectedAppAdapter.onItemClickListener onItemClickListener;
    private static final String TAG = "AppAdapter";

    public SelectedAppAdapter(LinkedList<ApplicationInfo> allApps, Context context) {
        this.allApps = allApps;
        this.context = context;
        packageManager = context.getPackageManager();
    }

    @NonNull
    @Override
    public MyChildHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyChildHolder(LayoutInflater.from(context).inflate(R.layout.single_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyChildHolder holder, int position) {

        ApplicationInfo app = allApps.get(position);
        holder.appName.setText(app.loadLabel(this.packageManager));

        holder.appIcon.setImageDrawable(app.loadIcon(this.packageManager));
        holder.isLocked.setVisibility(View.GONE);
//        holder.isLocked.setChecked(true,false);
//        holder.isLocked.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                allApps.remove(holder.getAdapterPosition());
//                notifyDataSetChanged();
//                onItemClickListener.onRemove(app);
//                holder.isLocked.setChecked(false,true);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return allApps.size();
    }

    public class MyChildHolder extends RecyclerView.ViewHolder {

        private final TextView appName;
        private final SwitcherX isLocked;
        private final ImageView appIcon;

        public MyChildHolder(View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.child_image_locker);
            appName = itemView.findViewById(R.id.child_app_locker_name);
            isLocked = itemView.findViewById(R.id.switcher);
        }
    }


    public interface onItemClickListener{
        void onRemove(ApplicationInfo app);
    }

    public void setOnItemClickListener(SelectedAppAdapter.onItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
