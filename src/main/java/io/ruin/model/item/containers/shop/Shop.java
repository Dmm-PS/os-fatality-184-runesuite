package io.ruin.model.item.containers.shop;

import io.ruin.api.utils.NumberUtils;
import io.ruin.model.World;
import io.ruin.model.entity.npc.NPC;
import io.ruin.model.entity.player.Player;
import io.ruin.model.inter.Interface;
import io.ruin.model.inter.InterfaceHandler;
import io.ruin.model.inter.InterfaceType;
import io.ruin.model.inter.actions.DefaultAction;
import io.ruin.model.inter.dialogue.MessageDialogue;

import java.util.List;

public class Shop {

    public final String name;

    public final ShopCurrency currency;

    public final ShopItem[] items;

    public boolean ironman;

    public Shop(String name, ShopCurrency currency, boolean ironman, List<ShopItem> items) {
        this(name, currency, ironman, items.toArray(new ShopItem[items.size()]));
    }

    public Shop(String name, ShopCurrency currency, boolean ironman, ShopItem... items) {
        this.name = name;
        this.currency = currency;
        this.items = items;
        this.ironman = ironman;
    }

    public void open(Player player) {
        if(player.isVisibleInterface(Interface.SHOP))
            player.closeInterface(InterfaceType.MAIN);
        player.getPacketSender().sendShop(name, items);
        player.openInterface(InterfaceType.MAIN, Interface.SHOP);
        player.getPacketSender().setHidden(Interface.SHOP, 2, true);
        player.shopActive = this;
    }

    private void handle(Player player, int option, int slot, int itemId) {
        if(slot < 0 || slot >= items.length)
            return;
        ShopItem item = items[slot];
        if(item == null || (itemId != item.getId() && itemId != item.getDef().placeholderMainId))
            return;
        if(option == 10) {
            item.examine(player);
            return;
        }
        if(item.achievement != null && !item.achievement.isFinished(player)) {
            player.sendMessage("You need to complete the <col=FF0000>" + item.achievement.getListener().name() + "</col> achievement to unlock this item.");
            return;
        }
        if(option == 1)
            player.sendMessage(item.getDef().name + " can be purchased for " + (item.price == 0 ? "free" : (NumberUtils.formatNumber(item.price) + " " + currency.name )) +  ".");
        else if(option == 2)
            item.buy(player, currency, 1);
        else if(option == 3)
            item.buy(player, currency, 5);
        else if(option == 4)
            item.buy(player, currency, 10);
        else if(option == 5)
            item.buy(player, currency, 50);
        else
            player.integerInput("Enter amount to buy:", amount -> item.buy(player, currency, amount));
    }

    static {
        InterfaceHandler.register(Interface.SHOP, h -> {
            h.actions[4] = (DefaultAction) (player, option, slot, itemId) -> {
                if(player.shopActive != null)
                    player.shopActive.handle(player, option, slot, itemId);
            };
            h.closedAction = (p, i) -> {
                p.shopActive = null;
                p.getPacketSender().sendClientScript(299, "ii", 1, 1);
            };
        });
    }

    public static void trade(Player player, NPC npc) {
        Shop shop = npc.getDef().shop;
        if(shop == null) {
            player.dialogue(new MessageDialogue("This shop is not available on this world."));
            return;
        }
        shop.open(player);
    }

}
