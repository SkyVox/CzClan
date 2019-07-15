package com.skydhs.czclan.clan;

import com.skydhs.czclan.clan.manager.objects.ClanMember;
import org.bukkit.entity.Player;

public interface ClanAddon {

    /**
     * Called when a player executes
     * /clan top;
     *
     * @param player players that execute
     *               the command.
     */
    void commandTop(Player player);

    /**
     * Called when player executes
     * /clan player <player>;
     *
     * @param player players that executes
     *               the command.
     * @param member the target member.
     */
    void commandPlayer(Player player, ClanMember member);
}