package uk.co.donotpassgo.bogstandard.util;

public final class Geo {
    private Geo() {}

    public static double haversineKm(double lat1, double lng1, double lat2, double lng2) {
        double r = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return r * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    public static String walkLabel(double km) {
        int metres = (int) Math.round(km * 1000);
        if (metres < 80) return "under 1 min walk";
        int mins = Math.max(1, (int) Math.round(metres / 80.0));
        if (mins == 1) return "1 min walk";
        return mins + " min walk";
    }

    public static int bucket(double lat) {
        return (int) Math.round(lat * 1000);
    }
}
