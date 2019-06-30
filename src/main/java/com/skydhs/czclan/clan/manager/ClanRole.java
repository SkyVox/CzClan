package com.skydhs.czclan.clan.manager;

import org.bukkit.ChatColor;

public enum ClanRole { // TODO, get the enum values from config file.
    LEADER("Dono", "&4"),
    OFFICER("ADMIN", "&c"),
    MEMBER("MEMBRO", "&7");

    private String translated;
    private String color;

    ClanRole(String translated, String color) {
        this.translated = translated;
        this.color = color;
    }
    
    public String getRoleTranslation() {
        return translated;
    }

    public String getRoleColor() {
        return color;
    }

    public String getFullName() {
        return ChatColor.translateAlternateColorCodes('&', getRoleColor() + "" + getRoleTranslation());
    }

    public boolean isAtLeast(ClanRole role) {
        return this.ordinal() >= role.ordinal();
    }

    public boolean isAtMost(ClanRole role) {
        return this.ordinal() <= role.ordinal();
    }

    public boolean isLessThan(ClanRole role) {
        return this.ordinal() < role.ordinal();
    }

    public boolean isMoreThan(ClanRole role) {
        return this.ordinal() > role.ordinal();
    }
}