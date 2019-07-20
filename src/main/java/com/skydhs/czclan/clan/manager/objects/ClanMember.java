package com.skydhs.czclan.clan.manager.objects;

import com.skydhs.czclan.clan.FileUtils;
import com.skydhs.czclan.clan.manager.ClanManager;
import com.skydhs.czclan.clan.manager.ClanRole;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*
 * This class below represents
 * the clan members.
 */
public class ClanMember implements Comparable<ClanMember> {
    private Player player;
    private UUID uuid;
    private String name;
    private String tag;
    private Clan clan;
    private ClanRole role;
    private ZonedDateTime joined;

    /*
     * Player stats.
     * This value is reseted every
     * time when he left a clan.
     */
    private GeneralStats stats;

    /*
     * @pendingInvites will store all
     * invitations to this player.
     *
     * Key = Player Name
     * Value = Long, this is the time
     *   if this value is >= than 5 minutes
     *   we can delete this invite.
     */
    private Map<String, ZonedDateTime> pendingInvites;

    public ClanMember(Player player, Clan clan, ClanRole role, ZonedDateTime joined, GeneralStats stats) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.tag = (clan == null ? null : clan.getUncoloredTag());
        this.clan = clan;
        this.role = role;
        this.joined = joined;
        this.stats = stats;
    }

    public ClanMember(UUID uuid, String name, Clan clan, ClanRole role, ZonedDateTime joined, GeneralStats stats) {
        this.uuid = uuid;
        this.name = name;
        this.tag = (clan == null ? null : clan.getUncoloredTag());
        this.clan = clan;
        this.role = role;
        this.joined = joined;
        this.stats = stats;
    }

    public ClanMember(UUID uuid, String name, String tag, ClanRole role, ZonedDateTime joined, GeneralStats stats) {
        this.uuid = uuid;
        this.name = name;
        this.tag = (tag == null ? null : ChatColor.translateAlternateColorCodes('&', tag));
        this.role = role;
        this.joined = joined;
        this.stats = stats;
    }

    /**
     * Call this method
     * whenever you want
     * to cache this @ClanMember
     * instance.
     */
    public void cache() {
        ClanManager.getManager().getLoadedMembers().put(this.name, this);
    }

    /**
     * Call this method
     * whenever you want to
     * remove this instance
     * from the cache.
     */
    public void unload() {
        ClanManager.getManager().getLoadedMembers().remove(this.name);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isOnline() {
        return player != null && player.isOnline();
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public Clan getClan() {
        if (clan == null && getTag() != null) {
            this.clan = ClanManager.getManager().getClan(this.tag);
        }

        return clan;
    }

    public Boolean hasClan() {
        return getClan() != null;
    }

    public ClanRole getRole() {
        return role;
    }

    public Boolean isLeader() {
        return role.isAtLeast(ClanRole.LEADER);
    }

    public void setRole(ClanRole role) {
        this.role = role;
    }

    public ZonedDateTime getJoinedDate() {
        return joined;
    }

    public GeneralStats getPlayerStats() {
        return stats;
    }

    public Map<String, ZonedDateTime> getPendingInvites() {
        return pendingInvites;
    }

    public Boolean hasPendingInvites() {
        return pendingInvites != null && pendingInvites.size() > 0;
    }

    public Boolean hasPendingInvites(String tag) {
        if (!hasPendingInvites()) return false;

        for (String str : getPendingInvites().keySet()) {
            if (StringUtils.equalsIgnoreCase(tag, str)) return true;
        }

        return false;
    }

    public void invitePlayer(Clan clan) {
        if (hasClan()) return;
        if (hasPendingInvites(clan.getUncoloredTag())) return;

        if (pendingInvites == null) this.pendingInvites = new HashMap<>(12);

        getPendingInvites().put(clan.getUncoloredTag(), ZonedDateTime.now());
        sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.invite-player-target").getString(player, clan));
    }

    /**
     * When player changes or
     * leave the clan, we always
     * should call this method
     * to update the ClanMember.
     *
     * @param oldClan Old clan.
     * @param clan The new Clan.
     */
    public void changeClan(Clan oldClan, Clan clan) {
        if (oldClan != null && clan != null) {
            if (oldClan.equals(clan)) return;
        }

        if (this.pendingInvites != null && clan != null) {
            this.pendingInvites.remove(clan.getUncoloredTag());
        }

        this.tag = (clan == null ? null : clan.getUncoloredTag());
        this.clan = clan;
        this.joined = ZonedDateTime.now();

        // TODO, Call the @UpdateMember event.
    }

    public void sendMessage(final String message) {
        if (!isOnline()) return;

        /* Send the message to {link@uuid} */
        player.sendMessage(message);
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
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", tag='" + tag + '\'' +
//                ", clan='" + clan + '\'' + // Removed.
                ", role='" + role + '\'' +
                ", joined='" + joined + '\'' +
                ", stats='" + stats + '\'' +
                '}';
    }

    @Override
    public int compareTo(@Nullable ClanMember value) throws NullPointerException {
        if (value == null) return 0;

        int x = this.getPlayerStats().getKills();
        int y = value.getPlayerStats().getKills();

        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }
}