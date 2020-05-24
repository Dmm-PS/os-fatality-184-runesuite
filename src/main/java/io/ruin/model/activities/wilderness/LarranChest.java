package io.ruin.model.activities.wilderness;

import io.ruin.model.entity.player.Player;
import io.ruin.model.item.Item;
import io.ruin.model.map.object.actions.ObjectAction;

/**
 * @author Adam Ali ("Kal-El") https://www.rune-server.ee/members/kal+el/
 */
public class LarranChest {

    private static final int CHEST = 1;

    private static final int LARRAN_KEY_ID = 23490;

    static {
        ObjectAction.register(CHEST, "open", ((player, obj) -> openChest(player)));
    }

    private static void openChest(Player player) {
        Item larrenKey = player.getInventory().findItem(LARRAN_KEY_ID);

        if (larrenKey == null) {
            player.sendFilteredMessage("You need a key to open this chest");
            return;
        }

        player.startEvent(event -> {
            player.lock();
            player.sendFilteredMessage("You unlock the chest with your key");
            larrenKey.remove();
            player.animate(536);
            //TODO: Handle loot.
            event.delay(1);
            player.unlock();
        });
    }
}
