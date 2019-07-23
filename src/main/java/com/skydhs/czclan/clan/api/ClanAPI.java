package com.skydhs.czclan.clan.api;

import com.skydhs.czclan.clan.manager.ClanManager;
import com.skydhs.czclan.clan.manager.objects.Clan;
import com.skydhs.czclan.clan.manager.objects.ClanMember;
import org.bukkit.entity.Player;

public class ClanAPI {
    private static ClanManager manager;

    static {
        ClanAPI.manager = ClanManager.getManager();
    }

    /**
     * Get {@link ClanMember} object.
     *
     * @param name name to search
     * @return @name clanMember object.
     */
    public static ClanMember getClanMember(String name) {
        return manager.getClanMember(name);
    }

    /**
     * Get the {@link Clan} object.
     *
     * if this player is null or this
     * player doesn't has clan will
     * return a null value.
     *
     * @param player player to search
     * @return {@link Clan} for player.
     */
    public static Clan getClan(Player player) {
        ClanMember member = getClanMember(player.getName());
        if (member == null || !member.hasClan()) return null;
        return member.getClan();
    }

    /**
     * Get the {@link Clan} object.
     *
     * if this uncoloredTag is null it
     * will return a null value.
     *
     * @param uncoloredTag tag to search
     * @return {@link Clan} for uncoloredTag.
     */
    public static Clan getClan(String uncoloredTag) {
        String search = manager.stripColor(uncoloredTag);
        return manager.getClan(search);
    }

    /**
     * Verify if the given @uncoloredTag
     * is a valid clan or not.
     *
     * @param uncoloredTag tag to search
     * @return if @uncoloredTag is a valid clan.
     */
    public static Boolean isClan(String uncoloredTag) {
        String search = manager.stripColor(uncoloredTag);
        return manager.isClan(search);
    }

    /**
     * Compare if two players
     * is on the same clan.
     *
     * @param one player to compare
     * @param two player to compare
     * @return if {@link Player one} is on
     *         the same clan as {@link Player two}
     */
    public static Boolean isSameClan(Player one, Player two) {
        return manager.isSameClan(one, two);
    }
}