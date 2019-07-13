package com.skydhs.czclan.clan.manager;

public enum AccessType {
    NAME("name"),
    UUID("uuid");

    private String column;

    AccessType(String column) {
        this.column = column;
    }

    public String getColumn() {
        return column;
    }
}