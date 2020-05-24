package io.ruin.model.item.actions.impl.boxes;

import io.ruin.cache.ItemDef;
import io.ruin.model.entity.player.Player;
import io.ruin.model.item.Item;
import io.ruin.model.item.actions.ItemAction;
import io.ruin.model.item.loot.LootItem;
import io.ruin.model.item.loot.LootTable;

public class HerbBox {

    private static final LootTable table = new LootTable().addTable(1,
            new LootItem(249, 1, 25),   //Guam leaf
            new LootItem(251, 1, 18),   //Marrentill
            new LootItem(253, 1, 14),   //Tarromin
            new LootItem(255, 1, 12),   //Harralander
            new LootItem(257, 1, 9),    //Ranarr weed
            new LootItem(259, 1, 6),    //Irit leaf
            new LootItem(261, 1, 5),    //Avantoe
            new LootItem(263, 1, 5),    //Kwuarm
            new LootItem(265, 1, 3),    //Cadantine
            new LootItem(2481, 1, 2),   //Lantadyme
            new LootItem(267, 1, 1)     //Dwarf weed
    );

    private static void open(Player player, Item herbBox) {
        int currentCharges = herbBox.getUniqueValue();
        int amtToAdd = currentCharges == -1 || currentCharges == 0 ? 10 : currentCharges;
        for (int i = 0; i < amtToAdd; i++)
            player.getBank().add(table.rollItem().getId(), 1);
        herbBox.remove();
        player.sendMessage(amtToAdd + " herb" + (amtToAdd == 1 ? " has" : "s have") + " been deposited into your bank.");
    }

    private static void check(Player player, Item herbBox) {
        int charges = herbBox.getUniqueValue();
        if(charges == 0)
            herbBox.setUniqueValue(10);
        charges = herbBox.getUniqueValue();
        player.sendMessage("Your box has " + charges + " herb" + (charges == 1 ? "" : "s") + " left.");
    }

    private static final int HERB_BOX = 11738;

    static {
        ItemAction.registerInventory(HERB_BOX, "take-one", (player, item) -> {
            if(player.getInventory().isFull()) {
                player.sendMessage("You need at least one inventory space to take a herb from your box.");
                return;
            }
            int currentCharges = item.getUniqueValue();
            int herb = table.rollItem().getId();
            if(currentCharges == 0) {
                item.setUniqueValue(9);
                player.getInventory().add(herb, 1);
            } else {
                if(currentCharges == 1)
                    item.remove();
                else
                    item.modifyUniqueValue(-1);
                player.getInventory().add(herb, 1);
            }
            player.sendMessage("You open the herb box and find " + ItemDef.get(herb).descriptiveName + ".");
        });
        ItemAction.registerInventory(HERB_BOX, "bank-all", HerbBox::open);
        ItemAction.registerInventory(HERB_BOX, "check", HerbBox::check);
    }
}
