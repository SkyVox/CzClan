package com.skydhs.czclan.clan;

import com.skydhs.czclan.clan.manager.objects.Clan;
import com.skydhs.czclan.clan.manager.objects.ClanMember;
import org.bukkit.entity.Player;

public interface ClanAddon {

    /**
     * Called when players executes
     * the main clan command, '/clan'.
     *
     * @param player player that execute
     *               the command.
     * @param member the @player clanMember.
     */
    void commandMain(Player player, ClanMember member);

    /**
     * Called when a player executes
     * /clan top;
     *
     * @param player player that execute
     *               the command.
     */
    void commandTop(Player player);

    /**
     * Called when player executes
     * /clan player <player>;
     *
     * @param player player that executes
     *               the command.
     * @param member the target member.
     */
    void commandPlayer(Player player, ClanMember member);

    /**
     * Called when player executes
     * /clan stats <clan>;
     *
     * @param player player that executes
     *               the command.
     * @param clan clan to verify.
     */
    void commandClan(Player player, Clan clan);

    /**
     * Called when player executes
     * /clan member <clan>;
     *
     * @param player player that executes
     *               the command.
     * @param clan clan to verify.
     */
    void commandMembers(Player player, Clan clan);
}