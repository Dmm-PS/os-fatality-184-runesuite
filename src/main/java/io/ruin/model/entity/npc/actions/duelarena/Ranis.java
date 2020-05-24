package io.ruin.model.entity.npc.actions.duelarena;

import io.ruin.model.entity.shared.listeners.SpawnListener;

public class Ranis {

    private static final int RANIS = 3745;
    static {
        SpawnListener.register(RANIS, npc -> npc.startEvent(event -> {
            while (true) {
                npc.forceText("Stay clear of scams or tricks, always check the final dual screen window before accepting");
                event.delay(10);
            }
        }));
    }

}
