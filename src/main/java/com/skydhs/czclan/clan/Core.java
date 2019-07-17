package com.skydhs.czclan.clan;

import com.skydhs.czclan.addon.Addon;
import com.skydhs.czclan.clan.commands.ClanAdminCmd;
import com.skydhs.czclan.clan.commands.ClanCmd;
import com.skydhs.czclan.clan.database.DBManager;
import com.skydhs.czclan.clan.listener.PlayerJoinListener;
import com.skydhs.czclan.clan.listener.PlayerQuitListener;
import com.skydhs.czclan.clan.manager.ClanManager;
import com.skydhs.czclan.clan.manager.format.NumberFormat;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin {
    private final String NAME = getDescription().getName();
    private final String VERSION = getDescription().getVersion();

    private ConsoleCommandSender console = Bukkit.getConsoleSender();

    private NumberFormat numberFormat;
    private Addon addon;

    @Override
    public void onEnable() {
        long time = System.currentTimeMillis();
        sendMessage("----------");
        sendMessage(ChatColor.GRAY + "Enabling " + ChatColor.YELLOW +  NAME + ChatColor.DARK_GRAY + "> " + ChatColor.GRAY + "Version: " + ChatColor.YELLOW + VERSION + ChatColor.GRAY + "!");

        // -- Generate and setup the configuration files -- \\
        sendMessage("Loading the configuration files...");
        new FileUtils(this);

        sendMessage("Loading dependencies and instances...");
        new Log(this);
        DBManager database = new DBManager();
        if (!FileUtils.get().getFile(FileUtils.Files.MYSQL).get().getBoolean("MySQL.enabled")) {
            sendMessage(ChatColor.RED + "Could not start " + ChatColor.YELLOW + NAME + ChatColor.RED + "!");
            sendMessage(ChatColor.GRAY + "MySQL are disabled, since this plugin depends on it,");
            sendMessage(ChatColor.GRAY + "this plugin will be automatically disabled!");
            sendMessage(ChatColor.GRAY + "Enable MySQL at, plugins/" + NAME + "/mysql.yml.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            FileConfiguration file = FileUtils.get().getFile(FileUtils.Files.MYSQL).get();
            database.enable(file.getString("MySQL.host"), file.getInt("MySQL.port"), file.getString("MySQL.database"), file.getString("MySQL.username"), file.getString("MySQL.password"));
        }

        char h = '0';

        for (int i = 0; i < 10; i++) {
//            int a = (int) h+1;
//            h = (char)a;

            System.out.println("Char == '" + (h+1) + "';");
        }

        this.numberFormat = new NumberFormat(FileUtils.get().getFile(FileUtils.Files.CONFIG).get());
        new ClanManager(this);
        // For the last, we should call the Addon class.
        this.addon = new Addon(this);

        //* Setup the shop commands. *//
        sendMessage("Loading commands and listeners...");
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginCommand("clan").setExecutor(new ClanCmd(this));
        getServer().getPluginCommand("clanadmin").setExecutor(new ClanAdminCmd());

        sendMessage(ChatColor.YELLOW +  NAME + ChatColor.DARK_GRAY + "> " + ChatColor.GRAY + "has been enabled! Took " + getSpentTime(time) + "ms.");
        sendMessage("----------");
    }

    @Override
    public void onDisable() {
        sendMessage("----------");
        sendMessage(ChatColor.GRAY + "Disabling " + ChatColor.YELLOW +  NAME + ChatColor.DARK_GRAY + "> " + ChatColor.GRAY + "Version: " + ChatColor.YELLOW + VERSION + ChatColor.GRAY + "!");

        if (DBManager.getDBManager().isEnabled()) {
            ClanManager.getManager().update();
            DBManager.getDBManager().disable();

            ClanManager.getManager().getDeletedClans().clear();
            ClanManager.getManager().getLoadedClans().clear();
        }

        this.numberFormat = null;
        this.addon = null;

        sendMessage(ChatColor.YELLOW +  NAME + ChatColor.DARK_GRAY + "> " + ChatColor.GRAY + "has been disabled!");
        sendMessage("----------");
    }

    public Addon getAddon() {
        return addon;
    }

    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    private void sendMessage(String text) {
        Validate.notNull(text, "Text cannot be null.");
        console.sendMessage(StringUtils.replace(text, "&", "§"));
    }

    private long getSpentTime(long time) {
        return (System.currentTimeMillis() - time);
    }
}