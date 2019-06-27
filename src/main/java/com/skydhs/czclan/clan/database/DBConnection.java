package com.skydhs.czclan.clan.database;

import java.sql.*;

public class DBConnection {

    /**
     * Executes an MySQL update
     *
     * @param query query to be updated.
     */
    private static void executeUpdate(final String query) {
        Connection connection = null;
        Statement statement = null;

        try {
            connection = getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeConnections(connection, null, null, statement);
        }
    }

    static void setupTable() {
        // TODO: We need to save two datas, @Clan and @PlayerClan.
        String query = "CREATE TABLE IF NOT EXISTS `" + DBManager.TABLE + "` (`owner` VARCHAR(16) NOT NULL, PRIMARY KEY(`owner`));";
        executeUpdate(query);
    }

    static void closeConnections(Connection connection, ResultSet resultSet, PreparedStatement preparedStatement, Statement statement) {
        try {
            if (connection != null) connection.close();
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (statement != null) statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException, NullPointerException {
        return DBManager.getDBManager().getConnection();
    }
}