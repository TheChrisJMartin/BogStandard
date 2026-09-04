package uk.co.donotpassgo.bogstandard.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class Migrator {
    private final Database db;

    public Migrator(Database db) {
        this.db = db;
    }

    public void apply() throws Exception {
        runClasspath("sql/schema.sql");
        runClasspath("sql/seed-gloucestershire.sql");
    }

    private void runClasspath(String path) throws Exception {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (in == null) throw new IllegalStateException("Missing classpath resource " + path);
        String sql;
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            sql = r.lines().collect(Collectors.joining("\n"));
        }
        List<String> statements = splitSql(sql);
        Connection c = db.get();
        try {
            c.setAutoCommit(false);
            try (Statement st = c.createStatement()) {
                for (String s : statements) {
                    String trimmed = s.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("--")) continue;
                    st.execute(trimmed);
                }
            }
            c.commit();
            System.out.println("[bogstandard] applied " + path + " (" + statements.size() + " statements)");
        } catch (Exception e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(true);
            db.release(c);
        }
    }

    static List<String> splitSql(String sql) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inStr = false;
        for (int i = 0; i < sql.length(); i++) {
            char ch = sql.charAt(i);
            if (ch == '\'') {
                inStr = !inStr;
                cur.append(ch);
            } else if (ch == ';' && !inStr) {
                String s = cur.toString().trim();
                if (!s.isEmpty()) out.add(s);
                cur.setLength(0);
            } else {
                cur.append(ch);
            }
        }
        String tail = cur.toString().trim();
        if (!tail.isEmpty()) out.add(tail);
        return out;
    }
}
