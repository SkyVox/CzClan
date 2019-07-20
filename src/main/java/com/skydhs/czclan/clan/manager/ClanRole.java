package com.skydhs.czclan.clan.manager;

import com.skydhs.czclan.clan.FileUtils;
import org.bukkit.ChatColor;

public enum ClanRole {
    UNRANKED(0, FileUtils.get().getString(FileUtils.Files.CONFIG, "Roles.unranked.name").get(), FileUtils.get().getString(FileUtils.Files.CONFIG, "Roles.unranked.color").get()),
    MEMBER(1, FileUtils.get().getString(FileUtils.Files.CONFIG, "Roles.member.name").get(), FileUtils.get().getString(FileUtils.Files.CONFIG, "Roles.member.color").get()),
    OFFICER(2, FileUtils.get().getString(FileUtils.Files.CONFIG, "Roles.officer.name").get(), FileUtils.get().getString(FileUtils.Files.CONFIG, "Roles.officer.color").get()),
    LEADER(3, FileUtils.get().getString(FileUtils.Files.CONFIG, "Roles.owner.name").get(), FileUtils.get().getString(FileUtils.Files.CONFIG, "Roles.owner.color").get());

    private int id;
    private String translated;
    private String color;

    ClanRole(int id, String translated, String color) {
        this.id = id;
        this.translated = translated;
        this.color = color;
    }

    public static ClanRole getById(int id) {
        for (ClanRole value : ClanRole.values()) {
            if (value.getId() == id) return value;
        }

        return null;
    }

    public int getId() {
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

    public ClanRole getNext() {
        int nextId = id + 1;

        ClanRole role = getById(nextId);
        if (role == null) return ClanRole.LEADER;
        return role;
    }

    public ClanRole getPrevious() {
        int id = (this.id-1);
        if (id <= 0) return ClanRole.MEMBER;

        ClanRole role = getById(id);
        if (role == null) return ClanRole.MEMBER;
        return role;
    }
}