package com.skydhs.czclan.clan.commands;

import com.skydhs.czclan.clan.Core;
import com.skydhs.czclan.clan.FileUtils;
import com.skydhs.czclan.clan.manager.ClanManager;
import com.skydhs.czclan.clan.manager.ClanRole;
import com.skydhs.czclan.clan.manager.objects.Clan;
import com.skydhs.czclan.clan.manager.objects.ClanMember;
import com.skydhs.czclan.clan.manager.objects.GeneralStats;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class ClanCmd implements CommandExecutor {
    private Core core;

    public ClanCmd(Core core) {
        this.core = core;
    }


    // class variable
    final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";

    final java.util.Random rand = new java.util.Random();

    // consider using a Map<String,Boolean> to say whether the identifier is being used or not
    final Set<String> identifiers = new HashSet<>();

    public String randomIdentifier() {
        StringBuilder builder = new StringBuilder();
        while(builder.toString().length() == 0) {
            int length = rand.nextInt(10)+5;
            for(int i = 0; i < length; i++) {
                builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
            }
            if(identifiers.contains(builder.toString())) {
                builder = new StringBuilder();
            }
        }
        return builder.toString();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(FileUtils.get().getString(FileUtils.Files.CONFIG, "Commands.only-players").get());
            return true;
        }

        Player player = (Player) sender;
        ClanMember member = ClanManager.getManager().getMember(player.getName());
        ClanMember target = null;

        if (args.length <= 0) {
            // TODO opens the main menu.;

            if (member == null || !member.hasClan()) {
            } else {
                for (int i = 0; i < 10; i++) {
                    String name = "test-" + i;
                    String tag = "a" + i;

                    ClanMember membera = new ClanMember(UUID.randomUUID(), "player-" + i, tag, ClanRole.LEADER, ZonedDateTime.now(), new GeneralStats(0D, new Random().nextInt(100), new Random().nextInt(100)));

                    Clan a = new Clan(player, membera, name, tag, null);
                    a.setKills(new Random().nextInt(1000));
                    a.setDeaths(new Random().nextInt(1000));

                    for (int b = 0; b < 10; b++) {
                        ClanMember am = new ClanMember(UUID.randomUUID(), randomIdentifier(), tag, ClanRole.MEMBER, ZonedDateTime.now(), new GeneralStats(0D, new Random().nextInt(100), new Random().nextInt(100)));
                        am.cache();
                        a.addMember(am);
                    }
//                    Integer ac = 0;
//                    ac.compareTo()

                    Bukkit.broadcastMessage("Creating: " + tag + "...");
                }
            }

            return true;
        }

        String argument = args[0].toUpperCase();
        boolean executed = false;

        switch (argument) {
            case "AJUDA":
            case "HELP":
                executed = true;

                for (String str : FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Commands.help").getColored()) {
                    player.sendMessage(str);
                }

                break;
            case "CRIAR":
            case "CREATE":
                executed = true;
                // Try to create this clan.
                CommandHandle.create(player, member, args);
                break;
            case "TOP":
                executed = true;
                // Execute top command.
                CommandHandle.top(core, player, args);
                break;
            case "JOGADOR":
            case "PERFIL":
            case "PLAYER":
                executed = true;

                if (args.length >= 2) {
                    String targetName = args[1];

                    if (targetName.equalsIgnoreCase(player.getName())) {
                        // Execute player command.
                        CommandHandle.player(core, player, member);
                        return true;
                    }

                    target = ClanManager.getManager().getMember(targetName);

                    // Execute player command.
                    CommandHandle.player(core, player, target);
                } else {
                    // Execute player command.
                    CommandHandle.player(core, player, member);
                }

                break;
            case "CLAN":
                executed = true;
                break;
            case "RIVALIDADES":
            case "RIVALS":
                executed = true;
                break;
            case "ALIANCAS":
            case "ALIANÇAS":
            case "ALLIES":
                executed = true;
                break;
            case "MEMBROS":
            case "MEMBERS":
                executed = true;
                break;
            case "PROMOVER":
            case "PROMOTE":
                executed = true;
                break;
            case "REBAIXAR":
            case "DEMOTE":
                executed = true;
                break;
            case "LISTA":
            case "LIST":
                executed = true;
                break;
            case "CONVIDAR":
            case "INVITE":
                executed = true;
                break;
            case "EXPULSAR":
            case "KICK":
                executed = true;
                break;
            case "DESFAZER":
            case "EXCLUIR":
            case "DELETE":
                executed = true;
                break;
            case "ALIADO":
            case "ALLY":
                executed = true;
                break;
            case "RIVAL":
                executed = true;
                break;
            case "PVP":
                executed = true;
                break;
        }

        if (!executed) {
            Bukkit.dispatchCommand(sender, "clan help");
        }

        return true;
    }
}