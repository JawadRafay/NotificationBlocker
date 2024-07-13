package noti.notificationblocker.blocknotification.ModelClasses;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "all_notifications")
public class AllNotifications {

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
    String packageName;

    @ColumnInfo
    long dateTime;

    @ColumnInfo
    boolean isSave;

    public AllNotifications(long id, String title, String message, String appName, String image, String packageName, long dateTime, boolean isSave) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.appName = appName;
        this.image = image;
        this.packageName = packageName;
        this.dateTime = dateTime;
        this.isSave = isSave;
    }

    public void setId(int id) {
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

    public void setSave(boolean save) {
        isSave = save;
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

    public boolean isSave() {
        return isSave;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
