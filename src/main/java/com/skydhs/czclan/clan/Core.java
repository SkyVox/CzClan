package com.skydhs.czclan.clan;

import com.skydhs.czclan.clan.commands.ClanAdminCmd;
import com.skydhs.czclan.clan.commands.ClanCmd;
import com.skydhs.czclan.clan.database.DBManager;
import com.skydhs.czclan.clan.listener.PlayerJoinListener;
import com.skydhs.czclan.clan.manager.format.NumberFormat;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin {
    private final String NAME = getDescription().getName();
    private final String VERSION = getDescription().getVersion();

    private ConsoleCommandSender console = Bukkit.getConsoleSender();

    @Override
    public void onEnable() {
        long time = System.currentTimeMillis();
        sendMessage("----------");
        sendMessage(ChatColor.GRAY + "Enabling " + ChatColor.YELLOW +  NAME + ChatColor.DARK_GRAY + "> " + ChatColor.GRAY + "Version: " + ChatColor.YELLOW + VERSION + ChatColor.GRAY + "!");

        // -- Generate and setup the configuration files -- \\
        sendMessage("Loading the configuration files...");
        new FileUtils(this);

        sendMessage("Loading instances and Listeners...");
        new DBManager(FileUtils.get().getFile(FileUtils.Files.CONFIG).get());
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
//        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);

        new NumberFormat(FileUtils.get().getFile(FileUtils.Files.CONFIG).get());

        //* Setup the shop commands. *//
        sendMessage("Loading commands...");
        getServer().getPluginCommand("clan").setExecutor(new ClanCmd());
        getServer().getPluginCommand("clanadmin").setExecutor(new ClanAdminCmd());

        sendMessage(ChatColor.YELLOW +  NAME + ChatColor.DARK_GRAY + "> " + ChatColor.GRAY + "has been enabled! Took " + getSpentTime(time) + "ms.");
        sendMessage("----------");
    }

    @Override
    public void onDisable() {}

    private void sendMessage(String text) {
        Validate.notNull(text, "Text cannot be null.");
        console.sendMessage(StringUtils.replace(text, "&", "§"));
    }

    private long getSpentTime(long time) {
        return (System.currentTimeMillis() - time);
    }
}