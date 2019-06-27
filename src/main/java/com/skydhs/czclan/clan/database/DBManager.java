package com.skydhs.czclan.clan.database;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;

public class DBManager {
    private static DBManager manager;
    public static DBManager getDBManager() { return manager; }

    static final String TABLE = "skyclan";

    private HikariDataSource hikari;
    private DBConnection connection;

    public DBManager(FileConfiguration file) {
        manager = this;
        this.connection = new DBConnection();

        enable(file);
        DBConnection.setupTable();
    }

    private void enable(FileConfiguration file) {
        this.hikari = new HikariDataSource();

        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", file.getString("MySQL.host"));
        hikari.addDataSourceProperty("port", file.getInt("MySQL.port"));
        hikari.addDataSourceProperty("databaseName", file.getString("MySQL.database"));
        hikari.addDataSourceProperty("user", file.getString("MySQL.username"));
        hikari.addDataSourceProperty("password", file.getString("MySQL.password"));
        hikari.setMaximumPoolSize(10);
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
        } catch (Exception ex) {}
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
}