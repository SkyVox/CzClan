package com.skydhs.czclan.clan.manager;

import com.skydhs.czclan.clan.FileUtils;
import org.bukkit.ChatColor;

public enum ClanRole {
    LEADER(FileUtils.get().getString(FileUtils.Files.CONFIG, "Roles.owner.name").get(), FileUtils.get().getString(FileUtils.Files.CONFIG, "Roles.owner.color").get()),
    OFFICER(FileUtils.get().getString(FileUtils.Files.CONFIG, "Roles.officer.name").get(), FileUtils.get().getString(FileUtils.Files.CONFIG, "Roles.officer.color").get()),
    MEMBER(FileUtils.get().getString(FileUtils.Files.CONFIG, "Roles.member.name").get(), FileUtils.get().getString(FileUtils.Files.CONFIG, "Roles.member.color").get());

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