package io.ruin.model.item;

import com.google.gson.annotations.Expose;
import io.ruin.cache.ItemDef;
import io.ruin.model.entity.player.Player;
import io.ruin.utility.Broadcast;

public class Item {

    @Expose private int id, amount;

    @Expose private int uniqueValue;

    private int slot = -1;

    private ItemContainerG container;

    public Broadcast lootBroadcast;

    /**
     * Initiation
     */

    public Item(int id, int amount) {
        this(id, amount, 0);
    }

    public Item(int id) {
        this(id, 1, 0);
    }

    public Item(int id, int amount, int uniqueValue) {
        this.id = id;
        this.amount = amount;
        this.uniqueValue = uniqueValue;
    }

    public Item copy() {
        return new Item(id, amount, uniqueValue);
    }

    public ItemDef getDef() {
        return ItemDef.LOADED[id];
    }

    /**
     * Id
     */

    public void setId(int id) {
        this.id = id;
        update();
    }

    public int getId() {
        return id;
    }

    /**
     * Amount
     */

    public void setAmount(int amount) {
        this.amount = amount;
        update();
    }

	public void incrementAmount(long amount) {
        long newAmount = (long) this.amount + amount;
        if(newAmount <= 0) {
            this.amount = 0;
            remove();
        } else {
            this.amount = (int) Math.min(newAmount, Integer.MAX_VALUE);
            update();
        }
    }

    public int getAmount() {
        return amount;
    }

    /**
     * Unique value
     */

    public void setUniqueValue(int value) {
        this.uniqueValue = value;
    }

    public int getUniqueValue() {
        return uniqueValue;
    }

    public void modifyUniqueValue(int value) {
        setUniqueValue(getUniqueValue() + value);
    }

    /**
     * Slot
     */

    protected void setSlot(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    /**
     * Container
     */

    public void setContainer(ItemContainerG container) {
        this.container = container;
    }

    public ItemContainerG getContainer() {
        return container;
    }

    /**
     * Container methods
     */

    public void remove() {
        if(container != null)
            container.set(slot, null);
    }

    public int remove(int amount) {
        if(container == null)
            return 0;
        if(getDef().stackable) {
            int temp = getAmount();
            incrementAmount(-amount);
            return temp - getAmount();
        }
        return container.remove(id, amount);
    }

    public int move(int addId, int amount, ItemContainerG toContainer) {
        if(container == null || amount <= 0)
            return 0;
        boolean stack;
        int moved;
        if(uniqueValue != 0) {
            //Never allow more than one to be moved at a time. This should only ever apply to banking.
            //Example: Removing "All" blowpipes when you have 3 in your bank with all different charges.
            //Since they all have their own unique value, they'll never stack in the bank. So withdrawing
            //just 1 at a time kind of makes sense anyways.. (And making it work the other way = slower code!)
            stack = false;
            moved = toContainer.add(addId, 1, uniqueValue);
        } else {
            if((stack = container.forceStack || getDef().stackable))
                amount = Math.min(amount, this.amount);
            else
                amount = Math.min(amount, container.count(id));
            moved = toContainer.add(addId, amount, 0);
        }
        if(moved <= 0) {
            /* failed to move to new container */
            return moved;
        }
        if(stack) {
            incrementAmount(-moved);
            return moved;
        }
        if(moved == 1) {
            remove();
            return 1;
        }
        return container.remove(id, moved);
    }

    public int count() {
        if(container == null)
            return 0;
        if(getDef().stackable)
            return amount;
        return container.count(id);
    }

    public void update() {
        if(container != null)
            container.update(slot);
    }

    /**
     * Examine
     */

    public void examine(Player player) {
        examine(player, getId(), getAmount());
    }

    public static void examine(Player player, int id) {
        examine(player, id, 1);
    }

    public static void examine(Player player, int id, int amount) {
        ItemDef def = ItemDef.get(id);
        if(def == null)
            return;
        player.sendMessage(def.examine == null ? "This item has no examine" : def.examine);
        /*
        String s = player.screenSafeColor() + def.name;
        if(amount > 100000)
            s += " x " + Misc.formatNumber(amount);
        if(def.examine != null)
            s += ":</col> " + def.examine;
        else
            s += "</col>";
        player.sendMessage(s);
        if(def.lowAlchValue > 0)
            player.sendMessage("    Low Alch: " + Misc.formatNumber(def.lowAlchValue));
        if(def.highAlchValue > 0)
            player.sendMessage("    High Alch: " + Misc.formatNumber(def.highAlchValue));
        */
    }

}
