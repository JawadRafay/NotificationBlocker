package noti.notificationblocker.blocknotification.ModelClasses;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import noti.notificationblocker.blocknotification.Databases.Converter.Converter;

import java.util.List;

@TypeConverters(Converter.class)
@Entity(tableName = "Schedules")
public class TimeModel {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "end_time")
    private String end_time;

    @ColumnInfo(name = "start_time")
    private String start_time;

    @ColumnInfo(name = "is_on")
    private boolean is_on;

    @ColumnInfo(name = "days")
    private String days;

    @ColumnInfo(name = "apps")
    private List<String> apps;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public boolean isIs_on() {
        return is_on;
    }

    public void setIs_on(boolean is_on) {
        this.is_on = is_on;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public List<String> getApps() {
        return apps;
    }

    public void setApps(List<String> apps) {
        this.apps = apps;
    }

    @NonNull
    public String toString(){
        return (
                "id: " + id + "\n" +
                "end_time: " + end_time + "\n" +
                "start_time: " + start_time + "\n" +
                "is_on: " + is_on + "\n" +
                "days: " + days + "\n" +
                "apps: " + apps
                );
    }
}
