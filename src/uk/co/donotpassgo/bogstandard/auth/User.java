package uk.co.donotpassgo.bogstandard.auth;

public final class User {
    public long id;
    public String email;
    public String displayName;
    public boolean verified;
    public String role;
    public boolean suspended;
    public boolean leaderboardOptIn;
    public String passwordHash;
    public String passwordSalt;

    public boolean isStaff() {
        return "moderator".equals(role) || "admin".equals(role);
    }

    public boolean canContribute() {
        return verified && !suspended;
    }
}
