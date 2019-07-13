package com.skydhs.czclan.clan.database;

import com.skydhs.czclan.clan.manager.AccessType;
import com.skydhs.czclan.clan.manager.ClanManager;
import com.skydhs.czclan.clan.manager.ClanRole;
import com.skydhs.czclan.clan.manager.ClanSettings;
import com.skydhs.czclan.clan.manager.objects.Clan;
import com.skydhs.czclan.clan.manager.objects.ClanMember;
import com.skydhs.czclan.clan.manager.objects.GeneralStats;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.sql.*;
import java.time.ZonedDateTime;
import java.util.*;

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

        final String query = "INSERT INTO `" + DBManager.CLAN_TABLE + "` (`uuid`, `name`, `tag`, `description`, `creation_date`, `base`, `friendly_fire`, `coins`, `kills`, `deaths`, `kdr`, `clan_members`, `allies`, `rivals`, `leader_name`, `leader_uuid`) VALUES " +
                "('" + clan.getClanUniqueId().toString() + "', " +
                "'" + clan.getName() + "', " +
                "'" + clan.getTag() + "', " +
                "'" + clan.getDescription() + "', " +
                "'" + clan.getCreatedDate().toInstant() + "', " +
                "'" + ClanManager.getManager().serializeLocation(clan.getBase()) + "', " +
                "'" + (clan.isFriendlyFire() ? 0 : 1) + "', " +
                "'" + clan.getCoins() + "', " +
                "'" + clan.getKills() + "', " +
                "'" + clan.getDeaths() + "', " +
                "'" + clan.getKDR() + "', " +
                "'" + ClanManager.getManager().getClanMembers(clan.getMembers()).toString() + "', " +
                "'" + ClanManager.getManager().getClanRelations(clan.getClanAllies()) + "', " +
                "'" + ClanManager.getManager().getClanRelations(clan.getClanRivals()) + "', " +
                "'" + clan.getCreatorName() + "', " +
                "'" + clan.getCreator().toString() + "');";
        executeUpdate(query);
    }

    /**
     * Update clan.
     *
     * @param clan
     */
    public void updateClan(Clan clan) {
        Validate.notNull(clan, "Clan cannot be null.");

        String uuid = clan.getClanUniqueId().toString();

        if (!contains(uuid, DBManager.CLAN_TABLE, "uuid")) {
            saveClan(clan, false);
            return;
        }

        final String query = "UPDATE `" + DBManager.CLAN_TABLE + "` SET `uuid`='" + uuid + "', " +
                "`name`='" + clan.getName() + "', " +
                "`tag`='" + clan.getTag() + "', " +
                "`description`='" + clan.getDescription() + "', " +
                "`creation_date`='" + clan.getCreatedDate().toInstant() + "', " +
                "`base`='" + ClanManager.getManager().serializeLocation(clan.getBase()) + "', " +
                "`friendly_fire`='" + (clan.isFriendlyFire() ? 0 : 1) + "', " +
                "`coins`='" + clan.getCoins() + "', " +
                "`kills`='" + clan.getKills() + "', " +
                "`deaths`='" + clan.getDeaths() + "', " +
                "`kdr`='" + clan.getKDR() + "', " +
                "`clan_members`='" + ClanManager.getManager().getClanMembers(clan.getMembers()).toString() + "', " +
                "`allies`='" + ClanManager.getManager().getClanRelations(clan.getClanAllies()) + "', " +
                "`rivals`='" + ClanManager.getManager().getClanRelations(clan.getClanRivals()) + "', " +
                "`leader_name`='" + clan.getCreatorName() + "', " +
                "`leader_uuid`='" + clan.getCreator().toString() + "' WHERE `uuid`='" + uuid + "';";
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
                    String tag = result.getString(3);
                    tag = (tag == null ? null : ChatColor.translateAlternateColorCodes('&', tag));

                    Clan clan = ClanManager.getManager().getClan(tag);
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
                        String name = result.getString(2);
                        String tag = result.getString(3);
                        String description = result.getString(4);
                        ZonedDateTime creation = ZonedDateTime.parse(result.getString(5));

                        Clan clan = new Clan(name, tag, description, creation);
                        if (clan.isNull()) continue;

                        Location base = ClanManager.getManager().deserializeLocation(result.getString(6));
                        boolean friendlyFire = result.getBoolean(7);
                        GeneralStats stats = new GeneralStats(result.getDouble(8), result.getInt(9), result.getInt(10));
                        List<ClanMember> members = ClanManager.getManager().getClanMembers(result.getString(12));
                        List<String> clanAllies = ClanManager.getManager().getClanRelations(result.getString(13));
                        List<String> clanRivals = ClanManager.getManager().getClanRelations(result.getString(14));
                        String creatorName = result.getString(15);
                        UUID creator = UUID.fromString(result.getString(16));

                        /*
                         * Then, we need load
                         * completely this clan.
                         */
                        clan.load(UUID.fromString(result.getString(1)), creatorName, creator, base, friendlyFire, stats, members, clanAllies, clanRivals);

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

//    /**
//     *
//     * @param name Player name.
//     * @return If player has clan.
//     *   - Null: Player doesn't.
//     *   - {@link Clan}: Player has Clan.
//     */
//    public Clan hasClan(String name) {
//        return null;
//    }

    /**
     * Insert a new member
     * to database.
     *
     * @param member
     * @param contains
     */
    public void insertMember(ClanMember member, boolean contains) {
        if (contains) {
            updateMember(member);
            return;
        }

        Validate.notNull(member, "ClanMember cannot be null.");

        final String query = "INSERT INTO `" + DBManager.CLAN_MEMBERS + "` (`uuid`, `name`, `tag`, `role`, `joined`, `coins`, `kills`, `deaths`) VALUES " +
                "('" + member.getUniqueId().toString() + "', " +
                "'" + member.getName() + "', " +
                "'" + member.getTag() + "', " +
                "'" + member.getRole().getId() + "', " +
                "'" + member.getJoinedDate().toInstant() + "', " +
                "'" + member.getPlayerStats().getCoins() + "', " +
                "'" + member.getPlayerStats().getKills() + "', " +
                "'" + member.getPlayerStats().getDeaths() + "');";
        executeUpdate(query);
    }

    /**
     * Update a member.
     *
     * @param member
     */
    public void updateMember(ClanMember member) {
        Validate.notNull(member, "ClanMember cannot be null.");

        String uuid = member.getUniqueId().toString();

        if (!contains(uuid, DBManager.CLAN_MEMBERS, "uuid")) {
            insertMember(member, false);
            return;
        }

        final String query = "UPDATE `" + DBManager.CLAN_MEMBERS + "` SET `uuid`='" + uuid + "', " +
                "`name`='" + member.getName() + "', " +
                "`tag`='" + member.getTag() + "', " +
                "`role`='" + member.getRole().getId() + "', " +
                "`joined`='" + member.getJoinedDate().toInstant() + "', " +
                "`coins`='" + member.getPlayerStats().getCoins() + "', " +
                "`kills`='" + member.getPlayerStats().getKills() + "', " +
                "`deaths`='" + member.getPlayerStats().getDeaths() + "' WHERE `uuid`='" + uuid + "';";
        executeUpdate(query);
    }

    /**
     * Get clanMember information
     * for a specific player.
     *
     * @param uuid uuid to search.
     * @return ClanMember.
     */
    public ClanMember getClanMember(final UUID uuid) {
        Validate.notNull(uuid, "Player UniqueId cannot be null.");
        if (!contains(uuid.toString(), DBManager.CLAN_MEMBERS, "uuid")) return null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        final String query = "SELECT * FROM `" + DBManager.CLAN_MEMBERS + "` WHERE `uuid`='" + uuid + "';";

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            result = preparedStatement.executeQuery();

            if (result.next()) {
                String name = result.getString(2);
                String tag = result.getString(3);
                ClanRole role = ClanRole.getById(result.getString(4).charAt(0));
                ZonedDateTime joined = ZonedDateTime.parse(result.getString(5));
                GeneralStats stats = new GeneralStats(result.getDouble(6), result.getInt(7), result.getInt(8));
                return new ClanMember(uuid, name, tag, role, joined, stats);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeConnections(connection, result, preparedStatement, null);
        }

        return null;
    }

    /**
     * Get clanMember information
     * for a specific player.
     *
     * @param value name|uuid to search.
     * @param type {@link AccessType}.
     * @return ClanMember.
     */
    public ClanMember getClanMember(final String value, AccessType type) {
        Validate.notNull(value);
        if (!contains(value, DBManager.CLAN_MEMBERS, type.getColumn())) return null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        final String query = "SELECT * FROM `" + DBManager.CLAN_MEMBERS + "` WHERE `" + type.getColumn() + "`='" + value + "';";

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            result = preparedStatement.executeQuery();

            if (result.next()) {
                UUID uuid = UUID.fromString(result.getString(1));
                String tag = result.getString(3);
                ClanRole role = ClanRole.getById(result.getString(4).charAt(0));
                ZonedDateTime joined = ZonedDateTime.parse(result.getString(5));
                GeneralStats stats = new GeneralStats(result.getDouble(6), result.getInt(7), result.getInt(8));
                return new ClanMember(uuid, result.getString(2), tag, role, joined, stats);
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
        final String clan = "CREATE TABLE IF NOT EXISTS `" + DBManager.CLAN_TABLE + "` (`uuid` VARCHAR(36) NOT NULL, `name` VARCHAR(16) NOT NULL, `tag` VARCHAR(16), `description` TINYTEXT, `creation_date` VARCHAR(32), `base` VARCHAR(56), `friendly_fire` BOOLEAN, `coins` DOUBLE(64,2), `kills` INT NOT NULL DEFAULT '0', `deaths` INT NOT NULL DEFAULT '0', `kdr` DOUBLE NOT NULL DEFAULT '0', `clan_members` TEXT, `allies` VARCHAR(255), `rivals` VARCHAR(255), `leader_name` VARCHAR(16) NOT NULL, `leader_uuid` VARCHAR(36) NOT NULL, PRIMARY KEY (`uuid`));";
        executeUpdate(clan);

        // Try to create the clanMembers table.
        final String clanMembers = "CREATE TABLE IF NOT EXISTS `" + DBManager.CLAN_MEMBERS + "` (`uuid` VARCHAR(36) NOT NULL, `name` VARCHAR(16) NOT NULL, `tag` VARCHAR(16), `role` CHAR(1), `joined` VARCHAR(32), `coins` DOUBLE(64,2), `kills` INT NOT NULL DEFAULT '0', `deaths` INT NOT NULL DEFAULT '0', PRIMARY KEY (`uuid`));";
        executeUpdate(clanMembers);
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