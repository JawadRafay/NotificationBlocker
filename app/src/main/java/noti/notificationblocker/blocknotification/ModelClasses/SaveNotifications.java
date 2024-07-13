package noti.notificationblocker.blocknotification.ModelClasses;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "save_notifications")
public class SaveNotifications {

    @PrimaryKey
    long id;

    @ColumnInfo
    String title;

    @ColumnInfo
    String message;

    @ColumnInfo
    String appName;

    @ColumnInfo
    String image;

    @ColumnInfo
    long dateTime;

    @ColumnInfo
    String packageName;

    @Ignore
    public SaveNotifications() {
    }

    public SaveNotifications(long id, String title, String message, String appName, String image, long dateTime, String packageName) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.appName = appName;
        this.image = image;
        this.dateTime = dateTime;
        this.packageName = packageName;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getAppName() {
        return appName;
    }

    public String getImage() {
        return image;
    }

    public long getDateTime() {
        return dateTime;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
