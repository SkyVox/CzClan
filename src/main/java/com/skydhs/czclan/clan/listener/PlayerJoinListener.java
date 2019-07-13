package com.skydhs.czclan.clan.listener;

import com.skydhs.czclan.clan.Core;
import com.skydhs.czclan.clan.manager.ClanManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerJoinListener implements Listener {
    private Core core;

    public PlayerJoinListener(Core core) {
        this.core = core;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                ClanManager.getManager().loadPlayer(player);
            }
        }.runTaskLaterAsynchronously(core, 0L);
    }
}