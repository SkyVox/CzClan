package com.skydhs.czclan.clan;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
    private static Logger logger;

    public Log(Core core) {
        Log.logger = core.getLogger();
    }

    public static void sendMessage(final String message) {
        sendMessage(Level.INFO, message);
    }

    public static void sendMessage(Level level, final String message) {
        logger.log(level, message);
    }

    public static void sendPlayerMessages(final String message) {
        for (Player players : Bukkit.getOnlinePlayers()) {
            players.sendMessage(message);
        }
    }
}