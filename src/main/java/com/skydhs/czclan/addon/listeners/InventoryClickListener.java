package com.skydhs.czclan.addon.listeners;

import com.skydhs.czclan.addon.menu.ClanMenuAddon;
import com.skydhs.czclan.clan.Core;
import com.skydhs.czclan.clan.manager.ClanLeaderboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {
    private Core core;

    public InventoryClickListener(Core core) {
        this.core = core;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        String title = event.getInventory().getTitle();
        ItemStack item = event.getCurrentItem();
        int slot = event.getSlot();

        /* Player clicked outside the inventory. */
        if (slot == -999) return;
        if (item == null || item.getType().equals(Material.AIR)) return;
        if (!hasTitle(title)) return;

        event.setCancelled(true);
        if ((event.getClick() == ClickType.SHIFT_LEFT) || (event.getClick() == ClickType.SHIFT_RIGHT)) {
            event.setResult(InventoryClickEvent.Result.DENY);
            return;
        }

        if (event.getClickedInventory() == player.getInventory()) return;
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;

        Inventory inventory = null;
        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());

        switch (name.toUpperCase()) {
            case "VOLTAR":
                Bukkit.dispatchCommand(player, "clan");
                break;
            case "FECHAR":
                player.closeInventory();
                break;
            case "CRIAR CLAN":
                core.getAddon().sendSuggestCommand(player, "/clan criar <nome> <tag>");
                player.closeInventory();
                break;
            case "CLASSIFICAÇÃO DE CLANS":
                core.getAddon().commandTop(player);
                break;
            case "CLANS ONLINE": // TODO;
                break;
            case "DESFAZER CLAN":
                Bukkit.dispatchCommand(player, "clan disband");
                player.closeInventory();
                break;
            case "SAIR DO CLAN":
                Bukkit.dispatchCommand(player, "clan leave");
                player.closeInventory();
                break;
            case "ESTATÍSTICAS":
                core.getAddon().sendSuggestCommand(player, "/clan stats <tag>");
                player.closeInventory();
                break;
            case "MEMBROS":
                core.getAddon().sendSuggestCommand(player, "/clan membros <tag>");
                player.closeInventory();
                break;
            case "ALIANÇAS":
                core.getAddon().sendSuggestCommand(player, "/clan aliancas");
                player.closeInventory();
                break;
            case "RIVALIDADES":
                core.getAddon().sendSuggestCommand(player, "/clan rivalidades");
                player.closeInventory();
                break;
            case "PVP":
                Bukkit.dispatchCommand(player, "clan pvp");
                break;
            case "ABATES":
                inventory = ClanMenuAddon.getTopClansMenu(player, ClanLeaderboard.LeaderboardType.KILLS);

                if (inventory == null) {
                    player.sendMessage(ChatColor.RED + "Não foi possível encontrar nenhum clan nessa classificação. Por favor tente novamente em alguns minutos!");
                    return;
                }

                player.openInventory(inventory);
                break;
            case "MORTES":
                inventory = ClanMenuAddon.getTopClansMenu(player, ClanLeaderboard.LeaderboardType.DEATHS);

                if (inventory == null) {
                    player.sendMessage(ChatColor.RED + "Não foi possível encontrar nenhum clan nessa classificação. Por favor tente novamente em alguns minutos!");
                    return;
                }

                player.openInventory(inventory);
                break;
            case "KDR":
                inventory = ClanMenuAddon.getTopClansMenu(player, ClanLeaderboard.LeaderboardType.KDR);

                if (inventory == null) {
                    player.sendMessage(ChatColor.RED + "Não foi possível encontrar nenhum clan nessa classificação. Por favor tente novamente em alguns minutos!");
                    return;
                }

                player.openInventory(inventory);
                break;
        }
    }

    private Boolean hasTitle(String clickedTitle) {
        for (String title : ClanMenuAddon.getTitles()) {
            if (core.getAddon().isStringEquals(title, clickedTitle)) {
                return true;
            }
        }

        return false;
    }
}