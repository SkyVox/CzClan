package com.skydhs.czclan.clan.interfaces;

public interface GeneralStats {

    /**
     * Get the current coins
     * amount.
     *
     * @return coins.
     */
    double getCoins();

    /**
     * Set coins.
     *
     * @param value amount to set.
     */
    void setCoins(double value);

    /**
     * Get kills.
     *
     * @return kills.
     */
    int getKills();

    /**
     * Set kills.
     *
     * @param value amount to set.
     */
    void setKills(int value);

    /**
     * Get deaths.
     *
     * @return deaths.
     */
    int getDeaths();

    /**
     * Set deaths.
     *
     * @param value amount set set.
     */
    void setDeaths(int value);

    /**
     * Reset all stats
     * information.
     *
     * This cannot be
     * undone. Make sure
     * before call this.
     */
    void resetStats();

    /**
     * Get the current
     * KDR.
     *
     * @return
     */
    float getKDR();

    /**
     * Get the formatted
     * kdr.
     *
     * @return formatted KDR.
     */
    String getFormattedKDR();
}