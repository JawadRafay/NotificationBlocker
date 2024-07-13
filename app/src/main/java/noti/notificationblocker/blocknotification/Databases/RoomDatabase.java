package noti.notificationblocker.blocknotification.Databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.TypeConverters;

import noti.notificationblocker.blocknotification.Databases.Converter.Converter;
import noti.notificationblocker.blocknotification.Databases.Dao.AppDao;
import noti.notificationblocker.blocknotification.Databases.Dao.ChatDao;
import noti.notificationblocker.blocknotification.Databases.Dao.ContactDao;
import noti.notificationblocker.blocknotification.Databases.Dao.NotificationDao;
import noti.notificationblocker.blocknotification.Databases.Dao.TimeDao;
import noti.notificationblocker.blocknotification.ModelClasses.AllNotifications;
import noti.notificationblocker.blocknotification.ModelClasses.AppInfo;
import noti.notificationblocker.blocknotification.ModelClasses.ChatModel;
import noti.notificationblocker.blocknotification.ModelClasses.ContactModel;
import noti.notificationblocker.blocknotification.ModelClasses.SaveNotifications;
import noti.notificationblocker.blocknotification.ModelClasses.TimeModel;

@Database(entities = {AppInfo.class, TimeModel.class, AllNotifications.class, SaveNotifications.class, ContactModel.class, ChatModel.class}, version = 3)
@TypeConverters({Converter.class})
public abstract class RoomDatabase extends androidx.room.RoomDatabase {

    public static final String DATABASE_NAME = "noti_notification_database";
    private static RoomDatabase instance = null;

        public static synchronized RoomDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),RoomDatabase.class,DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract TimeDao timeDao();

    public abstract AppDao appDao();

    public abstract NotificationDao notificationDao();

    public abstract ContactDao contactDao();

    public abstract ChatDao chatDao();

}
