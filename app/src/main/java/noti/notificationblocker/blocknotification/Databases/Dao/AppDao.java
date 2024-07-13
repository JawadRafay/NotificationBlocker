package noti.notificationblocker.blocknotification.Databases.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import noti.notificationblocker.blocknotification.ModelClasses.AppInfo;

import java.util.List;

@Dao
public interface AppDao {

    @Query("SElECT * FROM Apps")
    LiveData<List<AppInfo>> getApps();

    @Query("SElECT packageName FROM Apps")
    LiveData<List<String>> getAppsPackages();

    @Query("SElECT packageName FROM Apps")
    List<String> getPackages();

    @Insert
    void addApp(AppInfo model);

    @Query("DELETE FROM Apps WHERE packageName=:model")
    void deleteApp(String model);

    @Update
    void updateApp(AppInfo model);
}
