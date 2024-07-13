package noti.notificationblocker.blocknotification.ModelClasses;


import android.graphics.drawable.Drawable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Apps")
public class AppInfo {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "packageName")
    private String packageName;
    @Ignore
    private Drawable icon;

    @Ignore
    public AppInfo() {
    }

    public AppInfo(int id, String name, String packageName) {
        this.id = id;
        this.name = name;
        this.packageName = packageName;
    }

    @Ignore
    public AppInfo(String name, String packageName) {
        this.name = name;
        this.packageName = packageName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}

