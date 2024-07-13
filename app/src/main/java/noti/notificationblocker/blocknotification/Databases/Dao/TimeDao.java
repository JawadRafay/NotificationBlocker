package noti.notificationblocker.blocknotification.Databases.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import noti.notificationblocker.blocknotification.ModelClasses.TimeModel;

import java.util.List;

@Dao
public interface TimeDao {

    @Query("SELECT * FROM Schedules")
    LiveData<List<TimeModel>> getAllSchedules();

    @Query("SELECT * FROM Schedules WHERE is_on =:isOn AND days LIKE '%' || :days || '%'")
    LiveData<List<TimeModel>> getOnSchedules(boolean isOn, String days);

    @Query("UPDATE Schedules SET is_on =:isOn WHERE id =:id")
    void updateStatus(boolean isOn, int id);

    @Query("UPDATE Schedules SET is_on = :isOn WHERE id != :currentId")
    void updateAllStatusExceptCurrent(boolean isOn, int currentId);


    @Query("UPDATE Schedules SET is_on = :isOn")
    void updateAllStatus(boolean isOn);
    @Insert
    void addSchedule(TimeModel model);

    @Delete
    void deleteSchedule(TimeModel model);

    @Update
    void updateSchedule(TimeModel model);
}
