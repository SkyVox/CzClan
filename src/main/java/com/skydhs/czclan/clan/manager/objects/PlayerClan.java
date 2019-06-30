package com.skydhs.czclan.clan.manager.objects;

import com.skydhs.czclan.addon.clan.PlayerClanAddon;
import com.sun.istack.internal.Nullable;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerClan implements PlayerClanAddon {
    private Player player;

    @Nullable
    private Clan clan;
    @Nullable
    private Clan.ClanMember member;

    public Player getPlayer() {
        return player;
    }

    public void sendMessage(final String message) {
        if (player == null || !player.isOnline()) return;
        player.sendMessage(message);
    }

    public static class PlayerClanCache {
        private static Map<UUID, PlayerClan> cache;

        static {
            cache = new HashMap<>(256);
        }

        public static PlayerClan getPlayerClan(final UUID uuid) {
            return getPlayerClanList().get(uuid);
        }

        public static Map<UUID, PlayerClan> getPlayerClanList() {
            return cache;
        }
    }
}