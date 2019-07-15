package com.skydhs.czclan.clan.manager;

import com.skydhs.czclan.clan.FileUtils;
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
        KILLS('0', FileUtils.get().getString(FileUtils.Files.CONFIG, "Leaderboard.kills.name").get()),
        DEATHS('1', FileUtils.get().getString(FileUtils.Files.CONFIG, "Leaderboard.deaths.name").get()),
        KDR('2', FileUtils.get().getString(FileUtils.Files.CONFIG, "Leaderboard.kdr.name").get());

        private char id;
        private String translated;

        LeaderboardType(char id, String translated) {
            this.id = id;
            this.translated = translated;
        }

        public char getId() {
            return id;
        }

        public String getName() {
            return translated;
        }
    }
}