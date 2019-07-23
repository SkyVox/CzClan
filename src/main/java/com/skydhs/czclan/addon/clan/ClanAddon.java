package com.skydhs.czclan.addon.clan;

public abstract class ClanAddon {
    private int gladiatorWins;
    private int gladiatorLosses;
    private int miniGladiatorWins;
    private int miniGladiatorLosses;

    public ClanAddon(int gladiatorWins, int gladiatorLosses, int miniGladiatorWins, int miniGladiatorLosses) {
        this.gladiatorWins = gladiatorWins;
        this.gladiatorLosses = gladiatorLosses;
        this.miniGladiatorWins = miniGladiatorWins;
        this.miniGladiatorLosses = miniGladiatorLosses;
    }

    /**
     * Get how many wars this
     * clan won.
     *
     * @return how many times this
     *         clan won on gladiator.
     */
    public int getGladiatorWins() {
        return gladiatorWins;
    }

    /**
     * Calling this method
     * will be added plus
     * one gladiator win
     * on this clan stats.
     */
    public void addGladiatorWin() {
        this.gladiatorWins+=1;
    }

    /**
     * Calling this method
     * will be removed one
     * gladiator win on
     * this clan stats.
     */
    public void removeGladiatorWin() {
        this.gladiatorWins-=1;
    }

    /**
     * Set the gladiator wins.
     *
     * @param value wins to set.
     */
    public void setGladiatorWins(int value) {
        this.gladiatorWins = value;
    }

    /**
     * Get how many wars this
     * clan lose.
     *
     * @return how many times this
     *         clan lose a gladiator.
     */
    public int getGladiatorLosses() {
        return gladiatorLosses;
    }

    /**
     * Calling this method
     * will be added plus
     * one gladiator loss
     * on this clan stats.
     */
    public void addGladiatorLoss() {
        this.gladiatorLosses+=1;
    }

    /**
     * Calling this method
     * will be removed one
     * gladiator loss on
     * this clan stats.
     */
    public void removeGladiatorLoss() {
        this.gladiatorLosses-=1;
    }

    /**
     * Set the gladiator loses.
     *
     * @param value losses to set.
     */
    public void setGladiatorLosses(int value) {
        this.gladiatorLosses = value;
    }

    // ---------- \\
    // The Mini Gladiator information.
    // ---------- \\

    /**
     * Get how many mini gladiator
     * this clan won.
     *
     * @return how many times this
     *         clan won mini gladiators.
     */
    public int getMiniGladiatorWins() {
        return miniGladiatorWins;
    }

    /**
     * Calling this method
     * will be added plus
     * one miniGladiator
     * win on this clan stats.
     */
    public void addMiniGladiatorWin() {
        this.miniGladiatorWins+=1;
    }

    /**
     * Calling this method
     * will be removed one
     * miniGladiator win
     * on this clan stats.
     */
    public void removeMiniGladiatorWin() {
        this.miniGladiatorWins-=1;
    }

    /**
     * Set mini Gladiator wins.
     *
     * @param value wins to set.
     */
    public void setMiniGladiatorWins(int value) {
        this.miniGladiatorWins = value;
    }

    /**
     * Get how many miniGladiator
     * losses this clan has.
     *
     * @return how many times this
     *         clan lose an miniGladiator.
     */
    public int getMiniGladiatorLosses() {
        return miniGladiatorLosses;
    }

    /**
     * Calling this method
     * will be added plus
     * one miniGladiator loss
     * on this clan stats.
     */
    public void addMiniGladiatorLoss() {
        this.miniGladiatorLosses+=1;
    }

    /**
     * Calling this method
     * will be remobed one
     * miniGladiator loss
     * on this clan stats.
     */
    public void removeMiniGladiatorLoss() {
        this.miniGladiatorLosses-=1;
    }

    /**
     * Set mini Gladiator loses.
     *
     * @param value losses to set.
     */
    public void setMiniGladiatorLosses(int value) {
        this.miniGladiatorLosses = value;
    }
}