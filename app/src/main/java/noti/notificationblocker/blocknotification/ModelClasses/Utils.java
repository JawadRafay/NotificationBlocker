package noti.notificationblocker.blocknotification.ModelClasses;

import android.content.Context;
import android.widget.EditText;

import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {



    public static String getText(EditText editText){
        return editText.getText().toString().trim();
    }

    public static boolean isEmpty(EditText editText){
        return editText.getText().toString().isEmpty();
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String getDateTime(){
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        return simpleDateFormat.format(currentTime);
    }

    public static String getDate(){
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return simpleDateFormat.format(currentTime);
    }

    public static String formatDateTime(long mili){
        Date currentTime = new Date(mili);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        return simpleDateFormat.format(currentTime);
    }

    public static String formatTime12(String time){
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("hh:mm a");

        try {
            Date date = inputDateFormat.parse(time);
            return outputDateFormat.format(date);
        }catch (Exception e){}
        return "";
    }

    public static void showSuccessDialogue(Context context, String message){
        FancyToast.makeText(context, message, FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
    }

    public static void showErrorDialogue(Context context, String message){
        FancyToast.makeText(context, message, FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
    }

    public static void showInfoDialogue(Context context, String message){
        FancyToast.makeText(context, message, FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();
    }
}
