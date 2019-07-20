package com.skydhs.czclan.clan.commands;

import com.skydhs.czclan.clan.Core;
import com.skydhs.czclan.clan.FileUtils;
import com.skydhs.czclan.clan.manager.ClanManager;
import com.skydhs.czclan.clan.manager.ClanRole;
import com.skydhs.czclan.clan.manager.ClanSettings;
import com.skydhs.czclan.clan.manager.objects.Clan;
import com.skydhs.czclan.clan.manager.objects.ClanMember;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClanCmd implements CommandExecutor {
    private Core core;

    public ClanCmd(Core core) {
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Commands.only-players").get());
            return true;
        }

        Player player = (Player) sender;
        ClanMember member = ClanManager.getManager().getClanMember(player.getName());
        ClanMember target = null;
        Clan clan = null;

        if (args.length <= 0) {
            // Opens/Execute the main menu/command.
            CommandHandle.main(core, player, member);
            return true;
        }

        String argument = args[0].toUpperCase();
        boolean executed = false;

        switch (argument) {
            case "AJUDA":
            case "HELP":
                executed = true;

                for (String str : FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Commands.help").getColored()) {
                    player.sendMessage(str);
                }

                break;
            case "CRIAR":
            case "CREATE":
                executed = true;
                // Try to create this clan.
                CommandHandle.create(player, member, args);
                break;
            case "TOP":
                executed = true;
                // Execute top command.
                CommandHandle.top(core, player, args);
                break;
            case "JOGADOR":
            case "PERFIL":
            case "PLAYER":
                executed = true;

                if (args.length >= 2) {
                    String targetName = args[1];

                    if (targetName.equalsIgnoreCase(player.getName())) {
                        // Execute player command.
                        CommandHandle.player(core, player, member);
                        return true;
                    }

                    target = ClanManager.getManager().getClanMember(targetName);

                    // Execute player command.
                    CommandHandle.player(core, player, target);
                } else {
                    // Execute player command.
                    CommandHandle.player(core, player, member);
                }

                break;
            case "CLAN":
            case "STATS":
                executed = true;

                if (args.length >= 2) {
                    String targetClan = args[1];

                    if (targetClan != null && targetClan.length() >= ClanSettings.CLAN_TAG_MIN_SIZE) {
                        clan = ClanManager.getManager().getClan(targetClan);
                    }

                    // Execute clan command.
                    CommandHandle.clan(core, player, clan);
                } else {
                    if (member != null && member.hasClan()) {
                        clan = member.getClan();
                    }

                    // Execute clan command.
                    CommandHandle.clan(core, player, clan);
                }

                break;
            case "RIVALIDADES":
            case "RIVALS":
                executed = true;

                if (member != null && member.hasClan()) {
                    clan = member.getClan();

                    if (!clan.hasRivals()) {
                        player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Commands.no-rivals-found").getColored());
                    } else {
                        player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Commands.rivals-list").getColoredString(new String[] { "%sky_clan_rivals%" }, new String[] { clan.getFormattedRivals(',') }));
                    }
                } else {
                    player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.not-in-clan").getColored());
                }

                break;
            case "ALIANCAS":
            case "ALIANÇAS":
            case "ALLIES":
                executed = true;

                if (member != null && member.hasClan()) {
                    clan = member.getClan();

                    if (!clan.hasAllies()) {
                        player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Commands.no-allies-found").getColored());
                    } else {
                        player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Commands.allies-list").getColoredString(new String[] { "%sky_clan_allies%" }, new String[] { clan.getFormattedAllies(',') }));
                    }
                } else {
                    player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.not-in-clan").getColored());
                }

                break;
            case "MEMBROS":
            case "MEMBERS":
                executed = true;

                if (args.length >= 2) {
                    String targetClan = args[1];

                    if (targetClan != null && targetClan.length() >= ClanSettings.CLAN_TAG_MIN_SIZE) {
                        clan = ClanManager.getManager().getClan(targetClan);
                    }

                    // Execute members command.
                    CommandHandle.members(core, player, clan);
                } else {
                    if (member != null && member.hasClan()) {
                        clan = member.getClan();
                    }

                    // Execute members command.
                    CommandHandle.members(core, player, clan);
                }

                break;
            case "PROMOVER":
            case "PROMOTE":
                executed = true;

                if (member != null && member.hasClan()) {
                    clan = member.getClan();
                }

                // Execute promote command.
                CommandHandle.promote(player, member, clan, args);
                break;
            case "REBAIXAR":
            case "DEMOTE":
                executed = true;

                if (member != null && member.hasClan()) {
                    clan = member.getClan();
                }

                // Execute demote command.
                CommandHandle.demote(player, member, clan, args);
                break;
            case "LISTA":
            case "LIST":
                executed = true;
                break;
            case "CONVIDAR":
            case "INVITE":
                executed = true;

                if (member != null && member.hasClan()) {
                    clan = member.getClan();
                }

                // Execute invite command.
                CommandHandle.invite(player, member, clan, args);
                break;
            case "ACEITAR":
            case "ACCEPT":
                executed = true;

                // Execute accept command.
                CommandHandle.accept(player, member, args);
                break;
            case "EXPULSAR":
            case "KICK":
                executed = true;

                if (member != null && member.hasClan()) {
                    clan = member.getClan();
                }

                // Execute kick command.
                CommandHandle.kick(player, member, clan, args);
                break;
            case "DESFAZER":
            case "EXCLUIR":
            case "DISBAND":
                executed = true;

                if (member != null && member.hasClan()) {
                    clan = member.getClan();
                }

                // Execute disband command.
                CommandHandle.disband(player, member, clan);
                break;
            case "ALIADO":
            case "ALLY":
                executed = true;

                if (member != null && member.hasClan()) {
                    clan = member.getClan();
                }

                // Execute ally command.
                CommandHandle.ally(player, member, clan, args);
                break;
            case "RIVAL":
                executed = true;

                if (member != null && member.hasClan()) {
                    clan = member.getClan();
                }

                // Execute rival command.
                CommandHandle.rival(player, member, clan, args);
                break;
            case "PVP":
                executed = true;

                if (member == null || !member.hasClan()) {
                    player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.not-in-clan").getColored());
                    return true;
                }

                if (!member.getRole().isAtLeast(ClanRole.OFFICER)) {
                    player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.role-required").getColoredString(new String[] { "%role_name%" }, new String[] { ClanRole.OFFICER.getFullName() }));
                    return true;
                }

                boolean friendlyFire = !member.getClan().isFriendlyFire();
                member.getClan().setFriendlyFire(friendlyFire);

                if (friendlyFire) {
                    player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Commands.clan-pvp-enabled").getColored());
                } else {
                    player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Commands.clan-pvp-disabled").getColored());
                }

                break;
        }

        if (!executed) {
            Bukkit.dispatchCommand(sender, "clan help");
        }

        return true;
    }
}