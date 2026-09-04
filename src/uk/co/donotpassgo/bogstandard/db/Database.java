package uk.co.donotpassgo.bogstandard.db;

import uk.co.donotpassgo.bogstandard.config.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayDeque;
import java.util.Deque;

public final class Database {
    private final Config cfg;
    private final Deque<Connection> idle = new ArrayDeque<>();
    private final int max = 8;

    public Database(Config cfg) {
        this.cfg = cfg;
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                "PostgreSQL JDBC driver not on the classpath. Place postgresql.jar in Tomcat lib/.", e);
        }
    }

    public Connection get() throws SQLException {
        synchronized (idle) {
            while (!idle.isEmpty()) {
                Connection c = idle.pollFirst();
                if (c != null && c.isValid(2)) return c;
                closeQuiet(c);
            }
        }
        Connection c = DriverManager.getConnection(cfg.jdbcUrl, cfg.pgUser, cfg.pgPassword);
        c.setAutoCommit(true);
        return c;
    }

    public void release(Connection c) {
        if (c == null) return;
        try {
            if (c.isClosed()) return;
            synchronized (idle) {
                if (idle.size() < max) {
                    idle.addLast(c);
                    return;
                }
            }
        } catch (SQLException ignored) {}
        closeQuiet(c);
    }

    public boolean ping() {
        Connection c = null;
        try {
            c = get();
            try (Statement s = c.createStatement(); ResultSet rs = s.executeQuery("SELECT 1")) {
                return rs.next();
            }
        } catch (Exception e) {
            return false;
        } finally {
            release(c);
        }
    }

    public static void bind(PreparedStatement ps, Object... args) throws SQLException {
        for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
    }

    public static void closeQuiet(AutoCloseable c) {
        if (c == null) return;
        try { c.close(); } catch (Exception ignored) {}
    }
}
