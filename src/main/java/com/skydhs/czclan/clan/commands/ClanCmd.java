package com.skydhs.czclan.clan.commands;

import com.skydhs.czclan.clan.FileUtils;
import com.skydhs.czclan.clan.manager.objects.PlayerClan;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClanCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Commands.only-players").get());
            return true;
        }

        Player player = (Player) sender;
        PlayerClan playerClan = PlayerClan.PlayerClanCache.getPlayerClan(player);

        if (args.length <= 0) {
            // TODO opens the main menu.;

            if (playerClan.hasClan()) {

            } else {

            }

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
                break;
            case "TOP":
                executed = true;
                break;
            case "JOGADOR":
            case "PERFIL":
            case "PLAYER":
                executed = true;
                break;
            case "CLAN":
                executed = true;
                break;
            case "RIVALIDADES":
            case "RIVALS":
                executed = true;
                break;
            case "ALIANCAS":
            case "ALIANÇAS":
            case "ALLIES":
                executed = true;
                break;
            case "MEMBROS":
            case "MEMBERS":
                executed = true;
                break;
            case "PROMOVER":
            case "PROMOTE":
                executed = true;
                break;
            case "REBAIXAR":
            case "DEMOTE":
                executed = true;
                break;
            case "LISTA":
            case "LIST":
                executed = true;
                break;
            case "CONVIDAR":
            case "INVITE":
                executed = true;
                break;
            case "EXPULSAR":
            case "KICK":
                executed = true;
                break;
            case "DESFAZER":
            case "EXCLUIR":
            case "DELETE":
                executed = true;
                break;
            case "ALIADO":
            case "ALLY":
                executed = true;
                break;
            case "RIVAL":
                executed = true;
                break;
            case "PVP":
                executed = true;
                break;
        }

        if (!executed) {
            Bukkit.dispatchCommand(sender, "clan help");
        }

        return true;
    }
}