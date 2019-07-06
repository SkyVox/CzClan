package com.skydhs.czclan.clan.manager.objects;

import com.skydhs.czclan.addon.clan.PlayerClanAddon;
import com.skydhs.czclan.clan.database.DBManager;
import com.skydhs.czclan.clan.manager.ClanManager;
import com.sun.istack.internal.Nullable;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class PlayerClan implements PlayerClanAddon {
    private Player player;
    private String name;
    private ZonedDateTime lastSeen;

    // ----- \\
    // Clan Information
    // ----- \\
    private String tag;

    private Clan clan;
    private Clan.ClanMember member;

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

    public PlayerClan(String name, ZonedDateTime lastSeen, String tag) {
        this.name = name;
        this.lastSeen = lastSeen;
        this.tag = tag;

        if (tag != null && !tag.isEmpty()) {
            this.clan = ClanManager.getManager().getLoadedClans().get(tag);
            this.member = (clan == null ? null : clan.getMember(name));
        }
    }

    public PlayerClan(Player player, String name, ZonedDateTime lastSeen, String tag) {
        this.player = player;
        this.name = name;
        this.lastSeen = lastSeen;
        this.tag = tag;

        if (tag != null && !tag.isEmpty()) {
            this.clan = ClanManager.getManager().getLoadedClans().get(tag);
            this.member = (clan == null ? null : clan.getMember(name));
        }
    }

    public Player getPlayer() {
        return player;
    }

    public String getPlayerName() {
        return name;
    }

    public Boolean isPlayerOnline() {
        if (player == null) return false;
        return player.isOnline();
    }

    public ZonedDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(ZonedDateTime time) {
        this.lastSeen = time;
    }

    public String getUncoloredTag() {
        return tag;
    }

    @Nullable
    public Clan getClan() {
        return clan;
    }

    public void setPlayerClan(Clan clan) {
        if (clan == null) {
            this.tag = null;
            this.clan = null;
            this.member = null;
            return;
        }

        this.tag = clan.getUncoloredTag();
        this.clan = clan;
        this.member = clan.getMember(name);
    }

    @Nullable
    public Clan.ClanMember getClanMember() {
        return member;
    }

    /**
     * Check if @player is
     * member of a clan
     *
     * @return if player has clan or not.
     */
    public boolean hasClan() {
        return clan != null;
    }

    public Map<String, ZonedDateTime> getPendingInvites() {
        return pendingInvites;
    }

    public Boolean hasPendingInvites() {
        return pendingInvites != null && pendingInvites.size() > 0;
    }

    public void invitePlayer(Clan clan) {
        if (hasClan()) return;

        getPendingInvites().put(clan.getUncoloredTag(), ZonedDateTime.now());
        sendMessage("You were invited to: '" + clan.getColoredTag() + "' You have 5 minutes to accept. // DELETE THIS."); // TODO, get this message from Configuration file.
    }

    /**
     * Send an private message
     *
     * @param message message to be sent.
     */
    public void sendMessage(final String message) {
        if (player == null || !player.isOnline()) return;
        player.sendMessage(message);
    }

    public void delete() {
        // Delete this player from our database.
        DBManager.getDBManager().getDBConnection().delete(getPlayerName(), DBManager.PLAYERCLAN_TABLE, "player_name");
        // Then remove him from our cache.
        PlayerClan.PlayerClanCache.getPlayerClanList().remove(getPlayerName());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PlayerClan)) return false;
        PlayerClan playerClan = (PlayerClan) obj;

        boolean player = playerClan.player.equals(playerClan.getPlayer());
        boolean name = StringUtils.equals(playerClan.name, playerClan.getPlayerName());
        boolean lastSeen = (this.getLastSeen().toInstant().compareTo(playerClan.getLastSeen().toInstant()) == 0);
        boolean tag = StringUtils.equals(this.tag, playerClan.getUncoloredTag());
        boolean clan = this.clan.equals(playerClan.getClan());
        boolean member = this.member.equals(playerClan.getClanMember());

        return player && name && lastSeen && tag && clan && member;
    }

    @Override
    public String toString() {
        return "PlayerClan={" +
                "player='" + player + '\'' +
                ", name='" + name + '\'' +
                ", lastSeen='" + lastSeen + '\'' +
                ", tag='" + tag + '\'' +
                ", clan='" + clan + '\'' +
                ", member='" + member + '\'' +
                '}';
    }

    /**
     * Here all PlayerClan instances
     * will be stored.
     */
    public static class PlayerClanCache {
        private static Map<String, PlayerClan> cache;

        static {
            cache = new HashMap<>(256);
        }

        public static void loadPlayer(final Player player) {
            String name = player.getName();
            if (getPlayerClanList().containsKey(name)) return;

            PlayerClan playerClan = DBManager.getDBManager().getDBConnection().getPlayerClan(player);
            if (playerClan == null) playerClan = new PlayerClan(player, player.getName(), ZonedDateTime.now(), null);

            getPlayerClanList().put(name, playerClan);
        }

        public static PlayerClan getPlayerClan(final Player player) {
            String name = player.getName();
            if (getPlayerClanList().containsKey(name)) return getPlayerClanList().get(name);

            PlayerClan playerClan = DBManager.getDBManager().getDBConnection().getPlayerClan(player);
            if (playerClan == null) playerClan = new PlayerClan(player, player.getName(), ZonedDateTime.now(), null);

            getPlayerClanList().put(name, playerClan);
            return playerClan;
        }

        public static PlayerClan getPlayerClan(final String name) {
            if (getPlayerClanList().containsKey(name)) return getPlayerClanList().get(name);

            PlayerClan playerClan = DBManager.getDBManager().getDBConnection().getPlayerClan(name);
            if (playerClan == null) playerClan = new PlayerClan(name, ZonedDateTime.now(), null);

            getPlayerClanList().put(name, playerClan);
            return playerClan;
        }

        /**
         * Get playerClan
         *
         * Key = Player Name
         * Value = @PlayerClan
         *
         * @return
         */
        public static Map<String, PlayerClan> getPlayerClanList() {
            return cache;
        }
    }
}