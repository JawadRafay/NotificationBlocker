package noti.notificationblocker.blocknotification.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import noti.notificationblocker.blocknotification.Databases.RoomDatabase;
import noti.notificationblocker.blocknotification.ModelClasses.AdModel;
import noti.notificationblocker.blocknotification.ModelClasses.SaveNotifications;
import noti.notificationblocker.blocknotification.ModelClasses.Utils;
import noti.notificationblocker.blocknotification.R;

import java.util.List;

public class SaveNotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<Object> data;
    private final Typeface typefaceBold;
    private final Typeface typeface;
    private onItemClickListener onItemClickListener;
    private NativeAd nativeAd;
    private final RoomDatabase roomDatabase;
    private static final String TAG = "NotificationAdapter";

    public SaveNotificationAdapter(Context context, List<Object> data) {
        this.context = context;
        this.data = data;
        typefaceBold = ResourcesCompat.getFont(context, R.font.sfnsbold);
        typeface = ResourcesCompat.getFont(context, R.font.sfnsdisplay);
        roomDatabase = RoomDatabase.getInstance(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationViewHolder(LayoutInflater.from(context).inflate(R.layout.save_notification_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 0){
            AdViewHolder adViewHolder = (AdViewHolder) holder;
            refreshAD(adViewHolder.frameLayout);
        }else{
            SaveNotifications model = (SaveNotifications) data.get(position);
            NotificationViewHolder notificationViewHolder = (NotificationViewHolder) holder;
            notificationViewHolder.app_name.setText(model.getAppName());
            notificationViewHolder.date_time.setText(Utils.formatDateTime(model.getDateTime()));
            notificationViewHolder.message.setText(model.getMessage());
            try {
                Drawable icon = context.getPackageManager().getApplicationIcon(model.getPackageName());
                notificationViewHolder.app_image.setImageDrawable(icon);
            } catch (PackageManager.NameNotFoundException ne) { }
            notificationViewHolder.save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notificationViewHolder.save.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark));
                    SaveNotifications saveNotification = new SaveNotifications();
                    saveNotification.setId(model.getId());
                    roomDatabase.notificationDao().deleteSaveNotification(saveNotification);
                    data.remove(model);
                    notifyDataSetChanged();
                }
            });
            notificationViewHolder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure you want to delete?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            roomDatabase.notificationDao().deleteSaveNotification(model);
                        }
                    });
                    builder.setNegativeButton("No", null);
                    builder.create().show();
                }
            });
            notificationViewHolder.save.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark_fill));

            try {
                notificationViewHolder.app_name.setTypeface(typefaceBold);
                notificationViewHolder.message.setTypeface(typeface);
                notificationViewHolder.date_time.setTypeface(typeface);
            }catch (Exception e){}
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) instanceof AdModel)
            return 0;
        else
            return 1;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {

        private final ImageView app_image;
        private final ImageView save;
        private final ImageView more;
        private final TextView app_name;
        private final TextView date_time;
        private final TextView message;
        private final LinearLayout root;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            app_image = itemView.findViewById(R.id.app_image);
            save = itemView.findViewById(R.id.save);
            more = itemView.findViewById(R.id.more);
            app_name = itemView.findViewById(R.id.app_name);
            date_time = itemView.findViewById(R.id.date_time);
            message = itemView.findViewById(R.id.message);
            root = itemView.findViewById(R.id.root);

        }
    }

    public class AdViewHolder extends RecyclerView.ViewHolder {

        private final FrameLayout frameLayout;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);

            frameLayout = itemView.findViewById(R.id.fl_adplaceholder);
        }
    }

    public interface onItemClickListener{
        void onClick(Object model);
    }

    public void setOnItemClickListener(onItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    private void refreshAD(FrameLayout frameLayout){
        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        AdLoader adLoader = new AdLoader.Builder(context, context.getResources().getString(R.string.NATIVE))
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd NativeAd) {

                        if (nativeAd != null)
                            nativeAd.destroy();

                        nativeAd = NativeAd;

                        // Show the ad.
                        NativeAdView adView =
                                (NativeAdView) LayoutInflater.from(context).inflate(R.layout.ad_unified, null);
                        populateNativeAdView(NativeAd, adView);
                        frameLayout.removeAllViews();
                        frameLayout.addView(adView);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        Log.d(TAG, "onAdFailedToLoad: "+adError.getMessage());
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        .setVideoOptions(videoOptions)
                        .build())
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView(adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

    }
}
