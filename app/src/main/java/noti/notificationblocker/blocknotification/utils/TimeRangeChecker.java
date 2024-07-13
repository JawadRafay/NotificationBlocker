package noti.notificationblocker.blocknotification.utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeRangeChecker {

    public static boolean isCurrentTimeInRange(String startTime, String endTime) {
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTimeString = dateFormat.format(currentTime);

        try {
            // Parse start time
            Date startDate = dateFormat.parse(startTime);
            calendar.setTime(startDate);
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)); // Set the year to current year
            startDate = calendar.getTime();

            // Parse end time
            Date endDate = dateFormat.parse(endTime);
            calendar.setTime(endDate);
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)); // Set the year to current year
            endDate = calendar.getTime();

            // Check if current time is within the range
            Date currentTimeFormatted = dateFormat.parse(currentTimeString);
            return currentTimeFormatted.after(startDate) && currentTimeFormatted.before(endDate) || currentTimeFormatted.equals(startDate) || currentTimeFormatted.equals(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // Return false if there's any parsing exception
        }
    }

}

