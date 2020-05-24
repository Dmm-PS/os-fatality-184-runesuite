package io.ruin.services;

import io.ruin.Server;
import io.ruin.api.database.DatabaseStatement;
import io.ruin.api.database.DatabaseUtils;
import io.ruin.api.utils.StringUtils;
import io.ruin.api.utils.TimeUtils;
import io.ruin.model.World;
import io.ruin.model.activities.jail.Jail;
import io.ruin.model.entity.npc.NPC;
import io.ruin.model.entity.player.Player;
import io.ruin.model.entity.player.PlayerGroup;
import io.ruin.services.discord.DiscordConnection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Punishment {

    /**
     * Kicking (Admins only to prevent inevitable problems)
     */

    public static void kick(Player p1, Player p2) {
        p1.sendMessage("Kicking " + p2.getName() + "...");
        World.startEvent(e -> {
            while (p2.isLocked())
                e.delay(1); //wait
            if (!p2.isOnline())
                return;
            p2.lock();
            p2.resetActions(true, true, true);
            p2.forceLogout();
        });
    }

    /**
     * Jailing - tbh i want to come back and fix this jail system up.. was kinda rushed..
     * something about coding punishment cmds makes me depressed i swear idk why l0l
     */

    public static void jail(Player p1, Player p2, int ores) {
        if (p2.tournament != null) {
            p1.sendMessage("You can't jail a player while they are within a tournament.");
            return;
        }

        if (p2.lmsSession != null) {
            p2.lmsSession.eliminatePlayer(p2);
        }

        p1.sendMessage("Jailing " + p2.getName() + " to " + ores + " ores...");
        p2.jailerName = p1.getName();
        p2.jailOresAssigned = ores;
        p2.jailOresCollected = 0;
        World.startEvent(e -> {
            while (p2.isLocked())
                e.delay(1); //wait
            if (!p2.isOnline())
                return;
            p2.resetActions(true, true, true);
            Jail.startEvent(p2);
        });
        logPunishment(p1, p2, -2, "jailed", new MessageEmbed.Field("Ores", String.valueOf(ores), true));
    }

    public static void jail(Player player, NPC npc, int ores) {
        if (player.lmsSession != null) {
            player.lmsSession.eliminatePlayer(player);
        }
        player.jailerName = npc.getDef().name;
        player.jailOresAssigned = ores;
        player.jailOresCollected = 0;
        World.startEvent(e -> {
            if (!player.isOnline())
                return;
            player.resetActions(true, true, true);
            Jail.startEvent(player);
        });
    }


    public static void unjail(Player p1, Player p2) {
        p2.jailOresAssigned = 0; //this will automatically finish their sentence lol
        p1.sendMessage("You unjailed " + p2.getName() + ".");
        logPunishment(p1, p2, -2, "unjailed");
    }

    /**
     * Muting
     */

    public static void mute(Player p1, Player p2, long time, boolean shadow) {
        p2.muteEnd = time;
        p2.shadowMute = shadow;
        p1.sendMessage(p2.getName() + " is now muted.");

        logPunishment(p1, p2, time, shadow ? "shadowmuted" : "muted");
    }

    public static void unmute(Player p1, Player p2) {
        if (p2.muteEnd == 0) {
            p1.sendMessage(p2.getName() + " isn't muted.");
            return;
        }
        if (p2.shadowMute && !p1.isAdmin()) {
            p1.sendMessage("This player can only be unmuted by an admin.");
            return;
        }
        p2.muteEnd = 0;
        if (p2.shadowMute)
            p2.shadowMute = false;
        else
            p2.sendMessage("Your mute has been lifted.");
        p1.sendMessage(p2.getName() + " is now unmuted.");
        logPunishment(p1, p2, -2, "unmuted");
    }

    public static boolean isMuted(Player player) {
        return player.muteEnd == -1 || (player.muteEnd > 0 && System.currentTimeMillis() < player.muteEnd);
    }

    /**
     * Banning
     */

    public static void ban(Player p1, Player p2) {
        p1.sendMessage("Attemping to ban " + p2.getName() + "...");
        PlayerGroup.BANNED.sync(p2, "ban", () -> {
            Server.worker.execute(() -> {
                p2.forceLogout();
                p1.sendMessage("Successfully banned " + p2.getName() + "!");

                logPunishment(p1, p2, -1, "banned");
            });
        });
    }

    public static void macAddressBan(Player banned, Player by) {
        by.sendMessage("Sending ban request to server...");
        if (banned != null) {
            banned.attemptLogout();
        }

    }

    public static void ipBan(Player staffMember, Player punishedUser) {
        Server.gameDb.execute(new DatabaseStatement() {
            @Override
            public void execute(Connection connection) throws SQLException {
                PreparedStatement statement = null;
                ResultSet resultSet = null;
                try {
                    statement = connection.prepareStatement("SELECT * FROM ip_banned_users  WHERE banned_ip = ? LIMIT 1", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    statement.setString(1, punishedUser.getIp());
                    resultSet = statement.executeQuery();
                    if (!resultSet.next()) {
                        resultSet.moveToInsertRow();
                        ban(staffMember, punishedUser);
                        resultSet.updateInt("userid", punishedUser.getUserId());
                        resultSet.updateString("username", punishedUser.getName());
                        resultSet.updateString("banned_ip", punishedUser.getIp());
                        resultSet.insertRow();
                        finish(staffMember, punishedUser, true);
                    } else {
                        finish(staffMember, punishedUser, false);
                    }
                } finally {
                    DatabaseUtils.close(statement, resultSet);
                }
            }

            @Override
            public void failed(Throwable t) {
                System.out.println("FAILED");
                /* do nothing */
            }

            private void finish(Player staffMember, Player punishedUser, boolean success) {
                Server.worker.execute(() -> {
                    if(success) {
                        staffMember.sendMessage(punishedUser.getName() + " has been IP banned.");
                    } else {
                        staffMember.sendMessage(punishedUser.getName() + " is already IP banned.");
                    }
                    staffMember.unlock();

                    logPunishment(staffMember, punishedUser, -1, "IP banned");
                });
            }
        });
    }

    private static void logPunishment(Player staff, Player victim, long time, String type, MessageEmbed.Field... fields) {
        String until;

        if (time == -1 || time == -2) {
            until = "Never (perm).";
        } else {
            until = TimeUtils.dateFormat.format(new Date(time))
                    + " (in " + TimeUtils.fromMs(time - System.currentTimeMillis(), false) + ")";
        }

        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle(String.format("%s has been %s by %s", victim.getName(), type, staff.getName()));
        builder.addField("Name", victim.getName(), true);
        builder.addField("Staff member", staff.getName(), true);
        builder.addField("Punishment", StringUtils.capitalizeFirst(type), true);

        if (time != -2)
            builder.addField("Expires", until, true);

        if (fields != null && fields.length > 0) {
            for (MessageEmbed.Field field : fields) {
                builder.addField(field);
            }
        }

        DiscordConnection.post(DiscordConnection.CHANNEL_PUNISHMENTS, builder.build());
    }

}