package com.skydhs.czclan.addon;

import com.skydhs.czclan.addon.menu.ClanMenuAddon;
import com.skydhs.czclan.clan.ClanAddon;
import com.skydhs.czclan.clan.Core;
import org.bukkit.entity.Player;

public class Addon implements ClanAddon {
    private Core core;

    /*
     * Each plugin of @SkyClan
     * has a different @Addon class
     * which has different methods with
     * some custom systems.
     *
     * Some of functions here aren't available
     * on the configuration files.
     */

    public Addon(Core core) {
        this.core = core;
    }

    public void commandTop(Player player) {
        player.openInventory(ClanMenuAddon.getTopClansMenu(player));
    }
}