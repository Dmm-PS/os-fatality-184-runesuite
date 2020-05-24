package io.ruin.model;

import io.ruin.Server;
import io.ruin.api.protocol.world.WorldFlag;
import io.ruin.api.protocol.world.WorldStage;
import io.ruin.api.protocol.world.WorldType;
import io.ruin.cache.Color;
import io.ruin.cache.Icon;
import io.ruin.model.activities.pvminstances.PVMInstance;
import io.ruin.model.combat.Killer;
import io.ruin.model.entity.EntityList;
import io.ruin.model.entity.npc.NPC;
import io.ruin.model.entity.player.Player;
import io.ruin.model.entity.player.PlayerFile;
import io.ruin.model.map.Position;
import io.ruin.model.map.Region;
import io.ruin.process.event.EventWorker;
import io.ruin.utility.Broadcast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class World extends EventWorker {

    public static int id;

    public static String name;

    public static WorldStage stage;

    public static WorldType type;

    public static WorldFlag flag;

    public static int settings;

    public static String address;

    public static boolean isDev() {
        return stage == WorldStage.DEV;
    }

    public static boolean isBeta() {
        return stage == WorldStage.BETA;
    }

    public static boolean isLive() {
        return stage == WorldStage.LIVE;
    }

    public static boolean isPVP() {
        return type == WorldType.PVP;
    }

    public static boolean isEco() {
        return type == WorldType.ECO;
    }

    /**
     * Players
     */
    public static final EntityList<Player> players = new EntityList<>(new Player[1000]);

    public static Stream<Player> getPlayerStream() {
        return StreamSupport.stream(players.spliterator(), false)
                .filter(Objects::nonNull);
    }

    public static Player getPlayer(int index) {
        return players.get(index);
    }

    public static Player getPlayer(String name) {
        for(Player player : players) {
            if(player.getName().equalsIgnoreCase(name))
                return player;
        }
        return null;
    }

    public static Player getPlayer(int userId, boolean onlineReq) {
        if(onlineReq) {
            for(Player player : players) {
                if(player != null && player.getUserId() == userId)
                    return player;
            }
        } else {
            for(Player player : players.entityList) {
                if(player != null && player.getUserId() == userId)
                    return player;
            }
        }
        return null;
    }

    public static void sendSupplyChestBroadcast(String message) {
        players.forEach(p -> {
            if (p.broadcastSupplyChest)
                p.sendNotification(message);
        });
    }

    public static void sendGraphics(int id, int height, int delay, Position dest) {
        sendGraphics(id, height, delay, dest.getX(), dest.getY(), dest.getZ());
    }

    public static void sendGraphics(int id, int height, int delay, int x, int y, int z) {
        for(Player p : Region.get(x, y).players)
            p.getPacketSender().sendGraphics(id, height, delay, x, y, z);
    }

    /**
     * Npcs
     */
    public static final EntityList<NPC> npcs = new EntityList<>(new NPC[1000]);

    public static NPC getNpc(int index) {
        return npcs.get(index);
    }

    /**
     * PLAYER SAVERS
     */
    public static boolean doubleDrops;

    public static boolean doublePkp;

    public static boolean doubleSlayer;

    public static boolean doublePest;

    public static int xpMultiplier = 0;

    public static int bmMultiplier = 0;

    public static boolean weekendExpBoost = false;

    public static void toggleDoubleDrops() {
        doubleDrops = !doubleDrops;
        Broadcast.WORLD.sendNews(Icon.RED_INFO_BADGE, "Double drops have been " + (doubleDrops ? "enabled" : "disabled") + ".");
    }

    public static void toggleDoublePkp() {
        doublePkp = !doublePkp;
        Broadcast.WORLD.sendNews(Icon.RED_INFO_BADGE, "Double Pkp has been " + (doublePkp ? "enabled" : "disabled") + ".");
    }

    public static void toggleDoubleSlayer() {
        doubleSlayer = !doubleSlayer;
        Broadcast.WORLD.sendNews(Icon.RED_INFO_BADGE, "Double Slayer Points has been " + (doubleSlayer ? "enabled" : "disabled") + ".");
    }

    public static void toggleDoublePest() {
        doublePest = !doublePest;
        Broadcast.WORLD.sendNews(Icon.RED_INFO_BADGE, "Double Pest Control Points has been " + (doublePest ? "enabled" : "disabled") + ".");
    }

    public static void boostXp(int multiplier) {
        xpMultiplier = multiplier;
        if(xpMultiplier == 1)
            Broadcast.WORLD.sendNews(Icon.RED_INFO_BADGE, "Experience is now normal. (x1)");
        else if(xpMultiplier == 2)
            Broadcast.WORLD.sendNews(Icon.RED_INFO_BADGE, "Experience is now being doubled! (x2)");
        else if(xpMultiplier == 3)
            Broadcast.WORLD.sendNews(Icon.RED_INFO_BADGE, "Experience is now being tripled! (x3)");
        else if(xpMultiplier == 4)
            Broadcast.WORLD.sendNews(Icon.RED_INFO_BADGE, "Experience is now being quadrupled! (x4)");
        else
            Broadcast.WORLD.sendNews(Icon.RED_INFO_BADGE, "Experience is now boosted! (x" + multiplier + ")");
    }

    /*
     * Sets the base amount of blood money user can get per kill
     */
    public static void setBaseBloodMoney(int baseBloodMoney) {
        Killer.BASE_BM_REWARD = baseBloodMoney;
    }

    public static void toggleWeekendExpBoost() {
        weekendExpBoost = !weekendExpBoost;
        if(weekendExpBoost) {
            Broadcast.WORLD.sendNews(Icon.RED_INFO_BADGE, "The 25% weekend experience boost is now activated!");
        } else {
            Broadcast.WORLD.sendNews(Icon.RED_INFO_BADGE, "The 25% weekend experience boost is now deactivated!");
        }
    }

    public static void boostBM(int multiplier) {
        bmMultiplier = multiplier;
        if(bmMultiplier == 1)
            Broadcast.WORLD.sendNews(Icon.RED_INFO_BADGE, "Blood money drops from player kills are now normal. (x1)");
        else if(bmMultiplier == 2)
            Broadcast.WORLD.sendNews(Icon.RED_INFO_BADGE, "Blood money drops from player kills are now being doubled! (x2)");
        else if(bmMultiplier == 3)
            Broadcast.WORLD.sendNews(Icon.RED_INFO_BADGE, "Blood money drops from player kills are now being tripled! (x3)");
        else if(bmMultiplier == 4)
            Broadcast.WORLD.sendNews(Icon.RED_INFO_BADGE, "Blood money drops from player kills are now being quadrupled! (x4)");
        else
            Broadcast.WORLD.sendNews(Icon.RED_INFO_BADGE, "Blood money drops from player kills are now boosted! (x" + multiplier + ")");
    }

    public static void sendLoginMessages(Player player) {
        if(doubleDrops)
            player.sendMessage(Color.ORANGE_RED.tag() + "Npc drops are currently being doubled!");
        if(xpMultiplier == 2)
            player.sendMessage(Color.ORANGE_RED.tag() + "Experience is currently being doubled! (x2)");
        else if(xpMultiplier == 3)
            player.sendMessage(Color.ORANGE_RED.tag() + "Experience is currently being tripled! (x3)");
        else if(xpMultiplier == 4)
            player.sendMessage(Color.ORANGE_RED.tag() + "Experience is currently being quadrupled! (x4)");
    }

    public static boolean wildernessDeadmanKeyEvent = false;

    public static void toggleDmmKeyEvent() {
        wildernessDeadmanKeyEvent = !wildernessDeadmanKeyEvent;
    }

    public static boolean wildernessKeyEvent = false;
    public static void toggleWildernessKeyEvent() {
        wildernessKeyEvent = !wildernessKeyEvent;
    }

    /**
     * Updating
     */
    public static boolean updating = false;

    public static boolean update(int minutes) {
        if(minutes <= 0) {
            updating = false;
            for(Player player : players)
                player.getPacketSender().sendSystemUpdate(0);
            System.out.println("System Update Cancelled");
            return true;
        }
        if(updating)
            return false;
        updating = true;
        System.out.println("System Update: " + minutes + " minutes");
        for(Player player : players)
            player.getPacketSender().sendSystemUpdate(minutes * 60);
        startEvent(e -> {
            int ticks = minutes * 100;
            while(updating) {
                if(--ticks <= 0 && removeBots() && removePlayers())
                    return;
                e.delay(1);
            }
        });
        return true;
    }

    public static boolean removePlayers() {
        int pCount = players.count();
        if(pCount > 0) {
            System.out.println("Attempting to remove " + pCount + " players...");
            for(Player player : players)
                player.forceLogout();
            return false;
        }
        PVMInstance.destroyAll();
        System.out.println("Players removed from world successfully!");
        return true;
    }

    private static boolean removeBots() {
        for(Player p : World.players) {
            if(p.getChannel().id() == null)
                p.logoutStage = -1;
        }
        return true;
    }

    /**
     * Holiday themes
     */
    public static boolean halloween;

    public static boolean isHalloween() {
        return halloween;
    }

    public static boolean christmas;

    public static boolean isChristmas() {
        return christmas;
    }

    /*
     * Save event
     */
    static {
        startEvent(e -> {
            while(true) {
                e.delay(100); //every 1 minute just in case..
                for(Player player : players)
                    PlayerFile.save(player, -1);
            }
        });
    }

    /*
     * Announcement event
     */
    static {
        Server.afterData.add(() -> {
            List<String> announcements;
            announcements = Arrays.asList(
                    "Need help? Join our \"help\" cc!",
                    "You can sell your items to sigmund for gold @ home!",
                    "Make sure to vote to gain access to exclusive items!",
                    "Looking to support OSFatality? Type ::store ingame!",
                    "Take the time to protect your account and set a bank pin and 2FA!",
                    "Join our Discord/Forums to be updated on upcoming events ::discord/::forums",
                    "The BH npc will exchange your blood money for coins at home!",
                    "Normal and Iron modes can still pk but at your own risk!",
                    "You can find event timers in your information tab right of friends tab!",
                    "Please take the time to vote for us. It helps us out and takes two seconds! ::vote",
                    "Join ::discord to get closer to the community!",
                    "Ring of wealth give you a higher chance of receiving caskets!",
                    "Want to donate OSRS GP? PM Any Owner on ::Discord!"
            );

            Collections.shuffle(announcements);
            startEvent(e -> {
                int offset = 0;
                while(true) {
                    e.delay(500); //5 minutes
                    Broadcast.WORLD.sendNews(Icon.ANNOUNCEMENT, "Announcements", announcements.get(offset));
                    if(++offset >= announcements.size())
                        offset = 0;
                }
            });
        });
    }
}