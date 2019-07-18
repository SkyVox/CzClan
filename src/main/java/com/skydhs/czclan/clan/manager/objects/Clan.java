package com.skydhs.czclan.clan.manager.objects;

import com.skydhs.czclan.addon.clan.ClanAddon;
import com.skydhs.czclan.clan.FileUtils;
import com.skydhs.czclan.clan.Log;
import com.skydhs.czclan.clan.manager.ClanManager;
import com.skydhs.czclan.clan.manager.ClanRole;
import com.skydhs.czclan.clan.manager.ClanSettings;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Clan implements ClanAddon {
    private UUID uuid;
    private String creatorName;
    private UUID creator;
    private String leaderName;
    private String name;
    private String tag;
    private String description;
    private ZonedDateTime created;
    private Location base;
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

    /*
     * This field is required whenever
     * the clan needs to be updated.
     *
     * true - If the clan needs to be
     * updated.
     *
     * false - No changes was made,
     * so we don't need to update
     * this to db.
     */
    private Boolean update = false;

    /*
     * TODO, clan date creation and player joined date are being replaced always when player executes the action.
     */

    /**
     * Used to load clans.
     *
     * @param name
     * @param tag
     * @param description
     * @param created
     */
    public Clan(String name, String tag, String description, ZonedDateTime created) {
        this.name = name;
        this.tag = (tag == null ? null : ChatColor.translateAlternateColorCodes('&', tag));
        this.description = description;
        this.created = created;
    }

    /**
     * This constructor should be
     * called when you want
     * to create a new clan.
     *
     * @param player creator - leader
     * @param member player clanMember
     * @param name clan name
     * @param tag clan tag
     * @param description clan description
     */
    public Clan(Player player, ClanMember member, String name, String tag, String description) {
        this.uuid = ClanManager.getManager().generateId();
        this.creatorName = player.getName();
        this.creator = player.getUniqueId();
        this.leaderName = player.getName();
        this.name = name;
        this.tag = (tag == null ? null : ChatColor.translateAlternateColorCodes('&', tag));
        this.description = description;
        this.created = ZonedDateTime.now();
        this.base = null;
        this.friendlyFire = false;
        this.stats = new GeneralStats();
        this.members = new ArrayList<>(ClanSettings.CLAN_MAX_MEMBERS);
        this.topMembers = new LinkedList<>();
        this.clanAllies = new ArrayList<>(ClanSettings.CLAN_RELATIONS_SIZE);
        this.clanRivals = new ArrayList<>(ClanSettings.CLAN_RELATIONS_SIZE);

        ClanMember leader = member;
        if (leader == null) leader = new ClanMember(player.getUniqueId(), player.getName(), this, ClanRole.LEADER, ZonedDateTime.now(), new GeneralStats());

        leader.setPlayer(player);
        leader.cache();
        this.members.add(leader);

        this.save();
    }

    /**
     * Used to load an clan
     * from Database.
     *
     * @param uuid
     * @param creatorName
     * @param creator
     * @param base
     * @param friendlyFire
     * @param stats
     * @param members
     * @param clanAllies
     * @param clanRivals
     */
    public void load(UUID uuid, String creatorName, UUID creator, Location base, Boolean friendlyFire, GeneralStats stats, List<ClanMember> members, List<String> clanAllies, List<String> clanRivals) {
        this.uuid = uuid;
        this.creatorName = creatorName;
        this.creator = creator;
        this.leaderName = ClanManager.getManager().getClanLeader(members).getName();
        this.base = base;
        this.friendlyFire = friendlyFire;
        this.stats = stats;
        this.members = members;
        this.topMembers = new LinkedList<>();
        this.clanAllies = clanAllies;
        this.clanRivals = clanRivals;
    }

    public Boolean isNull() {
        return this.name == null && this.tag == null;
    }

    private void save() {
        this.update();
        ClanManager.getManager().getLoadedClans().put(getUncoloredTag(), this);
    }

    public UUID getClanUniqueId() {
        return uuid;
    }

    private void setClanUniqueId(UUID uuid) {
        this.uuid = uuid;
    }

    public String getCreatorName() {
        return creatorName;
    }

    private void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public UUID getCreator() {
        return creator;
    }

    private void setCreator(UUID creator) {
        this.creator = creator;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean hasDescription() {
        if (description == null || description.isEmpty()) return false;
        return true;
    }

    public ZonedDateTime getCreatedDate() {
        return created;
    }

    private void setCreatedDate(ZonedDateTime time) {
        this.created = time;
    }

    public Location getBase() {
        return base;
    }

    public void setBase(Location location) {
        this.base = location;
        this.update();
    }

    public void deleteBase() {
        this.base = null;
    }

    public Boolean isFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean value) {
        if (friendlyFire == value) return;

        this.friendlyFire = value;
        this.update();
    }

    public double getCoins() {
        return stats.getCoins();
    }

    public void addCoins(double value) {
        if (value <= 0) return;
        this.update();
        stats.addCoins(value);
    }

    public void removeCoins(double value) {
        if (value <= 0) return;
        this.update();
        stats.removeCoins(value);
    }

    public void setCoins(double value) {
        if (value < 0) return;
        this.update();
        stats.setCoins(value);
    }

    public int getKills() {
        return stats.getKills();
    }

    public void addKill(int value) {
        if (value <= 0) return;
        this.update();
        stats.addKill(value);
    }

    public void removeKill(int value) {
        if (value <= 0) return;
        this.update();
        stats.removeKill(value);
    }

    public void setKills(int value) {
        if (value < 0) return;
        this.update();
        stats.setKills(value);
    }

    public int getDeaths() {
        return stats.getDeaths();
    }

    public void addDeath(int value) {
        if (value <= 0) return;
        this.update();
        stats.addDeath(value);
    }

    public void removeDeath(int value) {
        if (value <= 0) return;
        this.update();
        stats.removeDeath(value);
    }

    public void setDeaths(int value) {
        if (value < 0) return;
        this.update();
        stats.setDeaths(value);
    }

    public float getKDR() {
        return stats.getKDR();
    }

    public String getFormattedKDR() {
        return stats.getFormattedKDR();
    }

    public void resetClanStats() {
        this.stats.setCoins(0D);
        this.stats.setKills(0);
        this.stats.setDeaths(0);
        this.update();
    }

    public List<ClanMember> getMembers() {
        return members;
    }

    public List<ClanMember> getTopMembers() {
        return new ArrayList<>(topMembers);
    }

    public Boolean isMember(final String name) {
        for (ClanMember members : getMembers()) {
            if (StringUtils.equalsIgnoreCase(name, members.getName())) return true;
        }

        return false;
    }

    public ClanMember getMember(final UUID uuid) {
        for (ClanMember members : getMembers()) {
            if (StringUtils.equals(uuid.toString(), members.getUniqueId().toString())) return members;
        }

        return null;
    }

    public ClanMember getMember(final String name) {
        for (ClanMember members : getMembers()) {
            if (StringUtils.equals(name, members.getName())) return members;
        }

        return null;
    }

    public boolean addMember(ClanMember member) {
        if (this.members.size() >= ClanSettings.CLAN_MAX_MEMBERS) return false;
        this.members.add(member);
        this.update();
        return true;
    }

    public boolean addMember(UUID uuid, String name, ClanRole role, ZonedDateTime joined) {
        if (this.members.size() >= ClanSettings.CLAN_MAX_MEMBERS) return false;
        ClanMember member = new ClanMember(uuid, name, this, role, joined, new GeneralStats());
        this.members.add(member);
        this.update();
        return true;
    }

    public void removeMember(ClanMember member) {
        this.members.remove(member);
        this.update();

        for (String str : FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Messages.promote-player-broadcast").getList(null, this, new String[] { "%target_name%" }, new String[] { member.getName() })) {
            sendMessage(str);
        }
    }

    public void removeMember(UUID uuid) {
        for (ClanMember members : new ArrayList<>(getMembers())) {
            if (StringUtils.equals(uuid.toString(), members.getUniqueId().toString())) {
                this.members.remove(members);
                this.update();

                for (String str : FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Messages.promote-player-broadcast").getList(null, this, new String[] { "%target_name%" }, new String[] { members.getName() })) {
                    sendMessage(str);
                }

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

        this.update();
    }

    public void promoteMember(ClanMember member, ClanMember target) {
        if (target.getRole().getId() == ClanRole.LEADER.getId()) return;

        if (target.getRole().isAtLeast(ClanRole.OFFICER)) {
            demoteMember(target, member);
        }

        ClanRole role = target.getRole().getNext();
        target.setRole(role);

        for (String str : FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Messages.promote-player-broadcast").getList(null, this, new String[] { "%target_name%", "%role_name%" }, new String[] { target.getName(), role.getFullName() })) {
            sendMessage(str);
        }

        member.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.promote-player-sender").getString(member.getPlayer(), this, new String[] { "%target_name%", "%role_name%" }, new String[] { target.getName(), role.getFullName() }));
        target.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.promote-player-target").getString(target.getPlayer(), this, new String[] { "%target_name%", "%role_name%" }, new String[] { target.getName(), role.getFullName() }));
    }

    public void demoteMember(ClanMember member, ClanMember target) {
        if (target.getRole().getId() == ClanRole.MEMBER.getId()) return;

        ClanRole role = target.getRole().getPrevious();
        target.setRole(role);

        for (String str : FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Messages.demote-player-broadcast").getList(null, this, new String[] { "%target_name%", "%role_name%" }, new String[] { target.getName(), role.getFullName() })) {
            sendMessage(str);
        }

        member.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.demote-player-sender").getString(member.getPlayer(), this, new String[] { "%target_name%", "%role_name%" }, new String[] { target.getName(), role.getFullName() }));
        target.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.demote-player-target").getString(target.getPlayer(), this, new String[] { "%target_name%", "%role_name%" }, new String[] { target.getName(), role.getFullName() }));
    }

    public List<String> getClanAllies() {
        return clanAllies;
    }

    /**
     * Format the clan allies
     * list.
     *
     * @param separator
     * @return The formatted clan allies.
     */
    public String getFormattedAllies(char separator) {
        return StringUtils.join(clanAllies, separator);
    }

    public Boolean hasAllies() {
        return clanAllies != null && clanAllies.size() > 0;
    }

    public boolean addAliases(Clan clan) {
        String tag = clan.getUncoloredTag();
        if (tag == null || this.clanAllies.size() >= ClanSettings.CLAN_RELATIONS_SIZE) return false;

        this.clanRivals.remove(tag);
        this.clanAllies.add(tag);
        this.update();
        return true;
    }

    public boolean removeAliases(Clan clan) {
        String tag = clan.getUncoloredTag();
        if (tag == null) return false;
        this.update();
        return clanAllies.remove(tag);
    }

    public List<String> getClanRivals() {
        return clanRivals;
    }

    /**
     * Format the clan rivals
     * list.
     *
     * @param separator
     * @return The formatted clan rivals.
     */
    public String getFormattedRivals(char separator) {
        return StringUtils.join(clanRivals, separator);
    }

    public Boolean hasRivals() {
        return clanRivals != null && clanRivals.size() >= 1;
    }

    public boolean addRivals(Clan clan) {
        String tag = clan.getUncoloredTag();
        if (tag == null || this.clanRivals.size() >= ClanSettings.CLAN_RELATIONS_SIZE) return false;

        this.clanAllies.remove(tag);
        this.clanRivals.add(tag);
        this.update();
        return true;
    }

    public boolean removeRivals(Clan clan) {
        String tag = clan.getUncoloredTag();
        if (tag == null) return false;
        this.update();
        return clanRivals.remove(tag);
    }

    /**
     * Send a private message for
     * all clan members.
     * @param message message to be sent.
     */
    public void sendMessage(final String message) {
        for (ClanMember members : getMembers()) {
            members.sendMessage(message);
        }
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
        List<ClanMember> members = new LinkedList<>(getMembers());

        // Clear all old member stats.
        this.topMembers.clear();
        this.topMembers = members.stream().sorted(Collections.reverseOrder()).limit(ClanSettings.CLAN_MEMBER_LEADERBOARD_LIMIT).collect(Collectors.toList());
    }

    /**
     * Always when changes
     * was made on the clan
     * this method should
     * be called.
     */
    public void update() {
        if (update) return;
        this.update = true;
    }

    public Boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean value) {
        this.update = value;
    }

    /**
     * By calling this method this
     * clan will be completely
     * deleted from our system.
     */
    public void disband() {
        ClanManager.getManager().getLoadedClans().remove(getUncoloredTag());
        ClanManager.getManager().getDeletedClans().add(getUncoloredTag());

        String[] message = FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Messages.clan-disbanded-broadcast").getList(null, this);

        for (ClanMember members : getMembers()) {
            for (String str : message) {
                members.sendMessage(str);
            }

            members.changeClan(this, null, new GeneralStats());
        }

        for (String str : FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Broadcast.clan-disbanded").getList(null, this)) {
            Log.sendPlayerMessages(str);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Clan)) return false;
        Clan clan = (Clan) obj;

        boolean uuid = StringUtils.equals(this.uuid.toString(), clan.getClanUniqueId().toString());
        boolean creatorName = StringUtils.equals(this.creatorName, clan.getCreatorName());
        boolean creator = StringUtils.equals(this.creator.toString(), clan.getCreator().toString());
        boolean leaderName = StringUtils.equals(this.leaderName, clan.getLeaderName());
        boolean name = StringUtils.equals(this.name, clan.getName());
        boolean tag = (this.hasTag() == clan.hasTag());
        boolean description = (this.hasDescription() == clan.hasDescription());
        boolean created = (this.getCreatedDate().toInstant().compareTo(clan.getCreatedDate().toInstant()) == 0);
        boolean base = ClanManager.getManager().isLocationEquals(this.base, clan.getBase());
        boolean friendlyFire = (this.friendlyFire == clan.isFriendlyFire());
        boolean stats = this.stats.equals(clan.stats);
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

        return uuid && creatorName && creator && leaderName && name && tag && description && created && base && friendlyFire && stats && members && topMembers && clanAllies && clanRivals;
    }

    @Override
    public String toString() {
        return "Clan={" +
                "uuid='" + uuid + '\'' +
                "creatorName='" + creatorName + '\'' +
                ", creator='" + creator + '\'' +
                ", creator='" + leaderName + '\'' +
                ", name='" + name + '\'' +
                ", tag='" + tag + '\'' +
                ", description='" + description + '\'' +
                ", created='" + created + '\'' +
                ", base='" + base + '\'' +
                ", friendlyFire='" + friendlyFire + '\'' +
                ", stats='" + stats + '\'' +
                ", members='" + members + '\'' +
                ", topMembers='" + topMembers + '\'' +
                ", clanAllies='" + clanAllies + '\'' +
                ", clanRivals='" + clanRivals + '\'' +
                '}';
    }
}