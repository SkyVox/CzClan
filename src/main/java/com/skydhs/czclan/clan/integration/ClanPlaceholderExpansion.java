package com.skydhs.czclan.clan.integration;

import com.skydhs.czclan.clan.Core;
import com.skydhs.czclan.clan.manager.ClanManager;
import com.skydhs.czclan.clan.manager.ClanRole;
import com.skydhs.czclan.clan.manager.ClanSettings;
import com.skydhs.czclan.clan.manager.objects.Clan;
import com.skydhs.czclan.clan.manager.objects.ClanMember;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.time.format.DateTimeFormatter;

public class ClanPlaceholderExpansion extends PlaceholderExpansion {
    private Core core;

    public ClanPlaceholderExpansion(Core core) {
        this.core = core;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return core.getDescription().getAuthors().get(0);
    }

    @Override
    public String getIdentifier() {
        return "skyclan";
    }

    @Override
    public String getVersion() {
        return core.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) return null;

        ClanMember member = ClanManager.getManager().getClanMember(player.getName());
        if (member == null) return null;

        switch (identifier.toLowerCase()) {
            case "member_name": // %skyclan_member_name%
                return member.getName();
            case "member_clan_tag": // %skyclan_member_clan_tag%
                return member.getTag();
            case "member_clan_formatted_tag": // %skyclan_member_clan_formatted_tag%
                return member.hasClan() ? member.getClan().getColoredTag() : ClanRole.UNRANKED.getFullName();
            case "member_role": // %skyclan_member_role%
                return member.getRole().getFullName();
            case "member_raw_role": // %skyclan_member_raw_role%
                return member.getRole().getRoleTranslation();
            case "member_joined_date": // %skyclan_member_joined_date%
                if (member.getJoinedDate() != null) {
                    return DateTimeFormatter.ofPattern(ClanSettings.DATE_FORMAT_PATTERN).format(member.getJoinedDate());
                } else return null;
            case "member_kills": // %skyclan_member_kills%
                return String.valueOf(member.getPlayerStats().getKills());
            case "member_deaths": // %skyclan_member_deaths%
                return String.valueOf(member.getPlayerStats().getDeaths());
            case "member_kdr": // %skyclan_member_kdr%
                return member.getPlayerStats().getFormattedKDR();
        }

        if (!member.hasClan()) return null;

        Clan clan = member.getClan();
        if (clan == null) return null;

        switch (identifier.toLowerCase()) {
            case "creator_uuid": // %skyclan_creator_uuid%
                return clan.getCreator().toString();
            case "creator_name": // %skyclan_creator_name%
                return clan.getCreatorName();
            case "leader_name": // %skyclan_leader_name%
                return clan.getLeaderName();
            case "clan_name": // %skyclan_clan_name%
                return clan.getName();
            case "clan_tag": // %skyclan_clan_tag%
                return clan.getColoredTag();
            case "uncolored_tag": // %skyclan_uncolored_tag%
                return clan.getUncoloredTag();
            case "clan_description": // %skyclan_clan_description%
                return clan.getDescription();
            case "created_date": // %skyclan_created_date%
                return DateTimeFormatter.ofPattern(ClanSettings.DATE_FORMAT_PATTERN).format(clan.getCreatedDate());
            case "friendly_fire": // %skyclan_friendly_fire%
                return clan.isFriendlyFire() ? ClanSettings.BOOLEAN_TRUE : ClanSettings.BOOLEAN_FALSE;
            case "clan_coins": // %skyclan_clan_coins%
                return String.valueOf(clan.getCoins());
            case "clan_kills": // %skyclan_clan_kills%
                return String.valueOf(clan.getKills());
            case "clan_deaths": // %skyclan_clan_deaths%
                return String.valueOf(clan.getDeaths());
            case "clan_kdr": // %skyclan_clan_kdr%
                return clan.getFormattedKDR();
            case "clan_raw_kdr": // %clan_raw_kdr%
                return String.valueOf(clan.getKDR());
            case "members_list": // %skyclan_members_list%
                return StringUtils.join(clan.getMembers(), ',');
            case "clan_allies": // %skyclan_clan_allies%
                return clan.getFormattedAllies(',');
            case "clan_rivals": // %skyclan_clan_rivals%
                return clan.getFormattedRivals(',');
        }

        return null;
    }
}