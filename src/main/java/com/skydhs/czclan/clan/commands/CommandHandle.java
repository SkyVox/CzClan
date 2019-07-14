package com.skydhs.czclan.clan.commands;

import com.skydhs.czclan.clan.Core;
import com.skydhs.czclan.clan.FileUtils;
import com.skydhs.czclan.clan.manager.ClanManager;
import com.skydhs.czclan.clan.manager.ClanSettings;
import com.skydhs.czclan.clan.manager.objects.ClanMember;
import org.bukkit.entity.Player;

public class CommandHandle {
    private static FileUtils file = FileUtils.get();
    private static final FileUtils.Files CONFIG = FileUtils.Files.CONFIG;

    public static boolean create(Player player, ClanMember member, String[] args) {
        if (args.length <= 2) {
            player.sendMessage(file.getString(CONFIG, "Commands.create-clan-help").getColored());
            return false;
        }

        if (member != null && member.hasClan()) {
            player.sendMessage(file.getString(CONFIG, "Messages.already-has-clan").getColored());
            return false;
        }

        String name = ClanManager.getManager().stripColor(args[1]);
        String tag = ClanManager.getManager().stripColor(args[2]);

        if (name == null || tag == null) {
            player.sendMessage(file.getString(CONFIG, "Messages.invalid-name-or-tag").getColored());
            return false;
        }

        if (name.length() > ClanSettings.CLAN_NAME_MAX_SIZE || name.length() < ClanSettings.CLAN_NAME_MIN_SIZE) {
            player.sendMessage(file.getString(CONFIG, "Messages.invalid-name-size").getColoredString(new String[] { "%max_length%", "%min_length%" }, new String[] { String.valueOf(ClanSettings.CLAN_NAME_MAX_SIZE), String.valueOf(ClanSettings.CLAN_NAME_MIN_SIZE) }));
            return false;
        }

        if (tag.length() > ClanSettings.CLAN_TAG_MAX_SIZE || tag.length() < ClanSettings.CLAN_TAG_MIN_SIZE) {
            player.sendMessage(file.getString(CONFIG, "Messages.invalid-tag-size").getColoredString(new String[] { "%max_length%", "%min_length%" }, new String[] { String.valueOf(ClanSettings.CLAN_TAG_MAX_SIZE), String.valueOf(ClanSettings.CLAN_TAG_MIN_SIZE) }));
            return false;
        }

        if (name.matches(ClanSettings.CLAN_NAMES_REGEX) || tag.matches(ClanSettings.CLAN_NAMES_REGEX)) {
            player.sendMessage(file.getString(CONFIG, "Messages.invalid-characters").getColored());
            return false;
        }

        if (!ClanManager.getManager().isNameInUse(name)) {
            player.sendMessage(file.getString(CONFIG, "Messages.name-already-in-use").getColored());
            return false;
        }

        if (!ClanManager.getManager().isTagInUse(tag)) {
            player.sendMessage(file.getString(CONFIG, "Messages.tag-already-in-use").getColored());
            return false;
        }

        String description = (args.length <= 3 ? null : ClanManager.getManager().stripColor(args[3]));
        ClanManager.getManager().create(player, member, name, args[2], description);
        return true;
    }

    public static void top(Core core, Player player, String[] args) {
        core.getAddon().commandTop(player);
    }
}