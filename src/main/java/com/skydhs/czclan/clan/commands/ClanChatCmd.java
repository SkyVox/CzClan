package com.skydhs.czclan.clan.commands;

import com.skydhs.czclan.clan.FileUtils;
import com.skydhs.czclan.clan.manager.ClanManager;
import com.skydhs.czclan.clan.manager.objects.ClanMember;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClanChatCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Commands.only-players").get());
            return true;
        }

        if (args.length <= 0) {
            sender.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Commands.clan-chat-help").getColored());
            return true;
        }

        Player player = (Player) sender;
        ClanMember member = ClanManager.getManager().getClanMember(player.getName());

        CommandHandle.clanChatMessage(player, member, args);
        return true;
    }
}