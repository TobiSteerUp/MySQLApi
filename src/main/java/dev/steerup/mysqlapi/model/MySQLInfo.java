package dev.steerup.mysqlapi.model;

public class MySQLInfo {
    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String password;

    public MySQLInfo(String host, int port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public String database() {
        return database;
    }

    public String user() {
        return user;
    }

    public String password() {
        return password;
    }
}