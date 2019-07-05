package com.skydhs.czclan.clan.database;

import com.skydhs.czclan.clan.manager.ClanManager;
import com.skydhs.czclan.clan.manager.ClanSettings;
import com.skydhs.czclan.clan.manager.objects.Clan;
import com.skydhs.czclan.clan.manager.objects.PlayerClan;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

import java.sql.*;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

        final String query = "INSERT INTO `" + DBManager.CLAN_TABLE + "` (`id`, `uncolored_tag`, `clan`, `kills`, `deaths`, `kdr`) VALUES " +
                "('" + clan.getClanUniqueId().toString() + "', " +
                "'" + clan.getUncoloredTag() + "', " +
                "'" + (Object) clan + "', " +
                "'" + clan.getKills() + "', " +
                "'" + clan.getDeaths() + "', " +
                "'" + clan.getKDR() + "');";
        executeUpdate(query);
    }

    /**
     * Update clan.
     *
     * @param clan
     */
    public void updateClan(Clan clan) {
        Validate.notNull(clan, "Clan cannot be null.");

        String id = clan.getClanUniqueId().toString();

        if (!contains(id, DBManager.CLAN_TABLE, "id")) {
            saveClan(clan, false);
            return;
        }

        final String query = "UPDATE `" + DBManager.CLAN_TABLE + "` SET `id`='" + id + "', " +
                "`clan`='" + clan.getUncoloredTag() + "', " +
                "`clan`='" + (Object) clan + "', " +
                "`kills`='" + clan.getKills() + "', " +
                "`deaths`='" + clan.getDeaths() + "', " +
                "`kdr`='" + clan.getKDR() + "' WHERE `id`='" + id + "';";
        executeUpdate(query);
    }

    /**
     * Get top clans.
     *
     * @param column column to sort
     *               the leaderboard.
     *               available columns
     *               {'kills', 'deaths',
     *               'kdr'}.
     * @return choosed leaderboard.
     */
    public List<Clan> getClanLeaderboard(final String column) {
        List<Clan> ret = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        final String query = "SELECT * FROM `" + DBManager.CLAN_TABLE + "` ORDER BY `" + column + "` DESC LIMIT " + ClanSettings.CLAN_LEADERBOARD_LIMIT + ";";

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            result = preparedStatement.executeQuery();

            if (result != null) {
                ret = new LinkedList<>();

                while (result.next()) {
                    Object object = result.getObject(3);
                    Clan clan = ClanManager.getManager().parseClanObject(object);
                    if (clan == null) continue;
                    ret.add(clan);
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

                ret = new HashMap<>(size * (size / 2));

                while (result.next()) {
                    try {
                        Object object = result.getObject(3);
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
     * Save player info.
     *
     * @param name player name.
     * @param playerClan
     * @param contains if contains this player
     *                 in our db.
     */
    public void savePlayer(final String name, PlayerClan playerClan, boolean contains) {
        if (contains) {
            updatePlayer(name, playerClan);
            return;
        }

        Validate.notNull(playerClan, "PlayerClan cannot be null.");

        final String query = "INSERT INTO `" + DBManager.PLAYERCLAN_TABLE + "` (`player_name`, `last_seen`, `uncolored_tag`) VALUES " +
                "('" + name + "', " +
                "'" + playerClan.getLastSeen().toInstant() + "', " +
                "'" + playerClan.getUncoloredTag() + "');";
        executeUpdate(query);
    }

    /**
     * Update player info.
     *
     * @param name player name.
     * @param playerClan
     */
    public void updatePlayer(final String name, PlayerClan playerClan) {
        Validate.notNull(playerClan, "PlayerClan cannot be null.");

        if (!contains(name, DBManager.PLAYERCLAN_TABLE, "player_name")) {
            savePlayer(name, playerClan, false);
            return;
        }

        final String query = "UPDATE `" + DBManager.PLAYERCLAN_TABLE + "` SET `player_name`='" + name + "', " +
                "`last_seen`='" + playerClan.getLastSeen().toInstant() + "', " +
                "`uncolored_tag`='" + playerClan.getUncoloredTag() + "' WHERE `player_name`='" + name + "';";
        executeUpdate(query);
    }

    /**
     * Get playerClan information
     * for a specific player.
     *
     * @param name name to search.
     * @return PlayerClan.
     */
    public PlayerClan getPlayerClan(String name) {
        Validate.notNull(name, "Player Name cannot be null.");
        if (!contains(name, DBManager.PLAYERCLAN_TABLE, "player_name")) return null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        final String query = "SELECT * FROM `" + DBManager.PLAYERCLAN_TABLE + "` WHERE `player_name`='" + name + "';";

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            result = preparedStatement.executeQuery();

            if (result.next()) {
                ZonedDateTime lastSeen = ZonedDateTime.parse(result.getString(2));
                String tag = result.getString(3);
                return new PlayerClan(result.getString(1), lastSeen, tag);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeConnections(connection, result, preparedStatement, null);
        }

        return null;
    }

    /**
     * Get playerClan information
     * for a specific player.
     *
     * @param player player to search.
     * @return PlayerClan.
     */
    public PlayerClan getPlayerClan(Player player) {
        Validate.notNull(player.getName(), "Player Name cannot be null.");
        if (!contains(player.getName(), DBManager.PLAYERCLAN_TABLE, "player_name")) return null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        final String query = "SELECT * FROM `" + DBManager.PLAYERCLAN_TABLE + "` WHERE `player_name`='" + player.getName() + "';";

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            result = preparedStatement.executeQuery();

            if (result.next()) {
                ZonedDateTime lastSeen = ZonedDateTime.parse(result.getString(2));
                String tag = result.getString(3);
                return new PlayerClan(player, player.getName(), lastSeen, tag);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeConnections(connection, result, preparedStatement, null);
        }

        return null;
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
        final String clan = "CREATE TABLE IF NOT EXISTS `" + DBManager.CLAN_TABLE + "` (`id` VARCHAR(36) NOT NULL, `uncolored_tag` VARCHAR(16) NOT NULL, `clan` BLOB, `kills` INT NOT NULL DEFAULT '0', `deaths` INT NOT NULL DEFAULT '0', `kdr` DOUBLE NOT NULL DEFAULT '0', PRIMARY KEY (`id`));";
        executeUpdate(clan);

        // Try to create the playerClan table.
        final String playerClan = "CREATE TABLE IF NOT EXISTS `" + DBManager.PLAYERCLAN_TABLE + "` (`player_name` VARCHAR(16) NOT NULL, `last_seen` VARCHAR(32), `uncolored_tag` VARCHAR(16) NOT NULL, PRIMARY KEY (`player_name`));";
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