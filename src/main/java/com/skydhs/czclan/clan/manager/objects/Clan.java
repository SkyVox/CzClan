package com.skydhs.czclan.clan.manager.objects;

import com.skydhs.czclan.addon.clan.ClanAddon;
import com.skydhs.czclan.clan.manager.ClanRole;
import org.bukkit.entity.Player;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class Clan implements ClanAddon {
    private UUID uuid;
    private String name;
    private String tag;
    private String description;
    private ZonedDateTime created;

    private List<ClanMember> members;

    public Clan() {}

    /*
     * This class below represents
     * the clan members.
     */
    public class ClanMember {
        private UUID uuid;
        private Player player;
        private ClanRole role;
        private ZonedDateTime joined;

        public ClanMember() {}

        public UUID getUniqueId() {
            return uuid;
        }

        public Player getPlayer() {
            return player;
        }

        public ClanRole getRole() {
            return role;
        }

        public ZonedDateTime getJoinedDate() {
            return joined;
        }

        public void sendMessage(final String message) {
            if (player == null || !player.isOnline()) return;
            player.sendMessage(message);
        }
    }
}