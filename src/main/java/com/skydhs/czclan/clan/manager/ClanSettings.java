package com.skydhs.czclan.clan.manager;

import com.skydhs.czclan.clan.FileUtils;

public class ClanSettings {
    /*
     * This is the max members
     * that this clan can have.
     */
    public static int CLAN_MAX_MEMBERS = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Settings.clan-max-members");

    /*
     * Clan Member leaderboard limit.
     * This is used to get the players
     * that have most kills between
     * his clan members.
     */
    public static int CLAN_MEMBER_LEADERBOARD_LIMIT = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Settings.member-leaderboard-limit");

    /*
     * @CLAN_RELATIONS_SIZE is used
     * to verify the max relations
     * that this clan can have
     * such as @ClanAliases and
     * @ClanRivals.
     */
    public static int CLAN_RELATIONS_SIZE = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Settings.clan-relations-size");

    /*
     * How many entries will be get
     * by our database connection.
     */
    public static int CLAN_LEADERBOARD_LIMIT = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Settings.clan-leaderboad-limit");

    /*
     * Used on the update task.
     * This value below is to
     * determine the task timer.
     */
    public static int CLAN_DELAYED_UPDATE_TASK_MIN = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Settings.update-interval");

    /*
     * Time in minutes to delete
     * the pending clan invitation
     * for a player.
     */
    public static int CLAN_PLAYER_INVITE_EXPIRATION_TIME = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Settings.invite-expiration-time");
}