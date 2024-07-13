package noti.notificationblocker.blocknotification.ModelClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import noti.notificationblocker.blocknotification.R;

public class CustomError { //implements ErrorItem

    private final Context context;

    public CustomError(Context context) {
        this.context = context;
    }

//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.error,null);
//        return new RecyclerView.ViewHolder(view){};
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, final OnRepeatListener onRepeatListener) {
//        ImageView btnRepeat = holder.itemView.findViewById(R.id.button);
//        btnRepeat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (onRepeatListener != null) {
//                    onRepeatListener.onClickRepeat(); //call onLoadMore
//                }
//            }
//        });
//    }
}
