package net.square.intect.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public final class DateUtils
{
    private DateUtils()
    {
        throw new RuntimeException("Cannot instantiate utility class");
    }

    public static String calculateTimeAgo(long timestamp)
    {
        LocalDateTime timestampDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        LocalDateTime now = LocalDateTime.now();

        long days = timestampDateTime.until(now, ChronoUnit.DAYS);

        timestampDateTime = timestampDateTime.plusDays(days);

        long hours = timestampDateTime.until(now, ChronoUnit.HOURS);

        String hoursString = hours == 1 ? "one hour" : hours + " hours";

        if (days == 0 && hours > 0)
        {
            return hoursString + " old";

        } else if(days == 0 && hours == 0) {
            return "recently released";

        } else {
            String dayString = days == 1 ? "one day" : days + " days";
            return dayString + " & " + hoursString + " old";
        }
    }
}
