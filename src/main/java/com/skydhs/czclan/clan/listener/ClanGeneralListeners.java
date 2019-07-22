package com.skydhs.czclan.clan.listener;

import com.skydhs.czclan.clan.manager.ClanManager;
import com.skydhs.czclan.clan.manager.objects.Clan;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ClanGeneralListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageEvent(EntityDamageByEntityEvent event) {
        if (event.getDamager() == null || event.getEntity() == null) return;
        if (!(event.getEntity() instanceof Player)) return;

        Player damager = null;
        Player damaged = (Player) event.getEntity();

        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();

            if (arrow.getShooter() instanceof Player) {
                damager = (Player) arrow.getShooter();
            }
        }

        if (damager == null || damaged == null) return;

        if (ClanManager.getManager().isSameClan(damager, damaged)) {
            Clan clan = ClanManager.getManager().getClanMember(damager.getName()).getClan();
            if (clan == null) return;

            if (!clan.isFriendlyFire()) {
                event.setCancelled(true);
            }
        }
    }
}