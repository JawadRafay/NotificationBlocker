package noti.notificationblocker.blocknotification.Databases;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private static final String PREF_NAME = "notiBlockNotifications";
    private static final String BLOCK_NOTIFICATION = "blockNotification";
    private static final String IS_PERMISSION_SET = "ispermission";
    private static final String IS_PREMIUM = "ispremium";
    private static final String IMEI = "imei";
    private static final String SUBSCRIPTION_END_DATE = "subs";
    private static final String SUBSCRIPTION_DATE = "date";
    private static final String EXPIRY = "expiry";
    private static final String SUBSCRIPTION_ID = "subscriptionId";
    private final Context context;

    public PrefManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setBlockNotification(Boolean loginEarlier){
        editor.putBoolean(BLOCK_NOTIFICATION,loginEarlier);
        editor.commit();
    }

    public Boolean getBlockNotification(){
        return sharedPreferences.getBoolean(BLOCK_NOTIFICATION,false);
    }

    public void setIsPermissionSet(Boolean isPermissionSet){
        editor.putBoolean(IS_PERMISSION_SET,isPermissionSet);
        editor.commit();
    }

    public Boolean getIsPermissionSet(){
        return sharedPreferences.getBoolean(IS_PERMISSION_SET,false);
    }

    public void setIsPremium(Boolean isPermissionSet){
        editor.putBoolean(IS_PREMIUM,isPermissionSet);
        editor.commit();
    }

    public Boolean getIsPremium(){
        return sharedPreferences.getBoolean(IS_PREMIUM,false);
    }


    public void setImei(String key){
        editor.putString(IMEI,key);
        editor.commit();
    }

    public String getImei(){
        return sharedPreferences.getString(IMEI,null);
    }


    public void setExpiry(String key){
        editor.putString(EXPIRY,key);
        editor.commit();
    }

    public String getExpiry(){
        return sharedPreferences.getString(EXPIRY, null);
    }

    public void setSubscriptionId(String key){
        editor.putString(SUBSCRIPTION_ID,key);
        editor.commit();
    }

    public String getSubscriptionId(){
        return sharedPreferences.getString(SUBSCRIPTION_ID, null);
    }
}
