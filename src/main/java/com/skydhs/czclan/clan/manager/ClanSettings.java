package com.skydhs.czclan.clan.manager;

import com.skydhs.czclan.clan.FileUtils;

public class ClanSettings {
    /*
     * When boolean values
     * are true.
     */
    public static final String BOOLEAN_TRUE = FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.boolean-true").get();

    /*
     * When boolean values
     * are false.
     */
    public static final String BOOLEAN_FALSE = FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.boolean-false").get();

    /*
     * The max characters size
     * that the clan tag can
     * have.
     */
    public static final int CLAN_TAG_MAX_SIZE = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Settings.clan-max-tag-size");

    /*
     * The min characters size
     * that the clan tag can
     * have.
     */
    public static final int CLAN_TAG_MIN_SIZE = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Settings.clan-min-tag-size");

    /*
     * The max characters size
     * that the clan name can
     * have.
     */
    public static final int CLAN_NAME_MAX_SIZE = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Settings.clan-name-max-size");

    /*
     * The min characters size
     * that the clan name can
     * have.
     */
    public static final int CLAN_NAME_MIN_SIZE = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Settings.clan-name-min-size");

    /*
     * This is the max members
     * that this clan can have.
     */
    public static final int CLAN_MAX_MEMBERS = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Settings.clan-max-members");

    /*
     * Clan Member leaderboard limit.
     * This is used to get the players
     * that have most kills between
     * his clan members.
     */
    public static final int CLAN_MEMBER_LEADERBOARD_LIMIT = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Settings.member-leaderboard-limit");

    /*
     * @CLAN_RELATIONS_SIZE is used
     * to verify the max relations
     * that this clan can have
     * such as @ClanAliases and
     * @ClanRivals.
     */
    public static final int CLAN_RELATIONS_SIZE = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Settings.clan-relations-size");

    /*
     * How many entries will be get
     * by our database connection.
     */
    public static final int CLAN_LEADERBOARD_LIMIT = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Settings.clan-leaderboad-limit");

    /*
     * Used on the update task.
     * This value below is to
     * determine the task timer.
     */
    public static final int CLAN_DELAYED_UPDATE_TASK_MIN = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Settings.update-interval");

    /*
     * Time in minutes to delete
     * the pending clan invitation
     * for a player.
     */
    public static final int CLAN_PLAYER_INVITE_EXPIRATION_TIME = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Settings.invite-expiration-time");

    /*
     * This Regex will be used
     * on clan tag and name.
     */
    public static final String CLAN_NAMES_REGEX = "[0-9a-zA-Z]*";
}