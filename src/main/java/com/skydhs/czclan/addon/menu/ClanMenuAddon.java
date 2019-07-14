package com.skydhs.czclan.addon.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ClanMenuAddon {
    public static final String TOP_MENU = ChatColor.GRAY + "";

    public static Inventory getMainMenu() { return null; }

    public static Inventory getTopClansMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9*6, TOP_MENU);
        return inventory;
    }
}