package com.skydhs.czclan.clan;

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
}