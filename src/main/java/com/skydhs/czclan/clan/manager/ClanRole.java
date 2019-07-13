package com.skydhs.czclan.clan.manager;

import com.skydhs.czclan.clan.FileUtils;
import org.bukkit.ChatColor;

public enum ClanRole {
    LEADER('0', FileUtils.get().getString(FileUtils.Files.CONFIG, "Roles.owner.name").get(), FileUtils.get().getString(FileUtils.Files.CONFIG, "Roles.owner.color").get()),
    OFFICER('1', FileUtils.get().getString(FileUtils.Files.CONFIG, "Roles.officer.name").get(), FileUtils.get().getString(FileUtils.Files.CONFIG, "Roles.officer.color").get()),
    MEMBER('2', FileUtils.get().getString(FileUtils.Files.CONFIG, "Roles.member.name").get(), FileUtils.get().getString(FileUtils.Files.CONFIG, "Roles.member.color").get());

    private char id;
    private String translated;
    private String color;

    ClanRole(char id, String translated, String color) {
        this.id = id;
        this.translated = translated;
        this.color = color;
    }

    public static ClanRole getById(char id) {
        for (ClanRole value : ClanRole.values()) {
            if (value.getId() == id) return value;
        }

        return null;
    }

    public char getId() {
        return id;
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