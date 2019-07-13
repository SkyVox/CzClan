package com.skydhs.czclan.clan.database;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DBManager {
    private static DBManager manager;

    /*
     * Database table names.
     */
    public static final String CLAN_TABLE = "skyclan";
    public static final String CLAN_MEMBERS = "clanmember";

    private DBConnection connection;
    private HikariDataSource hikari;

    private Boolean enabled = false;

    public DBManager() {
        manager = this;
        this.connection = new DBConnection();
    }

    public void enable(String host, int port, String database, String user, String password) {
        this.hikari = new HikariDataSource();

        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", host);
        hikari.addDataSourceProperty("port", port);
        hikari.addDataSourceProperty("databaseName", database);
        hikari.addDataSourceProperty("user", user);
        hikari.addDataSourceProperty("password", password);
        hikari.setMaximumPoolSize(10);

        // Then, setup our table.
        DBConnection.setupTable();

        // Update @enabled.
        this.enabled = true;
    }

    /**
     * Called once when servers
     * close.
     */
    public void disable() {
        try {
            if (hikari != null) {
                hikari.close();
            }
            this.enabled = false;
        } catch (Exception ex) {}
    }

    public Boolean isEnabled() {
        return enabled;
    }

    /**
     * Get connection
     *
     * @return hikari connection
     * @throws SQLException
     * @throws NullPointerException
     */
    public Connection getConnection() throws SQLException, NullPointerException {
        return hikari.getConnection();
    }

    /**
     * Get the database connection.
     *
     * @return return connection
     */
    public DBConnection getDBConnection() {
        return connection;
    }

    public static DBManager getDBManager() {
        return manager;
    }
}