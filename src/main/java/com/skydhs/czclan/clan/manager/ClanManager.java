package com.skydhs.czclan.clan.manager;

import com.skydhs.czclan.clan.Core;
import com.skydhs.czclan.clan.FileUtils;
import com.skydhs.czclan.clan.Log;
import com.skydhs.czclan.clan.database.DBManager;
import com.skydhs.czclan.clan.manager.objects.Clan;
import com.skydhs.czclan.clan.manager.objects.ClanMember;
import com.skydhs.czclan.clan.manager.objects.GeneralStats;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Pattern;

public class ClanManager {
    private final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf('&') + "[0-9A-FK-OR]");

    private static ClanManager manager;
    private static ClanLeaderboard leaderboard;

    private Map<String, Clan> loadedClans;
    private Set<String> deletedClans;

    private Map<String, ClanMember> loadedMembers;

    public ClanManager(Core core) {
        ClanManager.manager = this;
        ClanManager.leaderboard = new ClanLeaderboard();

        this.loadedClans = DBManager.getDBManager().getDBConnection().getClans();
        this.deletedClans = new HashSet<>(64);
        this.loadedMembers = new HashMap<>(512);

        setupTask(core);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player players : Bukkit.getOnlinePlayers()) {
                    ClanManager.getManager().loadClanMember(players);
                }
            }
        }.runTaskAsynchronously(core);
    }

    /**
     * Setup the async task
     * timer.
     *
     * @param core
     */
    private void setupTask(Core core) {
        final long time = 20*60*ClanSettings.CLAN_DELAYED_UPDATE_TASK_MIN;

        new BukkitRunnable() {
            @Override
            public void run() {
                update();
            }
        }.runTaskTimerAsynchronously(core, 20, time);
    }

    /**
     * By calling this method
     * we'll loop all clans
     * and update then if
     * is needed.
     */
    public void update() {
        for (Clan clans : new HashMap<>(getLoadedClans()).values()) {
            if (clans == null || deletedClans.contains(clans.getUncoloredTag())) continue;
            clans.updateTopMembers();

            if (clans.isUpdate()) {
                clans.setUpdate(false);
                DBManager.getDBManager().getDBConnection().updateClan(clans);
            }
        }

        for (String str : new HashSet<>(deletedClans)) {
            DBManager.getDBManager().getDBConnection().delete(str, DBManager.CLAN_TABLE, "uncolored_tag");
            deletedClans.remove(str);
        }

        // Update clan leaderboard.
        leaderboard.updateLeaderboard();

        for (String name : new HashMap<>(getLoadedMembers()).keySet()) {
            ClanMember member = getLoadedMembers().get(name);

            if (member == null) {
                // Delete this player from our database.
                DBManager.getDBManager().getDBConnection().delete(name, DBManager.CLAN_MEMBERS, "name");
                // Then remove him from our cache.
                getLoadedMembers().remove(name);
                continue;
            }

            final boolean pendingInvites = member.hasPendingInvites();

            if (member.hasClan()) {
                if (!member.isOnline()) {
                    member.unload();
                }
            } else {
                if (!pendingInvites) {
                    /*
                     * If this player doesn't has
                     * clan, we don't need him
                     * on our cache.
                     */
                    getLoadedMembers().remove(name);
                }
            }

            /*
             * Update this player,
             * send all them stats to
             * database.
             */
            DBManager.getDBManager().getDBConnection().updateMember(member);

            /*
             * Verify if this player has some
             * pending clan invitations.
             */
            if (pendingInvites) {
                ZonedDateTime now = ZonedDateTime.now();
                Map<String, ZonedDateTime> invitations = new HashMap<>(member.getPendingInvites());
                int expired = 0;

                for (String tag : invitations.keySet()) {
                    ZonedDateTime inviteTime = invitations.get(tag);
                    inviteTime.plusMinutes(ClanSettings.CLAN_PLAYER_INVITE_EXPIRATION_TIME);

                    Duration duration = Duration.between(inviteTime, now);
                    if (duration.toMinutes() >= 1) {
                        // This invite has expired.
                        member.getPendingInvites().remove(tag);
                        expired+=1;
                    }
                }

                if (expired >= invitations.size()) {
                    // Then remove him.
                    if (!member.hasClan()) {
                        getLoadedMembers().remove(name);
                    }
                }
            }
        }
    }

    public Clan create(Player player, ClanMember member, String name, final String tag, final String description) {
        Clan clan = new Clan(player, member, name, tag, description);
        // TODO, call the create clan event.

        // Send the created messages.
        player.sendMessage(FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Messages.clan-created").getList(player, clan));
        // Then, broadcast it.
        for (String str : FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Broadcast.new-clan-created").getList(player, clan)) {
            Log.sendPlayerMessages(str);
        }

        return clan;
    }

    /**
     * Get the clan leader.
     *
     * @param members the members list to verify.
     * @return
     */
    public ClanMember getClanLeader(List<ClanMember> members) {
        if (members == null || members.size() <= 0) return null;

        for (ClanMember member : members) {
            if (member.getRole().isAtLeast(ClanRole.LEADER)) return member;
        }

        return members.get(0);
    }

    public boolean isLocationEquals(Location one, Location two) {
        if (one == null || two == null) return false;
        return one.getBlockX() == two.getBlockX() && one.getBlockY() == two.getBlockY() && one.getBlockZ() == two.getBlockZ();
    }

    /**
     * Removes color code from
     * a specific string.
     *
     * @param input
     * @return
     */
    public String stripColor(String input) {
        return input == null ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public String serializeLocation(Location location) throws NullPointerException {
        if (location == null) return null;

        StringBuilder str = new StringBuilder(4);
        str.append(location.getWorld().getName()); // Add our world name.
        str.append("#" + location.getX()); // Add the 'x' point.
        str.append("#" + location.getY()); // Add the 'y' point.
        str.append("#" + location.getZ()); // Add the 'z' point.

        return str.toString();
    }

    public Location deserializeLocation(String location) throws NullPointerException {
        if (location == null || StringUtils.containsIgnoreCase(location, "null")) return null;

        String[] locationSplit = location.split("#");
        double x = Double.parseDouble(locationSplit[1]);
        double y = Double.parseDouble(locationSplit[2]);
        double z = Double.parseDouble(locationSplit[3]);

        return new Location(Bukkit.getWorld(String.valueOf(locationSplit[0])), x, y, z);
    }

    public StringBuilder getClanMembers(List<ClanMember> members) {
        StringBuilder ret = new StringBuilder(members.size());

        for (int i = 0; i < members.size(); i++) {
            ClanMember member = members.get(i);
            ret.append(member.getUniqueId().toString());

            if ((i+1) < members.size()) ret.append(", ");
        }

        return ret;
    }

    public List<ClanMember> getClanMembers(String members) {
        String[] member = members.split(", ");
        List<ClanMember> ret = new ArrayList<>(ClanSettings.CLAN_MAX_MEMBERS);

        for (int i = 0; i < member.length; i++) {
            String str = member[i];
            if (str == null || str.isEmpty()) continue;
            ret.add(DBManager.getDBManager().getDBConnection().getClanMember(str, AccessType.UUID));
        }

        return ret;
    }

    public StringBuilder getClanRelations(List<String> value) {
        StringBuilder ret = new StringBuilder(value.size());

        for (int i = 0; i < value.size(); i++) {
            String str = value.get(i);
            ret.append(str);

            if ((i+1) < value.size()) ret.append(", ");
        }

        return ret;
    }

    public List<String> getClanRelations(String value) {
        List<String> ret = new ArrayList<>(ClanSettings.CLAN_RELATIONS_SIZE);
        String[] split = value.split(", ");

        for (int i = 0; i < split.length; i++) {
            String str = split[i];
            if (str == null || str.isEmpty()) continue;
            ret.add(str);
        }

        return ret;
    }

    public ClanMember loadClanMember(Player player) {
        for (Clan clans : getLoadedClans().values()) {
            for (ClanMember member : clans.getMembers()) {
                if (StringUtils.equals(player.getUniqueId().toString(), member.getUniqueId().toString())) {
                    member.setPlayer(player);
                    member.cache();
                    return member;
                }
            }
        }

        ClanMember ret = DBManager.getDBManager().getDBConnection().getClanMember(player.getUniqueId());
        if (ret == null) ret = new ClanMember(player, null, ClanRole.UNRANKED, ZonedDateTime.now(), new GeneralStats());

        ret.setPlayer(player);
        ret.cache();
        return ret;
    }

    public void unloadPlayer(Player player) {
        ClanMember member = getLoadedMembers().get(player.getName());
        if (member == null) return;

        member.setPlayer(null);
        member.unload();
    }

    public ClanMember getClanMember(final String name) {
        if (getLoadedMembers().containsKey(name)) return getLoadedMembers().get(name);

        for (Clan clans : getLoadedClans().values()) {
            for (ClanMember member : clans.getMembers()) {
                if (StringUtils.equals(name, member.getName())) {
                    member.cache();
                    return member;
                }
            }
        }

        ClanMember ret = DBManager.getDBManager().getDBConnection().getClanMember(name, AccessType.NAME);
        if (ret == null) return null;

        ret.cache();
        return ret;
    }

    public boolean isNameInUse(final String name) {
        if (name == null) return false;

        String search = ChatColor.stripColor(name);

        for (Clan clans : getLoadedClans().values()) {
            if (StringUtils.equalsIgnoreCase(search, ChatColor.stripColor(clans.getUncoloredTag()))) return true;
        }

        return false;
    }

    public boolean isTagInUse(final String tag) {
        if (tag == null) return false;

        String search = ChatColor.stripColor(tag);
        return getLoadedClans().get(search) != null;
    }

    public boolean canUse(final String name, final String tag) {
        return isNameInUse(name) && isTagInUse(tag);
    }

    /**
     * Get a specific clan.
     *
     * @param tag Tag do search.
     * @return
     */
    public Clan getClan(final String tag) {
        if (tag == null) return null;

        String searchTag = ChatColor.stripColor(tag);
        return getLoadedClans().get(searchTag);
    }

    /**
     * Verify if the given
     * tag is from a valid
     * clan.
     *
     * @param tag Clan tag
     * @return if this tag is a valid clan.
     */
    public boolean isClan(String tag) {
        return getLoadedClans().get(tag) != null;
    }

    /**
     * Verify if the given {@link Object}
     * is a valid clan object.
     *
     * @param obj obj to be parse.
     * @return clan object.
     */
    public Clan parseClan(Object obj) {
        if (!(obj instanceof Clan)) return null;
        return (Clan) obj;
    }

    /**
     * This method is used to generate
     * a random and custom id, to
     * make your clan unique
     * we create a new id for every
     * new clan around the server.
     *
     * @return Custom clan Id.
     */
    public UUID generateId() {
        return UUID.randomUUID();
    }

    /**
     * Get all loaded clans.
     *
     * Key = Uncolored tag.
     * Value = {@link Clan}.
     *
     * @return
     */
    public Map<String, Clan> getLoadedClans() {
        return loadedClans;
    }

    /**
     * Clans on this list
     * will be deleted on
     * the next wave.
     *
     * @return
     */
    public Set<String> getDeletedClans() {
        return deletedClans;
    }

    /**
     * Get all cached members.
     * Once someone search for
     * a specific player, this
     * player will be cached on
     * this map.
     * Every 'x' minutes it'll be
     * cleared.
     *
     * Key = Player name.
     * Value = {@link ClanMember}
     *
     * @return
     */
    public Map<String, ClanMember> getLoadedMembers() {
        return loadedMembers;
    }

    public static ClanLeaderboard getClanLeaderboard() {
        return leaderboard;
    }

    public static ClanManager getManager() {
        return manager;
    }
}