package io.ruin.model.activities.wilderness;

import io.ruin.api.utils.Random;
import io.ruin.model.entity.npc.NPCAction;
import io.ruin.model.entity.player.Player;
import io.ruin.model.entity.shared.LockType;
import io.ruin.model.entity.shared.listeners.DeathListener;
import io.ruin.model.entity.shared.listeners.SpawnListener;
import io.ruin.model.inter.dialogue.NPCDialogue;
import io.ruin.model.inter.dialogue.OptionsDialogue;
import io.ruin.model.inter.dialogue.PlayerDialogue;
import io.ruin.model.inter.utils.Option;
import io.ruin.model.map.MapListener;
import io.ruin.model.map.Tile;
import io.ruin.model.map.object.GameObject;
import io.ruin.model.map.object.actions.ObjectAction;
import io.ruin.model.skills.Tool;

public class MageArena {

    private static void pullLever(Player player, GameObject lever, int teleportY, String message, boolean enter) {
        if (player.getCombat().checkTb())
            return;
        player.startEvent(event -> {
            player.lock(LockType.FULL_NULLIFY_DAMAGE);
            player.animate(2710);
            lever.animate(2711);
            player.sendMessage("You pull the lever...");
            event.delay(2);
            player.animate(714);
            player.graphics(111, 110, 0);
            event.delay(3);
            player.resetAnimation();
            player.mageArena = enter;
            player.getMovement().teleport(3105, teleportY, 0);
            player.sendMessage("...and get teleported " + message + " the arena!");
            player.unlock();
        });
    }

    static {
        /**
         * Levers
         */
        ObjectAction.register(9706, 3104, 3956, 0, "pull", (player, obj) -> {
            pullLever(player, obj, 3951, "into", true);
        });
        ObjectAction.register(9707, 3105, 3952, 0, "pull", (player, obj) -> {
            pullLever(player, obj, 3956, "out of", false);
        });

        /**
         * Odd unclipped areas around the circle which make you look
         * like you're floating on lava.
         */
        Tile.get(3093, 3939, 0).flagUnmovable();
        Tile.get(3094, 3941, 0).flagUnmovable();
        Tile.get(3110, 3946, 0).flagUnmovable();
        Tile.get(3098, 3945, 0).flagUnmovable();
        Tile.get(3100, 3946, 0).flagUnmovable();
        Tile.get(3112, 3945, 0).flagUnmovable();
        Tile.get(3116, 3941, 0).flagUnmovable();
        Tile.get(3117, 3939, 0).flagUnmovable();
        Tile.get(3117, 3928, 0).flagUnmovable();
        Tile.get(3116, 3926, 0).flagUnmovable();
        Tile.get(3112, 3922, 0).flagUnmovable();
        Tile.get(3110, 3921, 0).flagUnmovable();
        Tile.get(3100, 3921, 0).flagUnmovable();
        Tile.get(3098, 3922, 0).flagUnmovable();
        Tile.get(3094, 3926, 0).flagUnmovable();
        Tile.get(3093, 3928, 0).flagUnmovable();

       SpawnListener.register("battle mage", npc -> npc.deathEndListener = (DeathListener.SimpleKiller) killer -> {
            if (killer.player.mageArena) {
                int randomPoints = Random.get(3, 6);
                killer.player.mageArenaPoints += randomPoints;
            }
        });

        MapListener.registerRegion(12349)
                .onExit((p, logout) -> {
                    if (!logout)
                        p.mageArena = false;
                });

        NPCAction.register(1603, "talk-to", (player, npc) -> player.dialogue(
                new NPCDialogue(npc, "How can I help you?"),
                new OptionsDialogue(
                        new Option("How do I get mage arena points?", () -> player.dialogue(
                                new PlayerDialogue("How do I get mage arena points?"),
                                new NPCDialogue(npc, "Killing a Battle mage inside the Mage Arena will give you anywhere from 1-3 points. Be careful though, as "),
                                new NPCDialogue(npc, "it's dangerous out there. Also be sure to bring runes, as you can only use magic based attacks inside the arena."),
                                new PlayerDialogue("Okay, thanks.")
                        )),
                        /*
                        new Option("Can I see the point exchange?", () -> player.dialogue(
                                new PlayerDialogue("Can I see the point exchange?"),
                                new ActionDialogue(() -> npc.getDefinition().shop.open(player))
                        )),
                        */
                        new Option("I have to go.", () -> player.dialogue(new PlayerDialogue("I have to go.")))
                )
        ));
        NPCAction.register(1603, "check-points", (player, npc) -> {
            player.dialogue(new NPCDialogue(npc, "You've currently have " + player.mageArenaPoints + " point" + (player.mageArenaPoints == 1 ? "." : "s. Kill Battle Mage's inside the Mage Arena to get more points.")));
        });

        /**
         * Sack containing knife
         */
        ObjectAction.register(14743, 3093, 3956, 0, "search", (player, obj) -> {
            if(player.getInventory().isFull()) {
                player.sendFilteredMessage("Nothing interesting happens.");
                return;
            }

            player.getInventory().add(Tool.KNIFE, 1);
            player.sendFilteredMessage("You search the sack and find a knife.");
        });
    }

}
