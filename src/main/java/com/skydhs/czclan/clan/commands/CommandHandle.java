package com.skydhs.czclan.clan.commands;

import com.skydhs.czclan.clan.Core;
import com.skydhs.czclan.clan.FileUtils;
import com.skydhs.czclan.clan.manager.ClanManager;
import com.skydhs.czclan.clan.manager.ClanRole;
import com.skydhs.czclan.clan.manager.ClanSettings;
import com.skydhs.czclan.clan.manager.objects.Clan;
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

        if (!name.matches(ClanSettings.CLAN_NAMES_REGEX) || !tag.matches(ClanSettings.CLAN_NAMES_REGEX)) {
            player.sendMessage(file.getString(CONFIG, "Messages.invalid-characters").getColored());
            return false;
        }

        if (ClanManager.getManager().isNameInUse(name)) {
            player.sendMessage(file.getString(CONFIG, "Messages.name-already-in-use").getColored());
            return false;
        }

        if (ClanManager.getManager().isTagInUse(tag)) {
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

    public static void player(Core core, Player player, ClanMember member) {
        if (member == null) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.target-not-found").getColored());
            return;
        }

        if (!member.hasClan()) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.target-clan-not-found").getColored());
            return;
        }

        core.getAddon().commandPlayer(player, member);
    }

    public static void clan(Core core, Player player, Clan clan) {
        if (clan == null || clan.isNull()) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.clan-not-found").getColored());
            return;
        }

        core.getAddon().commandClan(player, clan);
    }

    public static void members(Core core, Player player, Clan clan) {
        if (clan == null || clan.isNull()) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.clan-not-found").getColored());
            return;
        }

        core.getAddon().commandMembers(player, clan);
    }

    public static void promote(Player player, ClanMember member, Clan clan, String[] args) {
        if (clan == null || clan.isNull()) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.not-in-clan").getColored());
            return;
        }

        if (args.length < 2) {
            player.sendMessage(file.getString(CONFIG, "Commands.promote-member-help").getColored());
            return;
        }

        if (!member.getRole().isAtLeast(ClanRole.LEADER)) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.role-required").getColoredString(new String[] { "%role_name%" }, new String[] { ClanRole.LEADER.getFullName() }));
            return;
        }

        if (player.getName().equalsIgnoreCase(args[1])) {
            player.sendMessage(file.getString(CONFIG, "Commands.cannot-execute-yourself").getColored());
            return;
        }

        String target = args[1];
        ClanMember targetMember = clan.getMember(target);

        // Then, try to promote this player.
        clan.promoteMember(member, targetMember);
    }

    public static void demote(Player player, ClanMember member, Clan clan, String[] args) {
        if (clan == null || clan.isNull()) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.not-in-clan").getColored());
            return;
        }

        if (args.length < 2) {
            player.sendMessage(file.getString(CONFIG, "Commands.demote-member-help").getColored());
            return;
        }

        if (!member.getRole().isAtLeast(ClanRole.LEADER)) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.role-required").getColoredString(new String[] { "%role_name%" }, new String[] { ClanRole.LEADER.getFullName() }));
            return;
        }

        if (player.getName().equalsIgnoreCase(args[1])) {
            player.sendMessage(file.getString(CONFIG, "Commands.cannot-execute-yourself").getColored());
            return;
        }

        String target = args[1];
        ClanMember targetMember = clan.getMember(target);

        // Then, try to demote this player.
        clan.demoteMember(member, targetMember);
    }

    public static void kick(Player player, ClanMember member, Clan clan, String[] args) {
        if (clan == null || clan.isNull()) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.not-in-clan").getColored());
            return;
        }

        if (args.length < 2) {
            player.sendMessage(file.getString(CONFIG, "Commands.kick-member-help").getColored());
            return;
        }

        if (!member.getRole().isAtLeast(ClanRole.OFFICER)) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.role-required").getColoredString(new String[] { "%role_name%" }, new String[] { ClanRole.OFFICER.getFullName() }));
            return;
        }

        if (player.getName().equalsIgnoreCase(args[1])) {
            player.sendMessage(file.getString(CONFIG, "Commands.cannot-execute-yourself").getColored());
            return;
        }

        String target = args[1];
        ClanMember targetMember = clan.getMember(target);
        clan.removeMember(targetMember);
        // TODO, Update @ClanMember, remove his clan.
    }
}