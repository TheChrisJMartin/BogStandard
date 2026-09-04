package uk.co.donotpassgo.bogstandard.util;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Hours {
    private static final ZoneId UK = ZoneId.of("Europe/London");
    private static final Pattern DAY = Pattern.compile(
        "\"(mon|tue|wed|thu|fri|sat|sun)\"\\s*:\\s*\\[\\s*\"([^\"]+)\"\\s*,\\s*\"([^\"]+)\"\\s*\\]",
        Pattern.CASE_INSENSITIVE);

    private Hours() {}

    public static boolean isUnknown(String raw) {
        return raw == null || raw.isBlank() || "unknown".equalsIgnoreCase(raw.trim());
    }

    public static Boolean openNow(String raw) {
        if (isUnknown(raw)) return null;
        ZonedDateTime now = ZonedDateTime.now(UK);
        String key = dayKey(now.getDayOfWeek());
        Matcher m = DAY.matcher(raw);
        while (m.find()) {
            if (!key.equalsIgnoreCase(m.group(1))) continue;
            String from = m.group(2);
            String to = m.group(3);
            if ("closed".equalsIgnoreCase(from) || "closed".equalsIgnoreCase(to)) return false;
            if ("dusk".equalsIgnoreCase(to)) to = "20:00";
            if ("dawn".equalsIgnoreCase(from) || "dusk".equalsIgnoreCase(from)) from = "08:00";
            try {
                LocalTime start = LocalTime.parse(from);
                LocalTime end = LocalTime.parse(to);
                LocalTime t = now.toLocalTime();
                if (!end.isAfter(start)) return !t.isBefore(start) || t.isBefore(end) || t.equals(end);
                return !t.isBefore(start) && t.isBefore(end);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static String summary(String raw) {
        if (isUnknown(raw)) return "Hours unknown";
        Boolean open = openNow(raw);
        if (open == null) return "Hours listed";
        return open ? "Open now" : "Closed now";
    }

    private static String dayKey(DayOfWeek d) {
        return switch (d) {
            case MONDAY -> "mon";
            case TUESDAY -> "tue";
            case WEDNESDAY -> "wed";
            case THURSDAY -> "thu";
            case FRIDAY -> "fri";
            case SATURDAY -> "sat";
            case SUNDAY -> "sun";
        };
    }
}
