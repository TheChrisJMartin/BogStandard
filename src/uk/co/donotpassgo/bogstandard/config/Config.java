package uk.co.donotpassgo.bogstandard.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class Config {
    public final String pgHost;
    public final String pgPort;
    public final String pgDatabase;
    public final String pgUser;
    public final String pgPassword;
    public final String jdbcUrl;
    public final String baseUrl;
    public final String sessionSecret;
    public final String smtpHost;
    public final int smtpPort;
    public final String smtpUser;
    public final String smtpPass;
    public final String smtpFrom;
    public final String tileUrl;
    public final String tileAttr;
    public final String uploadDir;
    public final List<String> disposableDomains;

    public Config() {
        List<String> missing = new ArrayList<>();
        pgHost = req("PGHOST", missing);
        pgPort = env("PGPORT", "5432");
        pgDatabase = req("PGDATABASE", missing);
        pgUser = req("PGUSER", missing);
        pgPassword = req("PGPASSWORD", missing);
        baseUrl = trimSlash(req("BASE_URL", missing));
        sessionSecret = req("SESSION_SECRET", missing);
        smtpHost = req("SMTP_HOST", missing);
        smtpPort = Integer.parseInt(env("SMTP_PORT", "587"));
        smtpUser = env("SMTP_USER", "");
        smtpPass = env("SMTP_PASS", "");
        smtpFrom = env("SMTP_FROM", "Bog Standard <noreply@donotpassgo.co.uk>");
        tileUrl = env("TILE_URL", "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png");
        tileAttr = env("TILE_ATTR", "&copy; OpenStreetMap contributors");
        uploadDir = env("UPLOAD_DIR", "/var/lib/bogstandard/uploads");
        String dd = env("DISPOSABLE_DOMAINS", "mailinator.com,guerrillamail.com,10minutemail.com,tempmail.com");
        disposableDomains = Arrays.asList(dd.toLowerCase(Locale.ROOT).split("\\s*,\\s*"));
        jdbcUrl = "jdbc:postgresql://" + pgHost + ":" + pgPort + "/" + pgDatabase;
        if (!missing.isEmpty()) {
            throw new IllegalStateException(
                "Bog Standard refused to start — missing required env vars: " + missing
                + ". Set them in setenv.sh (see setenv.example.sh).");
        }
    }

    public boolean isDisposable(String email) {
        int at = email.lastIndexOf('@');
        if (at < 0) return false;
        String domain = email.substring(at + 1).toLowerCase(Locale.ROOT);
        return disposableDomains.contains(domain);
    }

    private static String env(String k, String d) {
        String v = System.getenv(k);
        return (v == null || v.isBlank()) ? d : v.trim();
    }

    private static String req(String k, List<String> missing) {
        String v = System.getenv(k);
        if (v == null || v.isBlank()) missing.add(k);
        return v == null ? "" : v.trim();
    }

    private static String trimSlash(String u) {
        if (u.endsWith("/")) return u.substring(0, u.length() - 1);
        return u;
    }
}
