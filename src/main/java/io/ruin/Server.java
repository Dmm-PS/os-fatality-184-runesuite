package io.ruin;

import io.ruin.api.database.Database;
import io.ruin.api.database.DatabaseUtils;
import io.ruin.api.filestore.FileStore;
import io.ruin.api.netty.NettyServer;
import io.ruin.api.process.ProcessWorker;
import io.ruin.api.protocol.world.WorldFlag;
import io.ruin.api.protocol.world.WorldSetting;
import io.ruin.api.protocol.world.WorldStage;
import io.ruin.api.protocol.world.WorldType;
import io.ruin.api.utils.*;
import io.ruin.cache.*;
import io.ruin.data.DataFile;
import io.ruin.data.impl.login_set;
import io.ruin.model.World;
import io.ruin.model.combat.special.Special;
import io.ruin.model.entity.player.Player;
import io.ruin.model.map.object.actions.impl.Trapdoor;
import io.ruin.model.map.object.actions.impl.dungeons.StrongholdSecurity;
import io.ruin.model.map.object.actions.impl.edgeville.Giveaway;
import io.ruin.network.LoginDecoder;
import io.ruin.network.central.CentralClient;
import io.ruin.network.incoming.Incoming;
import io.ruin.process.CoreWorker;
import io.ruin.process.LoginWorker;
import io.ruin.process.event.EventWorker;
import io.ruin.services.LatestUpdate;
import io.ruin.services.Loggers;
import io.ruin.services.discord.DiscordConnection;
import io.ruin.services.livedata.LiveData;
import io.ruin.utility.OfflineMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Server extends ServerWrapper {

    public static final ProcessWorker worker = newWorker("server-worker", 600L, Thread.NORM_PRIORITY + 1);

    public static Database gameDb;

    public static final Database dumpsDb = new Database("", "", "", "");

    public static Database forumDb;

    public static List<Runnable> afterData = new ArrayList<>();

    public static FileStore fileStore;

    public static File dataFolder;

    public static boolean dataOnlyMode = false;

    static {
        init(Server.class);
    }

    public static void main(String[] args) throws Throwable {
        if (args.length > 1 && args[0].equalsIgnoreCase("build-data")) {
            World.type = WorldType.ECO;
            dataOnlyMode = true;
            dataFolder = FileUtils.get(args[1]);
            DataFile.loadUnpacked();
            System.out.println("Done.");
            System.exit(0);
        }

        /*
         * Server properties
         */
        println("Loading server settings...");
        Properties properties = new Properties();
        try (InputStream in = new FileInputStream(getPath() + File.separator + "server.properties")) {
            properties.load(in);
        } catch (IOException e) {
            logError("Failed to load server settings!", e);
            throw e;
        }
        /*
         * World information
         */
        World.id = Integer.valueOf(properties.getProperty("world_id"));
        World.name = properties.getProperty("world_name");
        World.stage = WorldStage.valueOf(properties.getProperty("world_stage"));
        World.type = WorldType.valueOf(properties.getProperty("world_type"));
        World.flag = WorldFlag.valueOf(properties.getProperty("world_flag"));
        World.halloween = Boolean.valueOf(properties.getProperty("halloween"));
        World.christmas = Boolean.valueOf(properties.getProperty("christmas"));
        String worldSettings = properties.getProperty("world_settings");
        for (String s : worldSettings.split(",")) {
            if (s == null || (s = s.trim()).isEmpty())
                continue;
            WorldSetting setting;
            try {
                setting = WorldSetting.valueOf(s);
            } catch (Exception e) {
                logError("INVALID WORLD SETTING: " + s, e);
                continue;
            }
            World.settings |= setting.mask;
        }
        String address = properties.getProperty("world_address");
        String[] split = address.split(":");
        String host = split[0].trim();
        int port = Integer.valueOf(split[1]);
        if (host.isEmpty() || host.equals("127.0.0.1") || host.equals("localhost"))
            host = IPAddress.get();
        World.address = host + ":" + port;

        /*
         * Offline mode
         */
        if (OfflineMode.enabled = Boolean.valueOf(properties.getProperty("offline_mode"))) {
            OfflineMode.setPath();
            println("WARNING: Offline mode enabled!");
        }

        /*
         * Loading (Data from cache & databases)
         */
        println("Loading server data...");
        try {
            fileStore = new FileStore(properties.getProperty("cache_path"));
            dataFolder = FileUtils.get(properties.getProperty("data_path"));
            Varpbit.load();
            IdentityKit.load();
            AnimDef.load();
            GfxDef.load();
            ScriptDef.load();
            InterfaceDef.load();
            ItemDef.load();
            NPCDef.load();
            ObjectDef.load();
            DataFile.load();

            /*
             * The following must come after DataFile.load
             */
            login_set.setActive(null, properties.getProperty("login_set"));
        } catch (Throwable t) {
            logError("", t);
            return;
        }

        /*
         * Database connections
         */
        if (!OfflineMode.enabled) {
            println("Connecting to SQL databases...");
            gameDb = new Database(properties.getProperty("database_host"), "game", properties.getProperty("database_user"), properties.getProperty("database_password"));
            forumDb = new Database(properties.getProperty("database_host"), "website", properties.getProperty("database_user"), properties.getProperty("database_password"));

            DatabaseUtils.connect(new Database[]{gameDb, forumDb}, errors -> {
                if (!errors.isEmpty()) {
                    for (Throwable t : errors)
                        logError("Database error", t);
                    System.exit(1);
                }
            });

            Loggers.clearOnlinePlayers(World.id);
            LatestUpdate.fetch();
            Giveaway.updateTotalAmount();
            DiscordConnection.setup("NjQwMjEwMzE4ODk4ODg4NzA0.Xb2i0w.1eCYYbA28oomoq9ZqF4u_fhSW7g");
        }

        /*
         * Loading (After data has been loaded!
         */
        for (Runnable r : afterData) {
            try {
                r.run();
            } catch (Throwable t) {
                logError("", t);
                return;
            }
        }
        afterData.clear();
        afterData = null;

        /*
         * Loading (Scripts & handlers)
         */
        println("Loading server scripts & handlers...");
        try {
            Special.load();
            Incoming.load();

            // When packaged, priority messes up and these load too late.
            StrongholdSecurity.register();
            Trapdoor.register();

            PackageLoader.load("io.ruin"); //ensures all static blocks load

            // When packaged, priority messes up and these load too late.
            StrongholdSecurity.register();
            Trapdoor.register();
        } catch (Throwable t) {
            logError("Error loading handlers", t);
            return;
        }

        /*
         * Processing
         */
        println("Starting server workers...");
        worker.queue(() -> {
            CoreWorker.process();
            EventWorker.process();
            return false;
        });
        LoginWorker.start();
        LiveData.start();
        /*
         * Network
         */
        NettyServer nettyServer = NettyServer.start(World.type.getWorldName() + " World (" + World.id + ") Server", port, LoginDecoder.class, 5);

        /*
         * Central server
         */
        if (!OfflineMode.enabled)
            CentralClient.start();

        /*
         * Shutdown hook
         */
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println();
            System.out.println("Gracefully shutting down world server...");
            /*
             * Shutdown network
             */
            nettyServer.shutdown();

            /*
             * Remove players
             */
            int fails = 0;
            while (true) {
                try {
                    for(Player p : World.players) {
                        if(p.getChannel().id() == null)
                            p.logoutStage = -1;
                    }
                    if (Server.worker.getExecutor().submit(World::removePlayers).get())
                        break;

                    ThreadUtils.sleep(10L); //^ that will already be a big enough delay
                } catch (Throwable t) {
                    logError("ERROR: Removing online players", t);
                    if (++fails >= 5 && World.removePlayers())
                        break;
                    ThreadUtils.sleep(1000L);
                }
            }
        }));
    }

    /**
     * Timing
     */
    public static long currentTick() {
        return worker.getExecutions();
    }

    public static long getEnd(long ticks) {
        return currentTick() + ticks;
    }

    public static boolean isPast(long end) {
        return currentTick() >= end;
    }

    public static int tickMs() {
        return (int) Server.worker.getPeriod();
    }

    public static int toTicks(int seconds) {
        return (seconds * 1000) / tickMs();
    }

}