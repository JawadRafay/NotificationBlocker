package noti.notificationblocker.blocknotification.ModelClasses;

import android.graphics.drawable.Icon;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "contact")
public class ContactModel {

    @PrimaryKey
    @NonNull
    private String name;
    @ColumnInfo
    private String logo;
    @ColumnInfo
    private long time;
    @ColumnInfo
    private String text;
    @ColumnInfo
    private String type;
    @ColumnInfo
    private String packageName;
    @Ignore
    private Icon icon;

    public ContactModel() {
    }

    public ContactModel(String name, String logo, String text, long time, String type, String packageName) {
        this.name = name;
        this.logo = logo;
        this.text = text;
        this.time = time;
        this.type = type;
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
