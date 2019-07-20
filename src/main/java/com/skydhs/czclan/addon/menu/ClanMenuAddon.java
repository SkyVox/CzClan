package com.skydhs.czclan.addon.menu;

import com.skydhs.czclan.clan.manager.ClanLeaderboard;
import com.skydhs.czclan.clan.manager.ClanManager;
import com.skydhs.czclan.clan.manager.ClanSettings;
import com.skydhs.czclan.clan.manager.ItemBuilder;
import com.skydhs.czclan.clan.manager.objects.Clan;
import com.skydhs.czclan.clan.manager.objects.ClanMember;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClanMenuAddon {
    public static final String MAIN_MENU = ChatColor.GRAY + "Clan - %%main_menu%%";
    public static final String TOP_MENU = ChatColor.GRAY + "Classificação de Clans";
    public static final String PLAYER_STATS_MENU = ChatColor.GRAY + "Informações de %%player_name%%";
    public static final String CLAN_STATS_MENU = ChatColor.GRAY + "Informações do '%%clan_tag%%'";
    public static final String CLAN_MEMBERS_MENU = ChatColor.GRAY + "Lista de Membros";

    private static final int[] TOP_MENU_GLASSES_SLOTS = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 16, 17, 18, 19, 25, 26, 27, 28, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 46, 48, 50, 52, 53 };
    private static final int[] LEADERBOARD_HEAD_SLOTS = new int[] { 11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33 };
    private static final int[] CLAN_MEMBERS_GLASSES_SLOTS = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 50, 51, 52, 53 };

    private static final ItemStack GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 7).withName(" ").build();

    private static final char SQUARE_CODE = '\u25A0';

    public static Inventory getMainMenu(ClanMember member) {
        Inventory inventory = null;

        if (member == null || !member.hasClan()) {
            inventory = Bukkit.createInventory(null, 9*3, StringUtils.replace(MAIN_MENU, "%%main_menu%%", "Menu Principal"));
        } else {
            inventory = Bukkit.createInventory(null, 9*6, StringUtils.replace(MAIN_MENU, "%%main_menu%%", member.getClan().getColoredTag()));

            Clan clan = member.getClan();

            ItemBuilder playerStats = new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3);
            playerStats.setSkullOwner(member.getName());
            playerStats.withName(ChatColor.GREEN + "Suas Informações");
            playerStats.addLore(ChatColor.GRAY + "Membro do Clan '" + clan.getColoredTag() + ChatColor.GRAY + "'");
            playerStats.addLore(ChatColor.GRAY + "Cargo: " + member.getRole().getFullName());
            playerStats.addLore(ChatColor.GRAY + "Kills/Deaths: " + member.getPlayerStats().getKills() + "/" + member.getPlayerStats().getDeaths());
            playerStats.addLore(ChatColor.GRAY + "KDR: " + member.getPlayerStats().getFormattedKDR());
            playerStats.addLore(ChatColor.GRAY + "Juntou-se em: " + DateTimeFormatter.ofPattern(ClanSettings.DATE_FORMAT_PATTERN).format(member.getJoinedDate()));

            ItemStack topClans = new ItemBuilder(Material.DIAMOND_BARDING).withName(ChatColor.YELLOW + "Classificação de Clans").withLore("", ChatColor.GRAY + "Clique para ver os melhores", ChatColor.GRAY + "clans do servidor.").build();

            ItemBuilder clanStats = new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3);
            clanStats.setSkullOwner(clan.getLeaderName());
            clanStats.withName(ChatColor.AQUA + ">> " + ChatColor.YELLOW + "'" + clan.getColoredTag() + ChatColor.YELLOW + "' " + ChatColor.AQUA + "<<");
            clanStats.addLore(" ");
            clanStats.addLore(ChatColor.GRAY + "Criador do Clan: " + clan.getCreatorName());
            clanStats.addLore(ChatColor.GRAY + "Nome: " + clan.getName());
            clanStats.addLore(ChatColor.GRAY + "Kills/Deaths, KDR: " + clan.getKills() + "/" + clan.getDeaths() + ", " + clan.getKDR());
            clanStats.addLore(ChatColor.GRAY + "Aliados: " + "[" + ChatColor.GREEN + clan.getFormattedAllies(',') + ChatColor.GRAY + "]");
            clanStats.addLore(ChatColor.GRAY + "Rivais: " + "[" + ChatColor.RED + clan.getFormattedRivals(',') + ChatColor.GRAY + "]");
            clanStats.addLore(new String[] { "", ChatColor.GRAY + "Membros:" });

            for (ClanMember members : clan.getMembers()) {
                clanStats.addLore((members.isOnline() ? ChatColor.GREEN : ChatColor.RED) + "  " + SQUARE_CODE /* The representative square */ + " " + ChatColor.GRAY + members.getName());
            }

            ItemStack gladInfo = new ItemBuilder(Material.DIAMOND_CHESTPLATE).withName(ChatColor.YELLOW + "Gladiador Estatísticas:").withLore("", ChatColor.GRAY + "Seu clan possui " + ChatColor.GREEN + "%%glad_stats%%", ChatColor.GRAY + "vitórias no gladiador.").build();
            ItemStack miniGladInfo = new ItemBuilder(Material.IRON_CHESTPLATE).withName(ChatColor.YELLOW + "Mini Gladiador Estatísticas:").withLore("", ChatColor.GRAY + "Seu clan possui " + ChatColor.GREEN + "%%mini_glad_stats%%", ChatColor.GRAY + "vitórias no mini gladiador.").build();

            String[] leftClanLore = member.isLeader() ? new String[] { "", ChatColor.GRAY + "Clique aqui para desfazer o Clan." } : new String[] { "", ChatColor.GRAY + "Clique aqui para sair do Clan." };
            ItemStack leftClan = new ItemBuilder(Material.SPRUCE_DOOR).withLore(member.isLeader() ? ChatColor.RED + "Desfazer Clan" : ChatColor.RED + "Sair do Clan").withLore(leftClanLore).build();
        }

        return inventory;
    }

    public static Inventory getTopClansMenu(Player player, ClanLeaderboard.LeaderboardType type) {
        List<Clan> leaderboard = ClanManager.getClanLeaderboard().getTopClans(type);
        if (leaderboard.size() <= 0) return null;

        Inventory inventory = Bukkit.createInventory(null, 9*6, TOP_MENU);

        for (int i : TOP_MENU_GLASSES_SLOTS) {
            inventory.setItem(i, GLASS);
        }

        for (int i = 0; i < leaderboard.size(); i++) {
            Clan clan = leaderboard.get(i);

            ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM, 1, (short) 3);
            item.withName("" + ChatColor.YELLOW + (i+1) + "º posição - Clan '" + clan.getColoredTag() + ChatColor.YELLOW + "'");
            item.setSkullOwner(clan.getLeaderName());
            item.addLore(new String[] { "", ChatColor.GRAY + "Classificação entre os membros", ChatColor.GRAY + "deste Clan:", "" });

            List<ClanMember> membersLeaderboard = clan.getTopMembers();

            if (membersLeaderboard == null || membersLeaderboard.size() <= 0) {
                item.addLore(new String[] { ChatColor.GRAY + "Não foi possível encontar", ChatColor.GRAY + "nenhum jogador." });
            } else {
                for (int position = 0; position < membersLeaderboard.size(); position++) {
                    ClanMember member = membersLeaderboard.get(position);

                    item.addLore(new String[] { "" + ChatColor.GRAY + (position+1) + "º - " + member.getName() + " > " + member.getPlayerStats().getKills() + "/" + member.getPlayerStats().getDeaths() });
                }
            }

            inventory.setItem(LEADERBOARD_HEAD_SLOTS[i], item.build());
            if ((i+1) >= LEADERBOARD_HEAD_SLOTS.length) break;
        }

        ItemStack back = new ItemBuilder(Material.ARROW).withName(ChatColor.GREEN + "Voltar").withLore("", ChatColor.GRAY + "Clique para voltar para página anterior.").build();

        for (ClanLeaderboard.LeaderboardType values : ClanLeaderboard.LeaderboardType.values()) {
            ItemBuilder item = new ItemBuilder(Material.INK_SACK, 1, (values.equals(type) ? (short) 10 : (short) 8));
            item.withName(ChatColor.YELLOW + values.getName());
            item.withLore(ChatColor.GRAY + "Clique para alterar a", ChatColor.GRAY + "classificação para " + values.getName() + ".");

            inventory.setItem(getLeaderboardTypeSlot(values), item.build());
        }

        inventory.setItem(45, back);

        return inventory;
    }

    public static Inventory getPlayerStatsMenu(ClanMember member) {
        Inventory inventory = Bukkit.createInventory(null, 9*3, StringUtils.replace(PLAYER_STATS_MENU, "%%player_name%%", member.getName()));

        for (int i = 0; i < 27; i++) {
            if (i == 13) continue;
            inventory.setItem(i, GLASS);
        }

        ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM, 1, (short) 3);
        item.setSkullOwner(member.getName());

        item.withName(ChatColor.YELLOW + "Informações do " + member.getName());
        item.addLore(" ");
        item.addLore(ChatColor.GRAY + "Membro do Clan '" + member.getClan().getColoredTag() + ChatColor.GRAY + "'");
        item.addLore(ChatColor.GRAY + "Cargo: " + member.getRole().getFullName());
        item.addLore(ChatColor.GRAY + "Kills/Deaths: " + member.getPlayerStats().getKills() + "/" + member.getPlayerStats().getDeaths());
        item.addLore(ChatColor.GRAY + "KDR: " + member.getPlayerStats().getFormattedKDR());
        item.addLore(ChatColor.GRAY + "Juntou-se em: " + DateTimeFormatter.ofPattern(ClanSettings.DATE_FORMAT_PATTERN).format(member.getJoinedDate()));

        inventory.setItem(13, item.build());

        return inventory;
    }

    public static Inventory getClanStatsMenu(Clan clan) {
        Inventory inventory = Bukkit.createInventory(null, 9*3, StringUtils.replace(CLAN_STATS_MENU, "%%clan_tag%%", clan.getColoredTag()));

        for (int i = 0; i < 27; i++) {
            if (i == 13) continue;
            inventory.setItem(i, GLASS);
        }

        ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM, 1, (short) 3);
        item.setSkullOwner(clan.getLeaderName());
        item.withName(ChatColor.AQUA + ">> " + ChatColor.YELLOW + "'" + clan.getColoredTag() + ChatColor.YELLOW + "' " + ChatColor.AQUA + "<<");
        item.addLore(" ");
        item.addLore(ChatColor.GRAY + "Criador do Clan: " + clan.getCreatorName());
        item.addLore(ChatColor.GRAY + "Líder atual: " + clan.getLeaderName());
        item.addLore(ChatColor.GRAY + "Nome: " + clan.getName());
        item.addLore(ChatColor.GRAY + "Kills/Deaths, KDR: " + clan.getKills() + "/" + clan.getDeaths() + ", " + clan.getKDR());
        item.addLore(ChatColor.GRAY + "Aliados: " + "[" + ChatColor.GREEN + clan.getFormattedAllies(',') + ChatColor.GRAY + "]");
        item.addLore(ChatColor.GRAY + "Rivais: " + "[" + ChatColor.RED + clan.getFormattedRivals(',') + ChatColor.GRAY + "]");
        item.addLore(ChatColor.GRAY + "Data de Criação: " + DateTimeFormatter.ofPattern(ClanSettings.DATE_FORMAT_PATTERN).format(clan.getCreatedDate()));
        item.addLore(new String[] { "", ChatColor.GRAY + "Membros:" });

        for (ClanMember members : clan.getMembers()) {
            item.addLore((members.isOnline() ? ChatColor.GREEN : ChatColor.RED) + "  " + SQUARE_CODE /* The representative square */ + " " + ChatColor.GRAY + members.getName());
        }

        inventory.setItem(13, item.build());

        return inventory;
    }

    public static Inventory getClanMembersMenu(Clan clan) {
        Inventory inventory = Bukkit.createInventory(null, 9*6, CLAN_MEMBERS_MENU);

        for (int i : CLAN_MEMBERS_GLASSES_SLOTS) {
            inventory.setItem(i, GLASS);
        }

        for (ClanMember members : clan.getMembers()) {
            ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM, 1, (short) 3);
            item.setSkullOwner(members.getName());

            item.withName(ChatColor.YELLOW + members.getName());
            item.addLore(ChatColor.GRAY + "Cargo: " + members.getRole().getFullName());
            item.addLore(ChatColor.GRAY + "Kills/Deaths: " + members.getPlayerStats().getKills() + "/" + members.getPlayerStats().getDeaths());
            item.addLore(ChatColor.GRAY + "KDR: " + members.getPlayerStats().getFormattedKDR());

            inventory.addItem(item.build());
        }

        inventory.setItem(49, new ItemBuilder(Material.ARROW).withName(ChatColor.GREEN + "Fechar").withLore(ChatColor.GRAY + "Clique aqui para fechar.").build());

        return inventory;
    }

    private static int getLeaderboardTypeSlot(ClanLeaderboard.LeaderboardType type) {
        switch (type) {
            case KILLS:
                return 47;
            case DEATHS:
                return 49;
            case KDR:
                return 51;
        }

        return -1;
    }
}