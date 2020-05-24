package io.ruin.model.entity.npc.actions.edgeville;

import io.ruin.cache.ItemDef;
import io.ruin.cache.NPCDef;
import io.ruin.model.entity.npc.NPCAction;
import io.ruin.model.entity.player.Player;
import io.ruin.model.entity.shared.listeners.SpawnListener;
import io.ruin.model.item.Item;
import io.ruin.model.item.actions.impl.ItemSet;
import io.ruin.model.item.actions.impl.jewellery.BraceletOfEthereum;
import io.ruin.model.item.containers.shop.ShopCurrency;
import io.ruin.model.item.containers.shop.ShopItem;


public class SigmundTheMerchant {
    private static int SIGMUND_THE_MERCHANT = 3894;

    public static int getPrice(Player player, Item item) {
        ItemDef def = item.getDef();
        if (item.getId() == BraceletOfEthereum.REVENANT_ETHER)
            return 0;
        if(def.isNote())
            def = def.fromNote();
        if(def.sigmundBuyPrice != 0)
            return def.sigmundBuyPrice;
        return player.getGameMode().isIronMan() ? def.lowAlchValue : def.highAlchValue;
    }

    static {
        //Tournament reach hoe
        SpawnListener.register(7317, npc -> npc.skipReachCheck = p -> p.equals(3407, 3180));

        SpawnListener.register(SIGMUND_THE_MERCHANT, npc -> npc.skipReachCheck = p -> p.equals(3078, 3510));
        NPCAction.register(SIGMUND_THE_MERCHANT, "sell-items", (player, npc) -> {
            player.getTrade().tradeSigmund();
        });
        NPCAction.register(SIGMUND_THE_MERCHANT, "buy-items", (player, npc) -> {
            if (player.getGameMode().isIronMan() ) {
                player.sendMessage("You're an ironman. You stand alone!");
                return;
            }
            npc.getDef().shop.open(player);
        });
        NPCAction.register(SIGMUND_THE_MERCHANT, "sets", (player, npc) -> ItemSet.open(player));

        /*
         * Safe checks
         */
        NPCDef.forEach(npcDef -> {
            if(npcDef.shop != null && npcDef.shop.currency == ShopCurrency.COINS) {
                for(ShopItem item : npcDef.shop.items) {
                    ItemDef def = item.getDef();
                    if(item.price < def.value)
                        item.price = def.value;
                    if(item.price < def.sigmundBuyPrice)
                        item.price = def.sigmundBuyPrice;
                }
            }
        });
    }
}
