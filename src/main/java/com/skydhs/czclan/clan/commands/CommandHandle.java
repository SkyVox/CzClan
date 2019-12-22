package com.skydhs.czclan.clan.commands;

import com.skydhs.czclan.clan.Core;
import com.skydhs.czclan.clan.FileUtils;
import com.skydhs.czclan.clan.integration.PlaceholderAPIDependency;
import com.skydhs.czclan.clan.manager.ClanManager;
import com.skydhs.czclan.clan.manager.ClanRole;
import com.skydhs.czclan.clan.manager.ClanSettings;
import com.skydhs.czclan.clan.manager.Cooldown;
import com.skydhs.czclan.clan.manager.objects.Clan;
import com.skydhs.czclan.clan.manager.objects.ClanMember;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

public class CommandHandle {
    private static FileUtils file = FileUtils.get();
    private static final FileUtils.Files CONFIG = FileUtils.Files.CONFIG;

    static void main(Core core, Player player, ClanMember member) {
        core.getAddon().commandMain(player, member);
    }

    static boolean create(Player player, ClanMember member, String[] args) {
        if (args.length <= 2) {
            player.sendMessage(file.getString(CONFIG, "Commands.create-clan-help").getColored());
            return false;
        }

        if (member != null && member.hasClan()) {
            player.sendMessage(file.getString(CONFIG, "Messages.already-has-clan").getColored());
            return false;
        }

        if (Cooldown.isInCooldown(player.getUniqueId(), "Clan_Creation_Cooldown")) {
            player.sendMessage(file.getString(CONFIG, "Messages.clan-creation-cooldown").getColoredString(new String[] {
                    "%seconds%"
            }, new String[] {
                    String.valueOf(Cooldown.getTimeLeft(player.getUniqueId(), "Clan_Creation_Cooldown"))
            }));
            return false;
        } else {
            Cooldown cooldown = new Cooldown(player.getUniqueId(), "Clan_Creation_Cooldown", ClanSettings.CLAN_CREATION_COOLDOWN);
            cooldown.start();
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

    static void top(Core core, Player player, String[] args) {
        core.getAddon().commandTop(player);
    }

    static void player(Core core, Player player, ClanMember member) {
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

    static void clan(Core core, Player player, Clan clan) {
        if (clan == null || clan.isNull()) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.clan-not-found").getColored());
            return;
        }

        core.getAddon().commandClan(player, clan);
    }

    static void members(Core core, Player player, Clan clan) {
        if (clan == null || clan.isNull()) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.clan-not-found").getColored());
            return;
        }

        core.getAddon().commandMembers(player, clan);
    }

    static void promote(Player player, ClanMember member, Clan clan, String[] args) {
        if (clan == null || clan.isNull()) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.not-in-clan").getColored());
            return;
        }

        if (args.length < 2) {
            player.sendMessage(file.getString(CONFIG, "Commands.promote-member-help").getColored());
            return;
        }

        if (!member.getRole().isAtLeast(ClanRole.LEADER)) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.role-required").getColoredString(new String[] { "%skyclan_member_role%" }, new String[] { ClanRole.LEADER.getFullName() }));
            return;
        }

        if (player.getName().equalsIgnoreCase(args[1])) {
            player.sendMessage(file.getString(CONFIG, "Messages.cannot-execute-yourself").getColored());
            return;
        }

        String target = args[1];
        ClanMember targetMember = clan.getMember(target);

        if (targetMember == null) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.target-not-found").getColored());
            return;
        }

        if (!targetMember.hasClan() || !clan.equals(targetMember.getClan())) {
            player.sendMessage(file.getString(CONFIG, "Messages.target-not-on-same-clan").getColoredString(new String[] { "%target_name%" }, new String[] { target }));
            return;
        }

        // Then, try to promote this player.
        clan.promoteMember(member, targetMember);
    }

    static void demote(Player player, ClanMember member, Clan clan, String[] args) {
        if (clan == null || clan.isNull()) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.not-in-clan").getColored());
            return;
        }

        if (args.length < 2) {
            player.sendMessage(file.getString(CONFIG, "Commands.demote-member-help").getColored());
            return;
        }

        if (!member.getRole().isAtLeast(ClanRole.LEADER)) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.role-required").getColoredString(new String[] { "%skyclan_member_role%" }, new String[] { ClanRole.LEADER.getFullName() }));
            return;
        }

        if (player.getName().equalsIgnoreCase(args[1])) {
            player.sendMessage(file.getString(CONFIG, "Messages.cannot-execute-yourself").getColored());
            return;
        }

        String target = args[1];
        ClanMember targetMember = clan.getMember(target);

        if (targetMember == null) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.target-not-found").getColored());
            return;
        }

        if (!targetMember.hasClan() || !clan.equals(targetMember.getClan())) {
            player.sendMessage(file.getString(CONFIG, "Messages.target-not-on-same-clan").getColoredString(new String[] { "%target_name%" }, new String[] { target }));
            return;
        }

        // Then, try to demote this player.
        clan.demoteMember(member, targetMember);
    }

    static void invite(Player player, ClanMember member, Clan clan, String[] args) {
        if (clan == null || clan.isNull()) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.not-in-clan").getColored());
            return;
        }

        if (args.length < 2) {
            player.sendMessage(file.getString(CONFIG, "Commands.invite-member-help").getColored());
            return;
        }

        if (!member.getRole().isAtLeast(ClanRole.OFFICER)) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.role-required").getColoredString(new String[] { "%skyclan_member_role%" }, new String[] { ClanRole.OFFICER.getFullName() }));
            return;
        }

        if (player.getName().equalsIgnoreCase(args[1])) {
            player.sendMessage(file.getString(CONFIG, "Messages.cannot-execute-yourself").getColored());
            return;
        }

        String target = args[1];

        if (clan.isMember(target)) {
            player.sendMessage(file.getString(CONFIG, "Messages.target-is-member-already").getColored());
            return;
        }

        ClanMember targetMember = ClanManager.getManager().getClanMember(target);

        if (targetMember == null) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.target-not-found").getColored());
            return;
        }

        if (targetMember.hasClan()) {
            player.sendMessage(file.getString(CONFIG, "Messages.target-has-clan-already").getColoredString(new String[] { "%target_name%" }, new String[] { target }));
            return;
        }

        targetMember.invitePlayer(clan);

        player.sendMessage(file.getString(CONFIG, "Messages.invite-player-sender").getString(player, clan, new String[] { "%target_name%" }, new String[] { target }));
    }

    static void accept(Player player, ClanMember member, String[] args) {
        if (member == null) return;
        if (member.hasClan()) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.already-has-clan").getColored());
            return;
        }

        if (args.length < 2) {
            player.sendMessage(file.getString(CONFIG, "Commands.accept-member-help").getColored());
            return;
        }

        String tag = ClanManager.getManager().stripColor(args[1]);

        if (!member.hasPendingInvites(tag)) {
            player.sendMessage(file.getString(CONFIG, "Messages.you-were-not-invited").getColored());
            return;
        }

        Clan clan = ClanManager.getManager().getClan(tag);

        if (clan == null || clan.isNull()) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.not-in-clan").getColored());
            return;
        }

        member.changeClan(null, clan, ClanRole.MEMBER);
        clan.addMember(member);

        player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.join-clan-target").getString(player, clan));

        for (String str : FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Messages.join-clan-broadcast").getList(null, clan, new String[] { "%target_name%" }, new String[] { player.getName() })) {
            clan.sendMessage(str);
        }
    }

    static void kick(Player player, ClanMember member, Clan clan, String[] args) {
        if (clan == null || clan.isNull()) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.not-in-clan").getColored());
            return;
        }

        if (args.length < 2) {
            player.sendMessage(file.getString(CONFIG, "Commands.kick-member-help").getColored());
            return;
        }

        if (!member.getRole().isAtLeast(ClanRole.OFFICER)) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.role-required").getColoredString(new String[] { "%skyclan_member_role%" }, new String[] { ClanRole.OFFICER.getFullName() }));
            return;
        }

        if (player.getName().equalsIgnoreCase(args[1])) {
            player.sendMessage(file.getString(CONFIG, "Messages.cannot-execute-yourself").getColored());
            return;
        }

        String target = args[1];
        ClanMember targetMember = clan.getMember(target);

        if (targetMember == null || !targetMember.hasClan() || !clan.equals(targetMember.getClan())) {
            player.sendMessage(file.getString(CONFIG, "Messages.target-not-on-same-clan").getColoredString(new String[] { "%target_name%" }, new String[] { target }));
            return;
        }

        if (!member.getRole().isMoreThan(targetMember.getRole())) {
            player.sendMessage(file.getString(CONFIG, "Messages.target-has-higher-role").getColoredString(new String[] { "%target_name%", "%skyclan_member_role%" }, new String[] { target, targetMember.getRole().getFullName() }));
            return;
        }

        clan.removeMember(targetMember);
        targetMember.changeClan(clan, null, ClanRole.UNRANKED);

        for (String str : FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Messages.player-kicked-broadcast").getList(null, clan, new String[] { "%target_name%" }, new String[] { member.getName() })) {
            clan.sendMessage(str);
        }

        player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.player-kicked-sender").getString(player, clan, new String[] { "%target_name%" }, new String[] { target }));
        targetMember.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.player-kicked-target").getString(null, clan, new String[] { "%player_name%" }, new String[] { player.getName() }));
    }

    static void disband(Player player, ClanMember member, Clan clan) {
        if (clan == null || clan.isNull()) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.not-in-clan").getColored());
            return;
        }

        if (!member.getRole().isAtLeast(ClanRole.LEADER)) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.role-required").getColoredString(new String[] { "%skyclan_member_role%" }, new String[] { ClanRole.LEADER.getFullName() }));
            return;
        }

        clan.disband();
    }

    static void leave(Player player, ClanMember member, Clan clan) {
        if (clan == null || clan.isNull()) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.not-in-clan").getColored());
            return;
        }

        clan.removeMember(member);
        member.changeClan(clan, null, ClanRole.UNRANKED);

        for (String str : FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Messages.left-clan-broadcast").getList(null, clan, new String[] { "%target_name%" }, new String[] { player.getName() })) {
            clan.sendMessage(str);
        }

        player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.left-clan-sender").getColoredString(new String[] { "%player_name%" }, new String[] { player.getName() }));
    }

    static void ally(Player player, ClanMember member, Clan clan, String[] args) {
        if (clan == null || clan.isNull()) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.not-in-clan").getColored());
            return;
        }

        if (args.length < 3) {
            player.sendMessage(file.getString(CONFIG, "Commands.ally-help").getColored());
            return;
        }

        if (!member.getRole().isAtLeast(ClanRole.LEADER)) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.role-required").getColoredString(new String[] { "%skyclan_member_role%" }, new String[] { ClanRole.LEADER.getFullName() }));
            return;
        }

        String argument = args[1].toUpperCase();
        Clan targetClan = ClanManager.getManager().getClan(args[2]);

        if (targetClan == null || targetClan.isNull()) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.clan-not-found").getColored());
            return;
        }

        String tag = targetClan.getUncoloredTag();

        switch (argument) {
            case "ADICIONAR":
            case "ADD":
                if (clan.isRival(tag)) {
                    player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.clan-is-rival").getColored());
                    return;
                }

                if (clan.isAlly(tag)) {
                    player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.clan-is-ally-already").getColored());
                    return;
                }

                clan.addAliases(targetClan);
                break;
            case "REMOVER":
            case "DEL":
                if (!clan.isAlly(tag)) {
                    player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.clan-is-not-ally").getColored());
                    return;
                }

                clan.removeAliases(targetClan);
                break;
        }
    }

    static void rival(Player player, ClanMember member, Clan clan, String[] args) {
        if (clan == null || clan.isNull()) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.not-in-clan").getColored());
            return;
        }

        if (args.length < 3) {
            player.sendMessage(file.getString(CONFIG, "Commands.rival-help").getColored());
            return;
        }

        if (!member.getRole().isAtLeast(ClanRole.LEADER)) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.role-required").getColoredString(new String[] { "%skyclan_member_role%" }, new String[] { ClanRole.LEADER.getFullName() }));
            return;
        }

        String argument = args[1].toUpperCase();
        Clan targetClan = ClanManager.getManager().getClan(args[2]);

        if (targetClan == null || targetClan.isNull()) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.clan-not-found").getColored());
            return;
        }

        String tag = targetClan.getUncoloredTag();

        switch (argument) {
            case "ADICIONAR":
            case "ADD":
                if (clan.isAlly(tag)) {
                    player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.clan-is-ally").getColored());
                    return;
                }

                if (clan.isRival(tag)) {
                    player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.clan-is-rival-already").getColored());
                    return;
                }

                clan.addRivals(targetClan);
                break;
            case "REMOVER":
            case "DEL":
                if (!clan.isRival(tag)) {
                    player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.clan-is-not-rival").getColored());
                    return;
                }

                clan.removeRivals(targetClan);
                break;
        }
    }

    public static void clanChatMessage(Player player, ClanMember member, String[] args) {
        if (member == null || !member.hasClan() || member.getClan().isNull()) {
            player.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.not-in-clan").getColored());
            return;
        }

        Clan clan = member.getClan();
        StringBuilder message = new StringBuilder(args.length);
        String format = null;

        try {
            if (PlaceholderAPIDependency.isEnabled()) {
                format = PlaceholderAPI.setPlaceholders(player, ClanSettings.CLAN_CHAT_FORMAT);
            } else {
                format = new FileUtils.StringReplace(ClanSettings.CLAN_CHAT_FORMAT).getString(player, clan);
            }
        } catch (NullPointerException ex) {
            format = new FileUtils.StringReplace(ClanSettings.CLAN_CHAT_FORMAT).getString(player, clan);
        }

        for (String str : args) {
            message.append(str).append(" ");
        }

        clan.sendMessage(StringUtils.replaceEach(format, new String[] { "%message%", "%player_name%" }, new String[] { message.toString(), player.getName() }));
        return;
    }
}