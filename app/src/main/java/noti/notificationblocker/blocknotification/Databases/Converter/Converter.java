package noti.notificationblocker.blocknotification.Databases.Converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class Converter {

    @TypeConverter
    public static String toString(List<String> apps){
        return new Gson().toJson(apps);
    }

    @TypeConverter
    public static List<String> fromString(String apps){
        return new Gson().fromJson(apps,new TypeToken<List<String>>(){}.getType());
    }
}
