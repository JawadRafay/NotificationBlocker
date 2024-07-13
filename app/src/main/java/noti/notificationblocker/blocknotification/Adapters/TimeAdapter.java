package noti.notificationblocker.blocknotification.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bitvale.switcher.SwitcherX;
import noti.notificationblocker.blocknotification.Databases.RoomDatabase;
import noti.notificationblocker.blocknotification.ModelClasses.TimeModel;
import noti.notificationblocker.blocknotification.ModelClasses.Utils;
import noti.notificationblocker.blocknotification.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.MyChildHolder> {

    private final List<TimeModel> allApps;
    private final Context context;
    private final RoomDatabase roomDatabase;

    public TimeAdapter(List<TimeModel> allApps, Context context) {
        this.allApps = allApps;
        this.context = context;
        roomDatabase = RoomDatabase.getInstance(context);
    }

    @NonNull
    @Override
    public MyChildHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyChildHolder(LayoutInflater.from(context).inflate(R.layout.time_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyChildHolder holder, int position) {
        final TimeModel timeModel = allApps.get(position);
        holder.time.setText(Utils.formatTime12(timeModel.getStart_time()) +" - "+Utils.formatTime12(timeModel.getEnd_time()));
        holder.days.setText(timeModel.getDays());
        holder.isOn.setChecked(timeModel.isIs_on(),true);


        holder.isOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    holder.isOn.setChecked(!holder.isOn.isChecked(), false);
                    roomDatabase.timeDao().updateStatus(holder.isOn.isChecked(),timeModel.getId());
                    roomDatabase.timeDao().updateAllStatusExceptCurrent(false, timeModel.getId());
                    roomDatabase.appDao().deleteApp(timeModel.getApps().toString());

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you wanna delete?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        roomDatabase.timeDao().deleteSchedule(timeModel);
                        allApps.remove(holder.getAdapterPosition());
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return allApps.size();
    }

    public class MyChildHolder extends RecyclerView.ViewHolder {

        private final TextView time;
        private final TextView days;
        private final SwitcherX isOn;
        private final ImageView delete;

        public MyChildHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            days = itemView.findViewById(R.id.days);
            isOn = itemView.findViewById(R.id.is_on);
            delete = itemView.findViewById(R.id.delete);
        }
    }


}
