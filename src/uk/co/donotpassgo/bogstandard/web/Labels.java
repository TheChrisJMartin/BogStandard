package uk.co.donotpassgo.bogstandard.web;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Labels {
    public static final Map<String, String> VENUE_TYPES = map(
        "public_convenience", "Public convenience",
        "pub", "Pub",
        "cafe", "Café",
        "restaurant", "Restaurant",
        "shop", "Shop / shopping centre",
        "shopping_centre", "Shopping centre",
        "supermarket", "Supermarket",
        "park", "Park",
        "station", "Station / transport",
        "petrol", "Petrol station",
        "library_leisure", "Library / leisure",
        "other", "Other"
    );
    public static final Map<String, String> ACCESS = map(
        "free", "Free", "paid", "Paid", "customer_only", "Customer only",
        "radar_key", "RADAR key", "community_scheme", "Community Toilet Scheme", "attended", "Attended"
    );
    public static final Map<String, String> FACILITIES = map(
        "male", "Male", "female", "Female", "gender_neutral", "Gender-neutral",
        "accessible", "Accessible", "baby_change", "Baby-change", "changing_places", "Changing Places",
        "family", "Family", "urinal_only", "Urinal-only"
    );
    public static final String[] AMENITY_KEYS = {
        "toilet_roll", "soap", "sanitiser", "hand_dryer", "paper_towels",
        "baby_change_table", "sanitary_bin", "sharps_bin", "mirror", "hooks",
        "contactless_entry", "water", "radar_key", "changing_places",
        "grab_rails", "emergency_cord", "cord_clear", "level_access",
        "wide_door", "turning_space", "hoist", "adult_bench"
    };
    public static String amenityLabel(String key) {
        return switch (key) {
            case "toilet_roll" -> "Toilet roll";
            case "soap" -> "Soap";
            case "sanitiser" -> "Hand sanitiser / gel";
            case "hand_dryer" -> "Hand dryer";
            case "paper_towels" -> "Paper towels";
            case "baby_change_table" -> "Baby-change table";
            case "sanitary_bin" -> "Sanitary bin";
            case "sharps_bin" -> "Sharps bin";
            case "mirror" -> "Mirror";
            case "hooks" -> "Coat / bag hooks";
            case "contactless_entry" -> "Contactless / coin entry";
            case "water" -> "Water available";
            case "radar_key" -> "RADAR key";
            case "changing_places" -> "Changing Places kit";
            case "grab_rails" -> "Grab rails";
            case "emergency_cord" -> "Emergency cord present";
            case "cord_clear" -> "Emergency cord clear (not tied up)";
            case "level_access" -> "Level / step-free access";
            case "wide_door" -> "Wide door";
            case "turning_space" -> "Turning space";
            case "hoist" -> "Hoist";
            case "adult_bench" -> "Adult-sized changing bench";
            default -> key.replace('_', ' ');
        };
    }
    public static String facility(String t) { return FACILITIES.getOrDefault(t, t); }
    public static String access(String t) { return ACCESS.getOrDefault(t, t); }
    public static String venueType(String t) { return VENUE_TYPES.getOrDefault(t, t); }
    private static Map<String, String> map(String... kv) {
        Map<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) m.put(kv[i], kv[i + 1]);
        return m;
    }
}
