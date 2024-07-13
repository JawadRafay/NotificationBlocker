package noti.notificationblocker.blocknotification.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import noti.notificationblocker.blocknotification.ModelClasses.AppInfo;
import noti.notificationblocker.blocknotification.ModelClasses.Constants;
import noti.notificationblocker.blocknotification.R;

import java.util.List;

public class CategoryAppAdapter extends RecyclerView.Adapter<CategoryAppAdapter.MyViewHolder> {

    private final Context context;
    private final List<AppInfo> data;
    private CategoryAppAdapter.onItemClickListener onItemClickListener;

    public CategoryAppAdapter(Context context, List<AppInfo> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.app_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        AppInfo model = data.get(position);
        holder.name.setText(model.getName());

        if (model.getName().equals(Constants.SELECTED_APP))
        {
            holder.green_dot.setVisibility(View.VISIBLE);
            holder.name.setTextColor(context.getResources().getColor(R.color.blue2));
        }else{
            holder.green_dot.setVisibility(View.INVISIBLE);
            holder.name.setTextColor(context.getResources().getColor(R.color.color2));
        }

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.SELECTED_APP = model.getName();
                onItemClickListener.onClick(model,holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final LinearLayout green_dot;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            green_dot = itemView.findViewById(R.id.green_dot);
        }
    }

    public interface onItemClickListener
    {
        void onClick(AppInfo model, int position);
    }

    public void setOnItemClickListener(CategoryAppAdapter.onItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
