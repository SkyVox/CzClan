package com.skydhs.czclan.clan.manager.objects;

import com.skydhs.czclan.addon.clan.ClanAddon;
import com.skydhs.czclan.clan.FileUtils;
import com.skydhs.czclan.clan.Log;
import com.skydhs.czclan.clan.interfaces.GeneralStats;
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

public class Clan extends ClanAddon implements GeneralStats {
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

    private double coins;
    private int kills;
    private int deaths;

    private List<ClanMember> members;
    private List<ClanMember> topMembers;

    // ----- \\
    // Below: Clan Allies and Rivals
    // ----- \\
    private Set<String> clanAllies;
    private Set<String> clanRivals;

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
     * TODO, ClanMember#isOnline sometimes is false even when this player is online.
     * TODO, Improve the update method.
     */

    /**
     * Used to load clans.
     *
     * @param name
     * @param tag
     * @param description
     * @param created
     */
    public Clan(String name, String tag, String description, ZonedDateTime created, int gladiatorWins, int gladiatorLosses, int miniGladiatorWins, int miniGladiatorLosses) {
        super(gladiatorWins, gladiatorLosses, miniGladiatorWins, miniGladiatorLosses);

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
        super(0, 0, 0, 0);

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
        this.coins = 0D;
        this.kills = 0;
        this.deaths = 0;
        this.members = new ArrayList<>(ClanSettings.CLAN_MAX_MEMBERS);
        this.topMembers = new LinkedList<>();
        this.clanAllies = new HashSet<>(ClanSettings.CLAN_RELATIONS_SIZE);
        this.clanRivals = new HashSet<>(ClanSettings.CLAN_RELATIONS_SIZE);

        ClanMember leader = member;
        if (leader == null) {
            leader = new ClanMember(player.getUniqueId(), player.getName(), this, ClanRole.LEADER, ZonedDateTime.now(), 0, 0);
        } else {
            leader.setRole(ClanRole.LEADER);
            leader.changeClan(null, this, ClanRole.LEADER);
        }

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
     * @param coins
     * @param kills
     * @param deaths
     * @param members
     * @param clanAllies
     * @param clanRivals
     */
    public void load(UUID uuid, String creatorName, UUID creator, Location base, Boolean friendlyFire, double coins, int kills, int deaths, List<ClanMember> members, Set<String> clanAllies, Set<String> clanRivals) {
        this.uuid = uuid;
        this.creatorName = creatorName;
        this.creator = creator;
        this.leaderName = ClanManager.getManager().getClanLeader(members).getName();
        this.base = base;
        this.friendlyFire = friendlyFire;
        this.coins = coins;
        this.kills = kills;
        this.deaths = deaths;
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
        return tag;
    }

    public String getUncoloredTag() {
        return ChatColor.stripColor(tag);
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

    @Override
    public double getCoins() {
        return coins;
    }

    @Override
    public void setCoins(double value) {
        this.coins = value;
    }

    @Override
    public int getKills() {
        return kills;
    }

    @Override
    public void setKills(int value) {
        this.kills = value;
        this.update();
    }

    @Override
    public int getDeaths() {
        return deaths;
    }

    @Override
    public void setDeaths(int value) {
        this.deaths = value;
        this.update();
    }

    @Override
    public float getKDR() {
        if (deaths == 0) return 1;
        return ((float) kills / (float) deaths);
    }

    @Override
    public String getFormattedKDR() {
        return String.format("%.2f", getKDR());
    }

    @Override
    public void resetStats() {
        setCoins(0D);
        setKills(0);
        setDeaths(0);
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

    public void removeMember(ClanMember member) {
        this.members.remove(member);
        this.update();
        this.checkMembers(member);
    }

    public void removeMember(UUID uuid) {
        for (ClanMember members : new ArrayList<>(getMembers())) {
            if (StringUtils.equals(uuid.toString(), members.getUniqueId().toString())) {
                this.members.remove(members);
                this.update();
                this.checkMembers(members);
                break;
            }
        }
    }

    public void checkMembers(ClanMember member) {
        if (members.size() <= 0) {
            disband();
            return;
        }

        if (member.getRole().isAtLeast(ClanRole.LEADER)) {
            disband();
            return;
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

        if (role == ClanRole.LEADER) {
            this.leaderName = target.getName();
        }

        for (String str : FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Messages.promote-player-broadcast").getList(null, this, new String[] { "%target_name%", "%skyclan_member_role%" }, new String[] { target.getName(), role.getFullName() })) {
            sendMessage(str);
        }

        member.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.promote-player-sender").getString(member.getPlayer(), this, new String[] { "%target_name%", "%skyclan_member_role%" }, new String[] { target.getName(), role.getFullName() }));
        target.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.promote-player-target").getString(target.getPlayer(), this, new String[] { "%target_name%", "%skyclan_member_role%" }, new String[] { target.getName(), role.getFullName() }));
    }

    public void demoteMember(ClanMember member, ClanMember target) {
        if (target.getRole().getId() == ClanRole.MEMBER.getId()) return;

        ClanRole role = target.getRole().getPrevious();
        target.setRole(role);

        for (String str : FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Messages.demote-player-broadcast").getList(null, this, new String[] { "%target_name%", "%skyclan_member_role%" }, new String[] { target.getName(), role.getFullName() })) {
            sendMessage(str);
        }

        member.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.demote-player-sender").getString(member.getPlayer(), this, new String[] { "%target_name%", "%skyclan_member_role%" }, new String[] { target.getName(), role.getFullName() }));
        target.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.demote-player-target").getString(target.getPlayer(), this, new String[] { "%target_name%", "%skyclan_member_role%" }, new String[] { target.getName(), role.getFullName() }));
    }

    public Set<String> getClanAllies() {
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

    public Boolean isAlly(String tag) {
        if (!hasAllies() || tag == null) return false;

        String formattedTag = ClanManager.getManager().stripColor(tag);

        for (String str : clanAllies) {
            if (StringUtils.equalsIgnoreCase(formattedTag, str)) return true;
        }

        return false;
    }

    public void addAliases(Clan clan) {
        String tag = clan.getUncoloredTag();
        if (tag == null || this.clanAllies.size() >= ClanSettings.CLAN_RELATIONS_SIZE) return;

        this.clanAllies.add(tag);
        this.update();
        clan.getClanAllies().add(getUncoloredTag());
        clan.update();
    }

    public void removeAliases(Clan clan) {
        String tag = clan.getUncoloredTag();
        if (tag == null) return;

        this.clanAllies.remove(tag);
        this.update();
        clan.getClanAllies().remove(getUncoloredTag());
        clan.update();
    }

    public Set<String> getClanRivals() {
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

    public Boolean isRival(String tag) {
        if (!hasRivals() || tag == null) return false;

        String formattedTag = ClanManager.getManager().stripColor(tag);

        for (String str : clanRivals) {
            if (StringUtils.equalsIgnoreCase(formattedTag, str)) return true;
        }

        return false;
    }

    public void addRivals(Clan clan) {
        String tag = clan.getUncoloredTag();
        if (tag == null || this.clanRivals.size() >= ClanSettings.CLAN_RELATIONS_SIZE) return;

        this.clanRivals.add(tag);
        this.update();
        clan.getClanRivals().add(getUncoloredTag());
        clan.update();
    }

    public void removeRivals(Clan clan) {
        String tag = clan.getUncoloredTag();
        if (tag == null) return;

        this.clanRivals.remove(tag);
        this.update();
        clan.getClanRivals().remove(getUncoloredTag());
        clan.update();
    }

    private void updateRelations() {
        for (String str : getClanAllies()) {
            Clan clan = ClanManager.getManager().getClan(str);
            if (clan == null) continue;
            clan.removeAliases(this);
        }

        for (String str : getClanRivals()) {
            Clan clan = ClanManager.getManager().getClan(str);
            if (clan == null) continue;
            clan.removeRivals(this);
        }
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
        ClanManager.getManager().getDeletedClans().add(getColoredTag());

        for (String str : getClanAllies()) {
            Clan clan = ClanManager.getManager().getClan(str);
            if (clan == null) continue;
            clan.removeAliases(this);
        }

        for (String str : getClanRivals()) {
            Clan clan = ClanManager.getManager().getClan(str);
            if (clan == null) continue;
            clan.removeRivals(this);
        }

        String[] message = FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Messages.clan-disbanded-broadcast").getList(null, this);

        for (ClanMember members : getMembers()) {
            for (String str : message) {
                members.sendMessage(str);
            }

            members.changeClan(this, null, ClanRole.UNRANKED);
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

        return uuid && creatorName && creator && leaderName && name && tag && description && created && base && friendlyFire && members && topMembers && clanAllies && clanRivals;
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
                ", members='" + members + '\'' +
                ", topMembers='" + topMembers + '\'' +
                ", clanAllies='" + clanAllies + '\'' +
                ", clanRivals='" + clanRivals + '\'' +
                '}';
    }
}