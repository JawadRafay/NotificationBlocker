package noti.notificationblocker.blocknotification.Databases.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import noti.notificationblocker.blocknotification.ModelClasses.ChatModel;

import java.util.List;

@Dao
public interface ChatDao {

    @Query("SElECT * FROM chat WHERE chatID =:id")
    LiveData<List<ChatModel>> getChatByID(String id);

    @Query("SElECT * FROM chat WHERE chatID =:id AND isRead=:isRead")
    LiveData<List<ChatModel>> getUnSeenCounter(String id, boolean isRead);

    @Insert
    void addChat(ChatModel model);

    @Delete
    void deleteChat(ChatModel model);

    @Query("UPDATE chat SET isRead =:isRead WHERE chatID =:id")
    void readChat(String id,boolean isRead);

}
