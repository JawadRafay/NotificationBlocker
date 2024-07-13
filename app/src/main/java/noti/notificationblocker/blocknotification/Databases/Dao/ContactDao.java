package noti.notificationblocker.blocknotification.Databases.Dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import noti.notificationblocker.blocknotification.ModelClasses.ContactModel;

import java.util.List;

@Dao
public interface ContactDao {

    @Query("SElECT * FROM contact WHERE packageName =:packageName ORDER BY time DESC")
    LiveData<List<ContactModel>> getChatByPackageName(String packageName);

    @Insert(onConflict = REPLACE)
    void addContact(ContactModel model);

    @Delete
    void deleteContact(ContactModel model);

}
