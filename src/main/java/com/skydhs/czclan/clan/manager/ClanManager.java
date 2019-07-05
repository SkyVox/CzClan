package com.skydhs.czclan.clan.manager;

import com.skydhs.czclan.clan.Core;
import com.skydhs.czclan.clan.database.DBManager;
import com.skydhs.czclan.clan.manager.objects.Clan;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ClanManager {
    private static ClanManager manager;
    private static ClanLeaderboard leaderboard;

    private Map<String, Clan> loadedClans;
    private Set<String> deletedClans;

    public ClanManager(Core core) {
        this.loadedClans = DBManager.getDBManager().getDBConnection().getClans();
        this.deletedClans = new HashSet<>(64);

        ClanManager.manager = this;
        ClanManager.leaderboard = new ClanLeaderboard();

        setupTask(core);
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
        }.runTaskTimerAsynchronously(core, time, time);
    }

    /**
     * By calling this method
     * we'll loop all clans
     * and update then if
     * is needed.
     */
    public void update() {
        for (Clan clans : new HashMap<String, Clan>(getLoadedClans()).values()) {
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

        leaderboard.updateLeaderboard();
    }

    public boolean isLocationEquals(Location one, Location two) {
        if (one == null || two == null) return false;
        return one.getBlockX() == two.getBlockX() && one.getBlockY() == two.getBlockY() && one.getBlockZ() == two.getBlockZ();
    }

    /**
     * Verify if the given {@link Object}
     * is a valid clan object.
     *
     * @param obj obj to be parse.
     * @return clan object.
     */
    public Clan parseClanObject(Object obj) {
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

    public static ClanLeaderboard getClanLeaderboard() {
        return leaderboard;
    }

    public static ClanManager getManager() {
        return manager;
    }
}