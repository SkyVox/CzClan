package com.skydhs.czclan.clan.listener;

import com.skydhs.czclan.clan.commands.CommandHandle;
import com.skydhs.czclan.clan.manager.ClanManager;
import com.skydhs.czclan.clan.manager.objects.ClanMember;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;

public class CommandPreprocessListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        if (message == null) return;

        String[] args = event.getMessage().split(" ");
        if (args.length <= 1) return;

        String command = args[0];

        if (command.equals(".")) {
            Player player = event.getPlayer();
            ClanMember member = ClanManager.getManager().getClanMember(player.getName());

            CommandHandle.clanChatMessage(player, member, Arrays.copyOfRange(args, 1, args.length));
        }
    }
}