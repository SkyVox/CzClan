package com.skydhs.czclan.clan.database;

import com.skydhs.czclan.clan.manager.ClanManager;
import com.skydhs.czclan.clan.manager.objects.Clan;
import org.apache.commons.lang.Validate;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DBConnection {

    /**
     * Verify if contains the given
     * value on our database.
     *
     * @param value
     * @param table
     * @param column
     * @return if exists the given value.
     */
    public boolean contains(final String value, String table, String column) {
        Validate.notNull(value);

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        final String query = "SELECT `" + column + "` FROM `" + table + "` WHERE `" + column + "`='" + value + "';";

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            result = preparedStatement.executeQuery();
            return result.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeConnections(connection, result, preparedStatement, null);
        }

        return false;
    }

    /**
     * Delete value from database.
     *
     * @param value
     * @param table
     * @param column
     */
    public void delete(final String value, String table, String column) {
        final String query = "DELETE FROM `" + table + "` WHERE `" + column + "`='" + value + "';";
        executeUpdate(query);
    }

    /**
     * Save clan.
     *
     * @param clan
     */
    public void saveClan(Clan clan, boolean contains) {
        if (contains) {
            updateClan(clan);
            return;
        }

        Validate.notNull(clan, "Clan cannot be null.");

        final String query = "INSERT INTO `" + DBManager.CLAN_TABLE + "` (`id`, `clan`, `kills`, `deaths`, `kdr`) VALUES" +
                "('" + clan.getClanUniqueId().toString() + "', " +
                "'" + (Object) clan + "', " +
                "'" + clan.getClanStats().getKills() + "', " +
                "'" + clan.getClanStats().getDeaths() + "', " +
                "'" + clan.getClanStats().getKDR() + "');";
        executeUpdate(query);
    }

    public void updateClan(Clan clan) {
        Validate.notNull(clan, "Clan cannot be null.");

        String id = clan.getClanUniqueId().toString();

        if (!contains(id, DBManager.CLAN_TABLE, "id")) {
            saveClan(clan, false);
            return;
        }

        final String query = "UPDATE `" + DBManager.CLAN_TABLE + "` SET `id`='" + id + "', " +
                "`clan`='" + (Object) clan + "', " +
                "`kills`='" + clan.getClanStats().getKills() + "', " +
                "`deaths`='" + clan.getClanStats().getDeaths() + "', " +
                "`kdr`='" + clan.getClanStats().getKDR() + "' WHERE `id`='" + id + "';";
        executeUpdate(query);
    }

    /**
     * By calling this method
     * you'll get all stored
     * clans on our database.
     *
     * This method should be
     * called once and async.
     *
     * key = uncolored tag.
     * value = respective clan.
     *
     * @return stored clans.
     */
    public Map<String, Clan> getClans() {
        Map<String, Clan> ret = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        final String query = "SELECT * FROM `" + DBManager.CLAN_TABLE + "`;";

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            result = preparedStatement.executeQuery();

            if (result != null) {
                int size = 0;

                if (result.last()) {
                    size = result.getRow();
                    result.beforeFirst();
                }

                ret = new HashMap<>(size);

                while (result.next()) {
                    try {
                        Object object = result.getObject(2);
                        Clan clan = ClanManager.getManager().parseClanObject(object);
                        if (clan == null) continue;
                        ret.put(clan.getUncoloredTag(), clan);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeConnections(connection, result, preparedStatement, null);
        }

        return ret;
    }

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
        // Try to create the clan table.
        final String clan = "CREATE TABLE IF NOT EXISTS `" + DBManager.CLAN_TABLE + "` (`id` VARCHAR(36) NOT NULL, `clan` BLOB, `kills` INT NOT NULL DEFAULT '0', `deaths` INT NOT NULL DEFAULT '0', `kdr` DOUBLE NOT NULL DEFAULT '0', PRIMARY KEY (`id`));";
        executeUpdate(clan);

        // Try to create the playerClan table.
        final String playerClan = "CREATE TABLE IF NOT EXISTS `" + DBManager.PLAYERCLAN_TABLE + "` (`id` VARCHAR(32) NOT NULL, `player_clan` BLOB, PRIMARY KEY (`id`));";
        executeUpdate(playerClan);
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