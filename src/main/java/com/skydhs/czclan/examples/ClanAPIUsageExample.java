package com.skydhs.czclan.examples;

import com.skydhs.czclan.clan.api.ClanAPI;
import com.skydhs.czclan.clan.manager.objects.Clan;
import com.skydhs.czclan.clan.manager.objects.ClanMember;
import org.bukkit.entity.Player;

public class ClanAPIUsageExample {

    /*
     * Those methods below aren't used on this
     * plugin they are just the example methods to
     * introduce you to the ClanAPI.
     *
     * You can also take a look on
     * {@link ClanAPI} class.
     * There have some others useful methods.
     */

    /**
     * Get {@link ClanMember} object.
     *
     * @param player player to search
     * @return @player clanMember object.
     */
    static ClanMember getClanMember(final Player player) {
        return getClanMember(player.getName());
    }

    /**
     * Get {@link ClanMember} object.
     *
     * @param name player to search
     * @return @player clanMember object.
     */
    static ClanMember getClanMember(final String name) {
        return ClanAPI.getClanMember(name);
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
    static Clan getClan(Player player) {
        return ClanAPI.getClan(player);
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
    static Clan getClan(String uncoloredTag) {
        return ClanAPI.getClan(uncoloredTag);
    }

    /**
     * This will increase the
     * current clan stats to
     * the given values.
     *
     * @param player player to search
     * @param coins amount of coins to
     *              add.
     * @param kills amount of kills to
     *              add.
     * @param deaths amount of deaths to
     *               add.
     */
    static void addClanStats(Player player, double coins, int kills, int deaths) {
        Clan clan = getClan(player);
        if (clan == null) return;

        clan.setCoins(clan.getCoins() + coins);
        clan.setKills(clan.getKills() + kills);
        clan.setDeaths(clan.getDeaths() + deaths);
    }

    /**
     * This will change the
     * current clan stats
     * to the given @coins,
     * @kills and @deaths.
     *
     * @param uncoloredTag tag to search
     * @param coins amount of coins to
     *              set.
     * @param kills amount of kills to
     *              set.
     * @param deaths amount of deaths to
     *               set.
     */
    static void setClanStats(String uncoloredTag, double coins, int kills, int deaths) {
        Clan clan = getClan(uncoloredTag);
        if (clan == null) return;

        clan.setCoins(coins);
        clan.setKills(kills);
        clan.setDeaths(deaths);
    }

    /**
     * Change the current
     * clan gladiator stats
     * to the given values.
     *
     * @param uncoloredTag tag to search
     * @param gladiatorWins amount of wins to
     *                      set.
     * @param gladiatorLosses amount of loss to
     *                        set.
     */
    static void setClanGladiatorStats(String uncoloredTag, int gladiatorWins, int gladiatorLosses) {
        Clan clan = getClan(uncoloredTag);
        if (clan == null) return;

        clan.setGladiatorWins(gladiatorWins);
        clan.setGladiatorLosses(gladiatorLosses);
    }

    /**
     * Change the current
     * clan miniGladiator stats
     * to the given values.
     *
     * @param uncoloredTag tag to search
     * @param miniGladiatorWins amount of wins to
     *                          set.
     * @param miniGladiatorLosses amount of loss to
     *                            set.
     */
    static void setClanMiniGladiatorStats(String uncoloredTag, int miniGladiatorWins, int miniGladiatorLosses) {
        Clan clan = getClan(uncoloredTag);
        if (clan == null) return;

        clan.setMiniGladiatorWins(miniGladiatorWins);
        clan.setMiniGladiatorLosses(miniGladiatorLosses);
    }
}