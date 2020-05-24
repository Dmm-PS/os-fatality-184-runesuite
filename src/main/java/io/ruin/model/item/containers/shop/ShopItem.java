package io.ruin.model.item.containers.shop;

import io.ruin.cache.ItemDef;
import io.ruin.model.achievements.Achievement;
import io.ruin.model.entity.player.Player;
import io.ruin.model.item.Item;
import io.ruin.services.Loggers;

public class ShopItem extends Item {

    public int price;

    public int remaining;

    public final Achievement achievement;

    public ShopItem(int id, int price) {
        this(id, price, -1, null);
    }

    public ShopItem(int id, int price, int remaining) {
        this(id, price, remaining, null);
    }

    public ShopItem(int id, int price, int remaining, Achievement achievement) {
        super(id, 1);
        this.price = price;
        this.remaining = remaining;
        this.achievement = achievement;
    }

    public int getDisplayId(Player player) {
        if(showPlaceholder(player)) {
            ItemDef def = getDef();
            if(def.hasPlaceholder())
                return def.placeholderMainId;
            System.err.println("Warning: Achievement item (" + getId() + ") has no placeholder!");
        }
        return getId();
    }

    private boolean showPlaceholder(Player player) {
        return achievement != null && !achievement.isFinished(player);
    }

    protected long getPrice(long amount) {
        return (long) this.price * amount;
    }

    /**
     * Buy
     */

    protected void buy(Player player, ShopCurrency currency, int amount) {
        if(amount <= 0)
            return;
        /**
         * Container check
         */
        ItemDef def = getDef();
        int freeSlots = player.getInventory().getFreeSlots();
        if(amount > freeSlots) {
            /**
             * Attempt to note the given item.
             */
            if(!def.isNote() && def.notedId != -1)
                def = def.fromNote();
        }
        if(def.stackable) {
            /**
             * 'Free' a slot if necessary.
             */
            if(freeSlots == 0 && player.getInventory().findItem(def.id) != null)
                freeSlots++;
        } else if(amount > freeSlots) {
            /**
             * Set amount equal to free slots.
             */
            amount = freeSlots;
        }
        if(freeSlots == 0) {
            player.sendMessage("Not enough space in your inventory.");
            return;
        }
        /**
         * Currency check
         */
        if(price != 0) {
            long buyPrice = getPrice(amount);
            int currencyAmount = currency.getAmount(player);
            if(currencyAmount < buyPrice) {
                if(price > currencyAmount) {
                    /* not enough currency to even buy 1 */
                    amount = 0;
                } else {
                    amount = currencyAmount / price;
                    buyPrice = getPrice(amount);
                }

                if (amount <= 0) {
                    player.sendMessage("You don't have enough " + currency.name + " to purchase this item.");

                    if (currency == ShopCurrency.WILDERNESS_POINTS) {
                        player.sendMessage("You need " + price + " point" + (price == 1 ? "" : "s") + " to purchase this, but you only have " + currencyAmount + ".");
                    }

                    return;
                }
            }
            if(buyPrice <= 0L || buyPrice < price || buyPrice > Integer.MAX_VALUE) {
                player.sendMessage("Please buy this item in a smaller quantity.");
                return;
            }
            currency.remove(player, (int) buyPrice);
        }
        player.getInventory().add(def.id, amount);
        Loggers.logShopBuy(player.getUserId(), player.getName(), player.getIp(), getId(), price, amount);
    }

}