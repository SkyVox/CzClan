package com.skydhs.czclan.clan.manager;

public class ClanSettings {
    /*
     * This is the max members
     * that this clan can have.
     */
    public static int CLAN_MAX_MEMBERS = 32;

    /*
     * Clan Member leaderboard limit.
     * This is used to get the players
     * that have most kills between
     * his clan members.
     */
    public static int CLAN_MEMBER_LEADERBOARD_LIMIT = 3;

    /*
     * @CLAN_RELATIONS_SIZE is used
     * to verify the max relations
     * that this clan can have
     * such as @ClanAliases and
     * @ClanRivals.
     */
    public static int CLAN_RELATIONS_SIZE = 10;

    /*
     * How many entries will be get
     * by our database connection.
     */
    public static int CLAN_LEADERBOARD_LIMIT = 10;
}