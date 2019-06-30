package com.skydhs.czclan.clan.manager.objects;

import com.skydhs.czclan.addon.clan.ClanAddon;
import com.skydhs.czclan.clan.manager.ClanRole;
import org.apache.commons.lang.StringUtils;

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

    public UUID getClanUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public boolean hasTag() {
        if (tag == null || tag.isEmpty()) return false;
        return true;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasDescription() {
        if (description == null || description.isEmpty()) return false;
        return true;
    }

    public ZonedDateTime getCreatedDate() {
        return created;
    }

    public List<ClanMember> getMembers() {
        return members;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Clan)) return false;
        Clan clan = (Clan) obj;

        boolean uuid = StringUtils.equals(this.uuid.toString(), clan.getClanUniqueId().toString());
        boolean name = StringUtils.equals(this.name, clan.getName());
        boolean tag = (this.hasTag() == clan.hasTag());
        boolean description = (this.hasDescription() == clan.hasDescription());
        boolean created = (this.getCreatedDate().toInstant().compareTo(clan.getCreatedDate().toInstant()) == 0);
        boolean members = this.getMembers().equals(clan.getMembers());

        if (tag) {
            tag = StringUtils.equals(this.getTag(), clan.getTag());
        }

        if (description) {
            description = StringUtils.equals(this.getDescription(), clan.getDescription());
        }

        return uuid && name && tag && description && created && members;
    }

    @Override
    public String toString() {
        return "Clan={" +
                "uuid='" + uuid.toString() + '\'' +
                ", name='" + name + '\'' +
                ", tag='" + tag + '\'' +
                ", description='" + description + '\'' +
                ", created='" + created + '\'' +
                ", members='" + members + '\'' +
                '}';
    }

    /*
     * This class below represents
     * the clan members.
     */
    public class ClanMember {
        private UUID uuid;
        private ClanRole role;
        private ZonedDateTime joined;

        public ClanMember() {}

        public UUID getUniqueId() {
            return uuid;
        }

        public ClanRole getRole() {
            return role;
        }

        public ZonedDateTime getJoinedDate() {
            return joined;
        }

        public void sendMessage(final String message) {
            PlayerClan playerClan = PlayerClan.PlayerClanCache.getPlayerClan(uuid);
            if (playerClan == null) return;

            /* Send the message to {link@uuid} */
            playerClan.sendMessage(message);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ClanMember)) return false;
            ClanMember member = (ClanMember) obj;

            boolean uuid = StringUtils.equals(this.uuid.toString(), member.getUniqueId().toString());
            boolean role = (this.role.equals(member.getRole()));
            boolean joined = (this.getJoinedDate().toInstant().compareTo(member.getJoinedDate().toInstant()) == 0);

            return uuid && role && joined;
        }

        @Override
        public String toString() {
            return "ClanMember={" +
                    "uuid='" + uuid.toString() + '\'' +
                    ", role='" + role + '\'' +
                    ", joined='" + joined + '\'' +
                    '}';
        }
    }
}