package com.skydhs.czclan.addon;

import com.skydhs.czclan.addon.listeners.InventoryClickListener;
import com.skydhs.czclan.addon.menu.ClanMenuAddon;
import com.skydhs.czclan.clan.ClanAddon;
import com.skydhs.czclan.clan.Core;
import com.skydhs.czclan.clan.fanciful.FancyMessage;
import com.skydhs.czclan.clan.manager.ClanLeaderboard;
import com.skydhs.czclan.clan.manager.objects.Clan;
import com.skydhs.czclan.clan.manager.objects.ClanMember;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.regex.Pattern;

public class Addon implements ClanAddon {
    private Core core;

    private static final Pattern COMPARE_WITH_PLACEHOLDERS = Pattern.compile("\\%[^]]*\\%");

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

        core.getServer().getPluginManager().registerEvents(new InventoryClickListener(core), core);
    }

    public void switchOff() {
        for (Player players : Bukkit.getOnlinePlayers()) {
            if (players.getOpenInventory() != null) {
                for (String title : ClanMenuAddon.getTitles()) {
                    if (isStringEquals(title, players.getOpenInventory().getTitle())) {
                        players.closeInventory();
                    }
                }
            }
        }
    }

    /**
     * This method will compare
     * two strings.
     * If this String has some
     * placeholders, @COMPARE_WITH_PLACEHOLDERS
     * will remove and then compare it.
     *
     * @param one This is the first String
     *            this String should be
     *            the default String.
     *            example: String str = Hello %player_name%.
     * @param two This is the second String
     *            this should be the
     *            replaced string.
     *            example: String str = Hello SkyVox.
     * @return If the Strings are equals.
     */
    public Boolean isStringEquals(String one, String two) {
        if (one == null && two == null) return true;
        if (one == null || two == null) return false;
        if (StringUtils.equalsIgnoreCase(one, two)) return true;

        String regex = COMPARE_WITH_PLACEHOLDERS.matcher(ChatColor.translateAlternateColorCodes('&', one)).replaceAll("(.*)");
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(two).matches();
    }

    public void sendSuggestCommand(Player player, String suggest) {
        player.sendMessage("");
        FancyMessage text = new FancyMessage("Clique para executar ").color(ChatColor.GRAY).style(ChatColor.BOLD);
        text.then(suggest + " ").color(ChatColor.GRAY).style(ChatColor.BOLD);
        text.then("[Clique Aqui] ").color(ChatColor.YELLOW).style(ChatColor.BOLD).suggest(suggest).formattedTooltip(new FancyMessage("Clique para ver o commando sugestivo, ").color(ChatColor.GRAY), new FancyMessage(suggest).color(ChatColor.GRAY));
        text.send(player);
        player.sendMessage("");
    }

    @Override
    public void commandMain(Player player, ClanMember member) {
        player.openInventory(ClanMenuAddon.getMainMenu(member));
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