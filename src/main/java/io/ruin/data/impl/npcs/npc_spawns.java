package io.ruin.data.impl.npcs;

import com.google.gson.annotations.Expose;
import io.ruin.Server;
import io.ruin.api.protocol.world.WorldType;
import io.ruin.api.utils.JsonUtils;
import io.ruin.data.DataFile;
import io.ruin.model.World;
import io.ruin.model.entity.npc.NPC;
import io.ruin.model.map.Direction;
import io.ruin.model.map.Tile;

import java.util.List;

public class npc_spawns implements DataFile {

    @Override
    public String path() {
        return "npcs/spawns/*.json";
    }

    @Override
    public Object fromJson(String fileName, String json) {
        List<Spawn> spawns = JsonUtils.fromJson(json, List.class, Spawn.class);

        if (!Server.dataOnlyMode) {
            spawns.forEach(spawn -> {
                if (spawn.world != null && spawn.world != World.type) return;
                if (spawn.walkRange == 0) Tile.get(spawn.x, spawn.y, spawn.z, true).flagUnmovable();
                NPC n = new NPC(spawn.id).spawn(spawn.x, spawn.y, spawn.z, Direction.get(spawn.direction), spawn.walkRange);
                n.defaultSpawn = true;
            });
        }

        return spawns;
    }

    private static final class Spawn {
        @Expose public int id;
        @Expose public int x, y, z;
        @Expose public String direction = "S";
        @Expose public int walkRange;
        @Expose public WorldType world;
    }

}