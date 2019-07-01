package com.skydhs.czclan.clan.manager.objects;

public class ClanStats {
    private double coins;
    private int kills;
    private int deaths;

    public ClanStats() {
        this.coins = 0D;
        this.kills = 0;
        this.deaths = 0;
    }

    public ClanStats(double coins, int kills, int deaths) {
        this.coins = coins;
        this.kills = kills;
        this.deaths = deaths;
    }

    public double getCoins() {
        return coins;
    }

    public void addCoins(double value) {
        if (value <= 0) return;
        this.coins+=value;
    }

    public void removeCoins(double value) {
        if (value <= 0) return;
        this.coins-=value;
    }

    public void setCoins(double value) {
        if (value < 0) return;
        this.coins = value;
    }

    public int getKills() {
        return kills;
    }

    public void addKill(int value) {
        if (value <= 0) return;
        this.kills+=value;
    }

    public void removeKill(int value) {
        if (value <= 0) return;
        this.kills-=value;
    }

    public void setKills(int value) {
        if (value < 0) return;
        this.kills = value;
    }

    public int getDeaths() {
        return deaths;
    }

    public void addDeath(int value) {
        if (value <= 0) return;
        this.deaths+=value;
    }

    public void removeDeath(int value) {
        if (value <= 0) return;
        this.deaths-=value;
    }

    public void setDeaths(int value) {
        if (value < 0) return;
        this.deaths = value;
    }

    public float getKDR() {
        if (deaths == 0) return 1;
        return ((float) kills / (float) deaths);
    }

    public String getFormattedKDR() {
        return String.format("%.2f", getKDR());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ClanStats)) return false;
        ClanStats clanStats = (ClanStats) obj;

        boolean coins = (this.coins == clanStats.getCoins());
        boolean kills = (this.kills == clanStats.getKills());
        boolean deaths = (this.deaths == clanStats.getDeaths());

        return coins && kills && deaths;
    }

    @Override
    public String toString() {
        return "ClanStats={" +
                "coins='" + coins + '\'' +
                ", kills='" + kills + '\'' +
                ", deaths='" + deaths + '\'' +
                '}';
    }
}