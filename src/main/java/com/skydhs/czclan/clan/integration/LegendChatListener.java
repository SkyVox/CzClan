package com.skydhs.czclan.clan.integration;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import com.skydhs.czclan.clan.manager.ClanManager;
import com.skydhs.czclan.clan.manager.objects.Clan;
import com.skydhs.czclan.clan.manager.objects.ClanMember;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class LegendChatListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChatMessage(ChatMessageEvent event) {
        Player player = event.getSender();
        if (player == null) return;

        ClanMember member = ClanManager.getManager().getClanMember(player.getName());
        if (member == null) return;
        if (!member.hasClan()) return;

        Clan clan = member.getClan();

        if (event.getTags().contains("skyclan_clan_name")) {
            event.setTagValue("skyclan_clan_name", clan.getName());
        }

        if (event.getTags().contains("skyclan_clan_tag")) {
            event.setTagValue("skyclan_clan_tag", clan.getColoredTag());
        }
    }
}