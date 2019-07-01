package com.skydhs.czclan.clan.manager;

import org.bukkit.Location;

import java.util.UUID;

public class ClanManager {
    private static ClanManager manager;

    public boolean isLocationEquals(Location one, Location two) {
        if (one == null || two == null) return false;
        return one.getBlockX() == two.getBlockX() && one.getBlockY() == two.getBlockY() && one.getBlockZ() == two.getBlockZ();
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

    public static ClanManager getManager() {
        return manager;
    }
}