package com.skydhs.czclan.clan.manager.objects;

import com.skydhs.czclan.addon.clan.ClanAddon;
import com.skydhs.czclan.clan.manager.ClanManager;
import com.skydhs.czclan.clan.manager.ClanRole;
import com.skydhs.czclan.clan.manager.ClanSettings;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.time.ZonedDateTime;
import java.util.*;

public class Clan implements ClanAddon {
    private UUID uuid;
    private String creatorName;
    private UUID creator;
    private String name;
    private String tag;
    private String description;
    private ZonedDateTime created;
    private Location home;
    private Boolean friendlyFire;

    /* This is the clan stats
     * it will store coins,
     * kills, deaths and
     * many more.
     */
    private GeneralStats stats;

    private List<ClanMember> members;
    private List<ClanMember> topMembers;

    // ----- \\
    // Below: Clan Allies and Rivals
    // ----- \\
    private List<String> clanAllies;
    private List<String> clanRivals;

    /**
     * This constructor should be
     * called when you want
     * to create a new clan.
     *
     * @param creatorName
     * @param creator
     * @param name
     * @param tag
     * @param description
     */
    public Clan(String creatorName, UUID creator, String name, String tag, String description) {
        this.uuid = ClanManager.getManager().generateId();
        this.creatorName = creatorName;
        this.creator = creator;
        this.name = name;
        this.tag = tag;
        this.description = description;
        this.created = ZonedDateTime.now();
        this.home = null;
        this.friendlyFire = false;
        this.stats = new GeneralStats();
        this.members = new ArrayList<>(ClanSettings.CLAN_MAX_MEMBERS);
        this.topMembers = new LinkedList<>();
        this.clanAllies = new ArrayList<>(ClanSettings.CLAN_RELATIONS_SIZE);
        this.clanRivals = new ArrayList<>(ClanSettings.CLAN_RELATIONS_SIZE);

        /* Then, save this clan. */
        save();
    }

    private void save() {
        ClanManager.getManager().getLoadedClans().put(getUncoloredTag(), this);
    }

    public UUID getClanUniqueId() {
        return uuid;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public UUID getCreator() {
        return creator;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public String getColoredTag() {
        if (!hasTag()) return null;
        return ChatColor.translateAlternateColorCodes('&', tag);
    }

    public String getUncoloredTag() {
        if (!hasTag()) return null;
        return ChatColor.stripColor(getColoredTag());
    }

    public Boolean hasTag() {
        if (tag == null || tag.isEmpty()) return false;
        return true;
    }

    public String getDescription() {
        return description;
    }

    public Boolean hasDescription() {
        if (description == null || description.isEmpty()) return false;
        return true;
    }

    public ZonedDateTime getCreatedDate() {
        return created;
    }

    public Location getHome() {
        return home;
    }

    public void setHome(Location value) {
        this.home = value;
    }

    public void deleteHome() {
        this.home = null;
    }

    public Boolean isFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean value) {
        if (friendlyFire == value) return;
        this.friendlyFire = value;
    }

    public GeneralStats getClanStats() {
        return stats;
    }

    public void resetClanStats() {
        this.stats.setCoins(0D);
        this.stats.setKills(0);
        this.stats.setDeaths(0);
    }

    public List<ClanMember> getMembers() {
        return members;
    }

    public List<ClanMember> getTopMembers() {
        return new ArrayList<>(topMembers);
    }

    public ClanMember getMember(UUID uuid) {
        for (ClanMember members : getMembers()) {
            if (StringUtils.equals(uuid.toString(), members.getUniqueId().toString())) return members;
        }

        return null;
    }

    public ClanMember getMember(String name) {
        for (ClanMember members : getMembers()) {
            if (StringUtils.equals(name, members.getName())) return members;
        }

        return null;
    }

    public boolean addMember(ClanMember member) {
        if (this.members.size() >= ClanSettings.CLAN_MAX_MEMBERS) return false;
        this.members.add(member);
        return true;
    }

    public boolean addMember(UUID uuid, String name, ClanRole role, ZonedDateTime joined) {
        if (this.members.size() >= ClanSettings.CLAN_MAX_MEMBERS) return false;
        ClanMember member = new ClanMember(uuid, name, role, joined);
        this.members.add(member);
        return true;
    }

    public void removeMember(ClanMember member) {
        this.members.remove(member);
    }

    public void removeMember(UUID uuid) {
        for (ClanMember members : new ArrayList<>(getMembers())) {
            if (StringUtils.equals(uuid.toString(), members.getUniqueId().toString())) {
                this.members.remove(members);
                break;
            }
        }
    }

    public void clearMembers() {
        /* All members will be cleared.
         *
         * But the clan creator cannot
         * be removed from it.
         */
        for (ClanMember members : new ArrayList<>(getMembers())) {
            if (!StringUtils.equals(creator.toString(), members.getUniqueId().toString())) {
                this.members.remove(members);
            }
        }
    }

    public List<String> getClanAllies() {
        return clanAllies;
    }

    public boolean addAliases(Clan clan) {
        String tag = clan.getUncoloredTag();
        if (tag == null || this.clanAllies.size() >= ClanSettings.CLAN_RELATIONS_SIZE) return false;

        this.clanRivals.remove(tag);
        this.clanAllies.add(tag);
        return true;
    }

    public boolean removeAliases(Clan clan) {
        String tag = clan.getUncoloredTag();
        if (tag == null) return false;
        return clanAllies.remove(tag);
    }

    public List<String> getClanRivals() {
        return clanRivals;
    }

    public boolean addRivals(Clan clan) {
        String tag = clan.getUncoloredTag();
        if (tag == null || this.clanRivals.size() >= ClanSettings.CLAN_RELATIONS_SIZE) return false;

        this.clanAllies.remove(tag);
        this.clanRivals.add(tag);
        return true;
    }

    public boolean removeRivals(Clan clan) {
        String tag = clan.getUncoloredTag();
        if (tag == null) return false;
        return clanRivals.remove(tag);
    }

    /**
     * Send a private message for
     * all clan members.
     * @param message message to be sent.
     */
    public void sendMessage(final String message) {
        String[] str = new String[1];
        str[0] = message;
        sendMessage(str);
    }

    /**
     * Send a private message for
     * all clan members.
     * @param message message to be sent.
     */
    public void sendMessage(final String[] message) {
        for (ClanMember members : getMembers()) {
            for (String str : message) {
                members.sendMessage(str);
            }
        }
    }

    /**
     * This method will update
     * {@link ClanMember}
     */
    public void updateTopMembers() {
        List<ClanMember> members = new ArrayList<>(ClanSettings.CLAN_MEMBER_LEADERBOARD_LIMIT);

        for (ClanMember member : getMembers()) {
            members.add(member);
            if (members.size() >= ClanSettings.CLAN_MEMBER_LEADERBOARD_LIMIT) break;
        }

        // Sort this list above by member kills.
        Collections.sort(members);

        getTopMembers().clear();

        for (ClanMember member : members) {
            this.topMembers.add(member);
        }
    }

    /**
     * By calling this method this
     * clan will be completely
     * deleted from our system.
     */
    public void delete() { // TODO.
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Clan)) return false;
        Clan clan = (Clan) obj;

        boolean uuid = StringUtils.equals(this.uuid.toString(), clan.getClanUniqueId().toString());
        boolean creator = StringUtils.equals(this.creator.toString(), clan.getCreator().toString());
        boolean name = StringUtils.equals(this.name, clan.getName());
        boolean tag = (this.hasTag() == clan.hasTag());
        boolean description = (this.hasDescription() == clan.hasDescription());
        boolean created = (this.getCreatedDate().toInstant().compareTo(clan.getCreatedDate().toInstant()) == 0);
        boolean home = ClanManager.getManager().isLocationEquals(this.home, clan.getHome());
        boolean friendlyFire = (this.friendlyFire == clan.isFriendlyFire());
        boolean stats = this.stats.equals(clan.getClanStats());
        boolean members = this.getMembers().equals(clan.getMembers());
        boolean topMembers = this.getTopMembers().equals(clan.getTopMembers());
        boolean clanAllies = this.clanAllies.equals(clan.getClanAllies());
        boolean clanRivals = this.clanRivals.equals(clan.getClanRivals());

        if (tag) {
            tag = StringUtils.equals(this.getTag(), clan.getTag());
        }

        if (description) {
            description = StringUtils.equals(this.getDescription(), clan.getDescription());
        }

        return uuid && creator && name && tag && description && created && home && friendlyFire && stats && members && topMembers && clanAllies && clanRivals;
    }

    @Override
    public String toString() {
        return "Clan={" +
                "uuid='" + uuid.toString() + '\'' +
                ", creator='" + creator + '\'' +
                ", name='" + name + '\'' +
                ", tag='" + tag + '\'' +
                ", description='" + description + '\'' +
                ", created='" + created + '\'' +
                ", home='" + home + '\'' +
                ", friendlyFire='" + friendlyFire + '\'' +
                ", stats='" + stats + '\'' +
                ", members='" + members + '\'' +
                ", topMembers='" + topMembers + '\'' +
                ", clanAllies='" + clanAllies + '\'' +
                ", clanRivals='" + clanRivals + '\'' +
                '}';
    }

    /*
     * This class below represents
     * the clan members.
     */
    public class ClanMember implements Comparable<ClanMember>{
        private UUID uuid;
        private String name;
        private ClanRole role;
        private ZonedDateTime joined;

        /*
         * Player stats.
         * This value is reseted every
         * time when he left a clan.
         */
        private GeneralStats stats;

        public ClanMember(UUID uuid, String name, ClanRole role, ZonedDateTime joined) {
            this.uuid = uuid;
            this.name = name;
            this.role = role;
            this.joined = joined;
            this.stats = new GeneralStats();
        }

        public UUID getUniqueId() {
            return uuid;
        }

        public String getName() {
            return name;
        }

        public ClanRole getRole() {
            return role;
        }

        public ZonedDateTime getJoinedDate() {
            return joined;
        }

        public GeneralStats getPlayerStats() {
            return stats;
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
            ClanMember clanMember = (ClanMember) obj;

            boolean uuid = StringUtils.equals(this.uuid.toString(), clanMember.getUniqueId().toString());
            boolean name = StringUtils.equals(this.name, clanMember.getName());
            boolean role = (this.role.equals(clanMember.getRole()));
            boolean joined = (this.getJoinedDate().toInstant().compareTo(clanMember.getJoinedDate().toInstant()) == 0);
            boolean stats = this.stats.equals(clanMember.getPlayerStats());

            return uuid && name && role && joined && stats;
        }

        @Override
        public String toString() {
            return "ClanMember={" +
                    "uuid='" + uuid.toString() + '\'' +
                    ", name='" + name + '\'' +
                    ", role='" + role + '\'' +
                    ", joined='" + joined + '\'' +
                    '}';
        }

        @Override
        public int compareTo(ClanMember value) {
            if (value == null) return 0;
            return this.getPlayerStats().getKills() - value.getPlayerStats().getKills();
        }
    }
}