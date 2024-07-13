package noti.notificationblocker.blocknotification.ModelClasses;

import android.app.Notification;
import android.graphics.drawable.Icon;

public class NotificationModel {

    private String name;
    private Icon icon;
    private Notification notification;

    public NotificationModel() {
    }

    public NotificationModel(String name, Notification notification) {
        this.name = name;
        this.notification = notification;
    }

    public NotificationModel(String name, Icon icon, Notification notification) {
        this.name = name;
        this.icon = icon;
        this.notification = notification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }
}
