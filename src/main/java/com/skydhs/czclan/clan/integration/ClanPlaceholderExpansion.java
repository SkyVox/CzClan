package com.skydhs.czclan.clan.integration;

import com.skydhs.czclan.clan.Core;
import com.skydhs.czclan.clan.manager.ClanManager;
import com.skydhs.czclan.clan.manager.objects.ClanMember;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class ClanPlaceholderExpansion extends PlaceholderExpansion {
    private Core core;

    public ClanPlaceholderExpansion(Core core) {
        this.core = core;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return core.getDescription().getAuthors().get(0);
    }

    @Override
    public String getIdentifier() {
        return "skyclan";
    }

    @Override
    public String getVersion() {
        return core.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){
        if (player == null) return null;

        ClanMember member = ClanManager.getManager().getClanMember(player.getName());
        if (member == null) return null;

        switch (identifier.toUpperCase()) {
            case "CREATOR_UUID": // %skyclan_creator_uuid%
                break;
        }

        return null;
    }
}