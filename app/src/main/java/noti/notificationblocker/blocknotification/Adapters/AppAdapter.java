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

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.MyChildHolder> {

    private final LinkedList<ApplicationInfo> allApps;
    private final Context context;
    public static LinkedList<String> SELECTED_APPS = new LinkedList<>();
    private final PackageManager packageManager;
    private AppAdapter.onItemClickListener onItemClickListener;
    private static final String TAG = "AppAdapter";

    public AppAdapter(LinkedList<ApplicationInfo> allApps, Context context) {
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
        try {
            holder.appIcon.setImageDrawable(app.loadIcon(this.packageManager));
        }catch (Exception e){}
        holder.isLocked.setChecked(SELECTED_APPS.contains(app.packageName),false);
        holder.isLocked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.isLocked.isChecked()){
                    holder.isLocked.setChecked(false,true);
                    onItemClickListener.onRemove(app);
                }else {
                    holder.isLocked.setChecked(true,true);
                    onItemClickListener.onAdd(app);
                }
            }
        });
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
        void onAdd(ApplicationInfo app);
        void onRemove(ApplicationInfo app);
    }

    public void setOnItemClickListener(AppAdapter.onItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
