package io.ruin.model.entity.npc.actions.edgeville;

import io.ruin.cache.Color;
import io.ruin.model.entity.npc.NPC;
import io.ruin.model.entity.npc.NPCAction;
import io.ruin.model.entity.player.Player;
import io.ruin.model.entity.player.Title;
import io.ruin.model.inter.Interface;
import io.ruin.model.inter.InterfaceType;
import io.ruin.model.inter.dialogue.NPCDialogue;
import io.ruin.model.inter.dialogue.OptionsDialogue;
import io.ruin.model.inter.handlers.OptionScroll;
import io.ruin.model.inter.utils.Config;
import io.ruin.model.inter.utils.Option;

import java.util.ArrayList;
import java.util.List;

public class MakeoverMage {

    private static void open(Player player, NPC npc) {
        if(!player.getEquipment().isEmpty()) {
            player.dialogue(new NPCDialogue(npc, "Please remove what your equipment before we proceed with the makeover."));
            return;
        }
        player.openInterface(InterfaceType.MAIN, Interface.MAKE_OVER_MAGE);
    }

    private static List<Option> getOptions(Player player, NPC npc) {
        List<Option> options = new ArrayList<>();
        options.add(new Option(unlocked(player, 250) + "Midnight Black - 250 kills", () -> unlockSkin(player, npc, 250, 9, "Midnight Black")));
        options.add(new Option(unlocked(player, 500) + "Moonlight White - 500 kills", () -> unlockSkin(player, npc, 500, 10, "Moonlight White")));
        options.add(new Option(unlocked(player, 1000) + "Swamp Green - 1000 kills or 20$ in store", () -> unlockSkin(player, npc, 1000, 8, "Swamp Green")));
        options.add(new Option(unlocked(player, 1500) + "Zombie Blue - 1500 kills or 20$ in store", () -> unlockSkin(player, npc, 1500, 11, "Zombie Blue"))); //todo get the proper color codes for these
        options.add(new Option(unlocked(player, 2000) + "Phasmatys Green - 2000 kills", () -> unlockSkin(player, npc, 2000, 12, "hasmatys Green")));
        options.add(new Option(unlocked(player, 3000) + "Putrid Purple - 3000 kills or 20$ in store", () -> unlockSkin(player, npc, 3000, 13, "Putrid Purple")));
        return options;
    }

    public static void openSkinUnlocks(Player player, NPC npc) {
        OptionScroll.open(player, "Select a skin color you'd like to use", false, getOptions(player, npc));
    }


    private static void unlockSkin(Player player, NPC npc, int killReq, int color, String skinName) {
        if (killReq == 1000 && player.unlockedGreenSkin) {
            player.getAppearance().colors[4] = color;
            player.getAppearance().update();
            player.dialogue(new NPCDialogue(npc, "You are now using the " + skinName + " skin."));
            return;
        } else if (killReq == 1500 && player.unlockedBlueSkin) {
            player.getAppearance().colors[4] = color;
            player.getAppearance().update();
            player.dialogue(new NPCDialogue(npc, "You are now using the " + skinName + " skin."));
            return;
        } else if (killReq == 3000 && player.unlockedPurpleSkin) {
            player.getAppearance().colors[4] = color;
            player.getAppearance().update();
            player.dialogue(new NPCDialogue(npc, "You are now using the " + skinName + " skin."));
            return;
        }

        if(Config.PVP_KILLS.get(player) >= killReq) {
            player.getAppearance().colors[4] = color;
            player.getAppearance().update();
            player.dialogue(new NPCDialogue(npc, "You are now using the " + skinName + " skin."));
        } else {
            player.dialogue(new NPCDialogue(npc, "You need at last " + Color.DARK_RED.wrap(killReq + " wilderness kills") + " to use this skin!"));
        }
    }

    private static String unlocked(Player player, int req) {
        if (req == 1000 && player.unlockedGreenSkin
                || req == 1500 && player.unlockedBlueSkin
                || req == 3000 && player.unlockedPurpleSkin)
            return "";

        if(Config.PVP_KILLS.get(player) < req)
            return "<str>";

        return "";
    }


    static {
        NPCAction.register(1307, "change-looks", MakeoverMage::open);
        NPCAction.register(1307, "skin-unlocks", MakeoverMage::openSkinUnlocks);
        NPCAction.register(1307, "title-unlocks", MakeoverMage::titles);
    }

    private static void titles(Player player, NPC npc) {
        player.dialogue(new OptionsDialogue(
                new Option("View Unlocked Titles", () -> Title.openSelection(player, false)),
                new Option("View All Titles", () -> Title.openSelection(player, true)),
                new Option("Remove my Title", () -> Title.clearTitle(player)),
                new Option("Cancel", () -> {})));
    }
}
