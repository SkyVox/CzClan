package com.skydhs.czclan.clan.manager;

import com.skydhs.czclan.clan.database.DBManager;
import com.skydhs.czclan.clan.manager.objects.Clan;

import java.util.LinkedList;
import java.util.List;

public class ClanLeaderboard {
    private List<Clan> topKills, topDeaths, topKDR;

    public ClanLeaderboard() {}

    public void clearLeaderboard() {
        if (topKills != null) this.topKills.clear();
        if (topDeaths != null) this.topDeaths.clear();
        if (topKDR != null) this.topKDR.clear();
    }

    /**
     * Get clan leaderboard
     *
     * @param type leaderboard type
     *             you want to verify.
     * @return choosed leaderboard.
     */
    public List<Clan> getTopClans(LeaderboardType type) {
        switch (type) {
            case KILLS:
                return new LinkedList<>(topKills);
            case DEATHS:
                return new LinkedList<>(topDeaths);
            case KDR:
                return new LinkedList<>(topKDR);
        }

        // Then return null if the type does not exists.
        return null;
    }

    /**
     * Update all leaderboards.
     */
    public void updateLeaderboard() {
        // First clear all old leaderboard stats.
        clearLeaderboard();

        this.topKills = DBManager.getDBManager().getDBConnection().getClanLeaderboard("kills");
        this.topDeaths = DBManager.getDBManager().getDBConnection().getClanLeaderboard("deaths");
        this.topKDR = DBManager.getDBManager().getDBConnection().getClanLeaderboard("kdr");
    }

    public enum LeaderboardType {
        KILLS,
        DEATHS,
        KDR;
    }
}