package io.ruin.model.entity.npc.actions.edgeville;

import io.ruin.cache.ObjectID;
import io.ruin.data.impl.teleports;
import io.ruin.model.entity.shared.listeners.SpawnListener;
import io.ruin.model.map.object.actions.ObjectAction;

public class HomePortal {

    static {

        for(int trader : new int[]{6040, 6042}) {
            SpawnListener.register(trader, npc -> npc.startEvent(event -> {
                while (true) {
                    npc.forceText("Come check out the trading post!");
                    event.delay(150);
                }
            }));
        }

        SpawnListener.register(4159, npc -> npc.startEvent(event -> {
            while (true) {
                npc.forceText("Use the portal to teleport around the world!");
                event.delay(100);
            }
        }));

        ObjectAction.register(ObjectID.PORTAL_OF_CHAMPIONS, "teleport", (player, npc) -> teleports.open(player));
        ObjectAction.register(ObjectID.PORTAL_OF_CHAMPIONS, "teleport-previous", (player, npc) -> teleports.previous(player));
    }
}
