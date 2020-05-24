package io.ruin.services;

import io.ruin.Server;
import io.ruin.api.utils.IPAddress;
import io.ruin.model.World;
import io.ruin.model.activities.pvp.PVPInstance;
import io.ruin.model.activities.wilderness.Wilderness;
import io.ruin.utility.OfflineMode;

import java.io.IOException;
import java.sql.PreparedStatement;

public class PlayersOnline {

    static {
        if (!OfflineMode.enabled) {
            String[] split = World.address.split(":");
            try {
                split[0] = IPAddress.get();
            } catch (IOException e) {
                /* ignored */
            }
            String ip = split[0];
            int port = Integer.valueOf(split[1]);
            World.startTask(t -> {
                while (true) {
                    t.sleep(10000L); //10 seconds
                    Server.gameDb.execute(con -> {
                        try (PreparedStatement statement = con.prepareStatement("UPDATE players_online SET online = ?, wild = ?, pvp_instance = ?, server_ip = ?, port_id = ? WHERE id = ?")) {
                            statement.setInt(1, (int) (World.players.count() * 1.6));
                            statement.setInt(2, Wilderness.players.size());
                            statement.setInt(3, PVPInstance.players.size());
                            statement.setString(4, ip);
                            statement.setInt(5, port);
                            statement.setInt(6, World.id);
                            statement.executeUpdate();
                        }
                    });
                }
            });
        }

        if (!OfflineMode.enabled) {
            World.startTask(t -> {
                while (true) {
                    t.sleep(1800000); //30 minutes
                    Server.gameDb.execute(con -> {
                        try (PreparedStatement statement = con.prepareStatement("INSERT INTO online_statistics (world, players) VALUES (?, ?)")) {
                            statement.setInt(1, World.id);
                            statement.setInt(2, (int) (World.players.count() * 1.6));
                            statement.executeUpdate();
                        }
                    });
                }
            });
        }
    }

}