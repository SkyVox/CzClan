package com.skydhs.czclan.clan;

import com.skydhs.czclan.clan.manager.ClanSettings;
import com.skydhs.czclan.clan.manager.objects.Clan;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtils {
    private static FileUtils instance;
    private Core core;

    private static final String CHARSET_NAME = "UTF-8";
    private static final String FILES_PATH = "files/%%file_name%%";

    /*
     * All loaded files.
     */
    private Map<Files, FileManager> files;

    public FileUtils(Core core) {
        FileUtils.instance = this;
        this.core = core;
        this.files = new HashMap<>(Files.values().length);

        for (Files files : Files.values()) {
            this.files.put(files, new FileManager(files));
        }
    }

    public Boolean getBoolean(Files file, String path) {
        return StringUtils.equalsIgnoreCase(getFile(file).get().getString(path), "true");
    }

    public int getInt(Files file, String path) {
        return getFile(file).get().getInt(path);
    }

    public double getDouble(Files file, String path) {
        return getFile(file).get().getDouble(path);
    }

    public float getFloat(Files file, String path) {
        return (float) getFile(file).get().getDouble(path);
    }

    public StringReplace getString(Files file, String path) {
        return new StringReplace(getFile(file).get().getString(path));
    }

    public ListReplace getStringList(Files file, String path) {
        return new ListReplace(getFile(file).get().getStringList(path));
    }

    public Set<String> getSection(Files file, String path) {
        return getFile(file).get().getConfigurationSection(path).getKeys(false);
    }

    public FileManager getFile(Files file) {
        return getFiles().get(file);
    }

    private Map<Files, FileManager> getFiles() {
        return files;
    }

    public static FileUtils get() {
        return instance;
    }

    private static void copy(InputStream is, File file, Logger logger) {
        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;

            while ((len=is.read(buf)) > 0) {
                out.write(buf,0,len);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "An error has occurred while copying an configuration file.", ex);
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "An error has occurred while closing OutputStream!", ex);
            }
        }
    }

    public enum Files {
        ADDON("addon", "yml"),
        CONFIG("config", "yml"),
        MYSQL("mysql", "yml");

        public String name;
        public String extension;

        Files(String name, String extension) {
            this.name = name;
            this.extension = extension;
        }

        public String getName() {
            return name;
        }

        public String getExtension() {
            return extension;
        }
    }

    public class FileManager {
        private Files file;
        private File pdfile;
        private FileConfiguration language;

        public FileManager(Files file) {
            this.file = file;
            this.pdfile = new File(core.getDataFolder(), file.getName() + '.' + file.getExtension());

            if (!this.pdfile.exists()) {
                InputStream is = null;

                try {
                    pdfile.getParentFile().mkdirs();
                    pdfile.createNewFile();

                    is = core.getResource(file.getName() + '.' + file.getExtension());

                    if (is == null) {
                        copy(core.getResource(StringUtils.replace(FILES_PATH, "%%file_name%%", file.getName()) + '.' + file.getExtension()), pdfile, core.getLogger());
                    } else {
                        copy(is, pdfile, core.getLogger());
                    }
                } catch (IOException ex) {
                    core.getLogger().log(Level.SEVERE, "Could not create " + file.getName() + "!", ex);
                } finally {
                    try {
                        if (is != null) is.close();
                    } catch (IOException ex) {
                        core.getLogger().log(Level.SEVERE, "An error has occurred while closing InputStream!", ex);
                    }
                }
            }

            BufferedReader reader = null;

            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(pdfile), CHARSET_NAME));
                language = YamlConfiguration.loadConfiguration(reader);
            } catch (UnsupportedEncodingException | FileNotFoundException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (reader != null) reader.close();
                } catch (IOException ex) {
                    core.getLogger().log(Level.SEVERE, "An error has occurred while closing BufferedReader!", ex);
                }
            }
        }

        /**
         * Get fileConfiguration
         *
         * @return language.
         */
        public FileConfiguration get() {
            return language;
        }

        /**
         * Get file
         *
         * @return pdfile.
         */
        public File getFile() {
            return pdfile;
        }

        /**
         * Save configuration file
         *
         * @return if file has been saved.
         */
        public boolean save() {
            try {
                language.save(pdfile);
                return true;
            } catch (IOException ex) {
                core.getLogger().log(Level.SEVERE, "Could not save " + file.getName() + "!", ex);
            }

            return false;
        }

        /**
         * Reload configuration file
         *
         * @return if file has been reloaded.
         */
        public boolean reload() {
            try {
                language = YamlConfiguration.loadConfiguration(pdfile);
                return true;
            } catch (Exception ex) {
                core.getLogger().log(Level.SEVERE, "Could not reload " + file.getName() + "!", ex);
            }

            return false;
        }
    }

    public class StringReplace {
        private String str;

        public StringReplace(String str) {
            this.str = str;
        }

        public String get() {
            return str;
        }

        public String getColored() {
            return ChatColor.translateAlternateColorCodes('&', str);
        }

        public String getString(String[] placeholders, String[] replaces) {
            return setPlaceholder(placeholders, replaces);
        }

        public String getString(Player player, Clan clan) {
            if (str == null || str.isEmpty()) return str;
            if (clan == null) return getColored();

            str = ChatColor.translateAlternateColorCodes('&', StringUtils.replaceEach(str, new String[] {
                    // Clan Placeholders.
                    "%sky_clan_creator_uuid%",
                    "%sky_clan_creator_name%",
                    "%sky_clan_leader_name%",
                    "%sky_clan_name%",
                    "%sky_clan_tag%",
                    "%sky_clan_uncolored_tag%",
                    "%sky_clan_description%",
                    "%sky_clan_created_date%",
                    "%sky_clan_friendly_fire%",
                    "%sky_clan_coins%",
                    "%sky_clan_kills%",
                    "%sky_clan_deaths%",
                    "%sky_clan_members_list%",
                    "%sky_clan_allies%",
                    "%sky_clan_rivals%",

                    // Player Placeholders.
                    "%player_name%"
            }, new String[] {
                    clan.getCreator().toString(),
                    clan.getCreatorName(),
                    clan.getLeaderName(),
                    clan.getName(),
                    clan.getTag(),
                    clan.getUncoloredTag(),
                    clan.getDescription(),
                    clan.getCreatedDate().toInstant().toString(),
                    clan.isFriendlyFire() ? ClanSettings.BOOLEAN_TRUE : ClanSettings.BOOLEAN_FALSE,
                    String.valueOf(clan.getCoins()),
                    String.valueOf(clan.getKills()),
                    String.valueOf(clan.getDeaths()),
                    StringUtils.join(clan.getMembers(), ','),
                    StringUtils.join(clan.getClanAllies(), ','),
                    StringUtils.join(clan.getClanRivals(), ','),
                    player == null ? "" : player.getName()
            }));

            return str;
        }

        public String getColoredString(String[] placeholders, String[] replaces) {
            return ChatColor.translateAlternateColorCodes('&', setPlaceholder(placeholders, replaces));
        }

        private String setPlaceholder(String[] placeholders, String[] replaces) {
            if (str == null || str.isEmpty()) return str;

            if (placeholders != null && replaces != null && placeholders.length == replaces.length) {
                str = StringUtils.replaceEach(str, placeholders, replaces);
            }

            return str;
        }
    }

    public class ListReplace {
        private String[] str;

        public ListReplace(String[] str) {
            this.str = str;
        }

        public ListReplace(List<String> list) {
            this.str = new String[list.size()];

            for (int i = 0; i < list.size(); i++) {
                this.str[i] = list.get(i);
            }
        }

        public String[] get() {
            return str;
        }

        public String[] getColored() {
            String[] ret = new String[str.length];

            for (int i = 0; i < str.length; i++) {
                ret[i] = ChatColor.translateAlternateColorCodes('&', str[i]);
            }

            return ret;
        }

        public String[] getList(String[] placeholders, String[] replaces) {
            return setPlaceholder(placeholders, replaces);
        }

        public String[] getList(Player player, Clan clan) {
            if (str == null || str.length <= 0) return str;
            if (clan == null) return getColored();

            String[] ret = new String[str.length];

            for (int i = 0; i < str.length; i++) {
                ret[i] = ChatColor.translateAlternateColorCodes('&', StringUtils.replaceEach(str[i], new String[] {
                        // Clan Placeholders.
                        "%sky_clan_creator_uuid%",
                        "%sky_clan_creator_name%",
                        "%sky_clan_leader_name%",
                        "%sky_clan_name%",
                        "%sky_clan_tag%",
                        "%sky_clan_uncolored_tag%",
                        "%sky_clan_description%",
                        "%sky_clan_created_date%",
                        "%sky_clan_friendly_fire%",
                        "%sky_clan_coins%",
                        "%sky_clan_kills%",
                        "%sky_clan_deaths%",
                        "%sky_clan_members_list%",
                        "%sky_clan_allies%",
                        "%sky_clan_rivals%",

                        // Player Placeholders.
                        "%player_name%"
                }, new String[] {
                        clan.getCreator().toString(),
                        clan.getCreatorName(),
                        clan.getLeaderName(),
                        clan.getName(),
                        clan.getTag(),
                        clan.getUncoloredTag(),
                        clan.getDescription(),
                        clan.getCreatedDate().toInstant().toString(),
                        clan.isFriendlyFire() ? ClanSettings.BOOLEAN_TRUE : ClanSettings.BOOLEAN_FALSE,
                        String.valueOf(clan.getCoins()),
                        String.valueOf(clan.getKills()),
                        String.valueOf(clan.getDeaths()),
                        StringUtils.join(clan.getMembers(), ','),
                        StringUtils.join(clan.getClanAllies(), ','),
                        StringUtils.join(clan.getClanRivals(), ','),
                        player == null ? "" : player.getName()
                }));
            }

            return ret;
        }

        private String[] setPlaceholder(String[] placeholders, String[] replaces) {
            if (str == null || str.length <= 0) return str;

            // Create new instance.
            String[] ret = new String[str.length];

            if (placeholders != null && replaces != null && placeholders.length == replaces.length) {
                for (int i = 0; i < str.length; i++) {
                    ret[i] = ChatColor.translateAlternateColorCodes('&', StringUtils.replaceEach(str[i], placeholders, replaces));
                }
            }

            return ret;
        }
    }
}