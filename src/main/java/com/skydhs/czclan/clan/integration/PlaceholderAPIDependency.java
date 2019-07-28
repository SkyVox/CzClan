package com.skydhs.czclan.clan.integration;

public class PlaceholderAPIDependency {
    private static Boolean enabled = false;

    public static Boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(Boolean value) {
        PlaceholderAPIDependency.enabled = value;
    }
}