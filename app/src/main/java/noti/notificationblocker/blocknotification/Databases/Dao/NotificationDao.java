package noti.notificationblocker.blocknotification.Databases.Dao;

import static androidx.room.OnConflictStrategy.IGNORE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import noti.notificationblocker.blocknotification.ModelClasses.AllNotifications;
import noti.notificationblocker.blocknotification.ModelClasses.SaveNotifications;

import java.util.List;

@Dao
public interface NotificationDao {

    //All Notifications
    @Query("SELECT * FROM all_notifications ORDER BY id DESC LIMIT :pageSize OFFSET :offset")
    LiveData<List<AllNotifications>> getNotification(int pageSize, int offset);



    @Query("SELECT * FROM all_notifications ORDER By id DESC ")
    List<AllNotifications> getNotificationOff();

    @Insert(onConflict = IGNORE)
    void addNotification(AllNotifications notification);

    @Delete
    void deleteNotification(AllNotifications notification);

    @Query("DELETE FROM all_notifications")
    void deleteAllNotification();

    @Query("DELETE FROM all_notifications WHERE dateTime<:miliSeconds")
    void deletePreviousNotification(long miliSeconds);

    @Query("SELECT * FROM all_notifications WHERE dateTime>=:miliSeconds ORDER By id DESC")
    LiveData<List<AllNotifications>> getTodayNotification(long miliSeconds);

    @Query("SELECT DISTINCT packageName FROM all_notifications WHERE dateTime>=:miliSeconds")
    LiveData<List<String>> getTodayApps(long miliSeconds);

    //Save Notifications
    @Query("SELECT * FROM save_notifications ORDER By id DESC")
    LiveData<List<SaveNotifications>> getSaveNotification();

    @Insert(onConflict = IGNORE)
    void addSaveNotification(SaveNotifications notification);

    @Delete
    void deleteSaveNotification(SaveNotifications notification);

    @Query("SELECT * FROM save_notifications WHERE id =:id")
    SaveNotifications isSave(long id);

    @Query("DELETE FROM save_notifications")
    void deleteAllSaveNotification();
}
