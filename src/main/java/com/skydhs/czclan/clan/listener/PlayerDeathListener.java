package com.skydhs.czclan.clan.listener;

import com.skydhs.czclan.clan.manager.ClanManager;
import com.skydhs.czclan.clan.manager.objects.ClanMember;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDeath(PlayerDeathEvent event) {
        if (event.getEntity() == null /*|| (!(event.getEntity() instanceof Player))*/) return;

        Player player = event.getEntity();
        Player killer = player.getKiller();

        ClanMember playerMember = ClanManager.getManager().getClanMember(player.getName());
        playerMember.setDeaths(playerMember.getKills() + 1);

        if (killer != null) {
            ClanMember killerMember = ClanManager.getManager().getClanMember(killer.getName());
            killerMember.setKills(killerMember.getKills() + 1);
        }
    }
}