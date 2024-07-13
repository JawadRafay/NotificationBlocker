package noti.notificationblocker.blocknotification.ModelClasses;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat")
public class ChatModel {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo
    private String chatID;
    @ColumnInfo
    private long time;
    @ColumnInfo
    private String text;
    @ColumnInfo
    private String type;
    @ColumnInfo
    private boolean isRead;

    public ChatModel() {
    }

    public ChatModel(String chatID, long time, String text, String type, boolean isRead) {
        this.chatID = chatID;
        this.time = time;
        this.text = text;
        this.type = type;
        this.isRead = isRead;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }
}
