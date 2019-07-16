package com.skydhs.czclan.addon;

import com.skydhs.czclan.addon.menu.ClanMenuAddon;
import com.skydhs.czclan.clan.ClanAddon;
import com.skydhs.czclan.clan.Core;
import com.skydhs.czclan.clan.manager.ClanLeaderboard;
import com.skydhs.czclan.clan.manager.objects.Clan;
import com.skydhs.czclan.clan.manager.objects.ClanMember;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

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

    @Override
    public void commandTop(Player player) {
        Inventory inventory = ClanMenuAddon.getTopClansMenu(player, ClanLeaderboard.LeaderboardType.KILLS);

        if (inventory == null) {
            player.sendMessage(ChatColor.RED + "Não foi possível encontrar nenhum clan nessa classificação. Por favor tente novamente em alguns minutos!");
            return;
        }

        player.openInventory(inventory);
    }

    @Override
    public void commandPlayer(Player player, ClanMember member) {
        player.openInventory(ClanMenuAddon.getPlayerStatsMenu(member));
    }

    @Override
    public void commandClan(Player player, Clan clan) {
        player.openInventory(ClanMenuAddon.getClanStatsMenu(clan));
    }

    @Override
    public void commandMembers(Player player, Clan clan) {
        player.openInventory(ClanMenuAddon.getClanMembersMenu(clan));
    }
}