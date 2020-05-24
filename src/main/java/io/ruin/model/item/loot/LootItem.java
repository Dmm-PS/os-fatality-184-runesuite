package io.ruin.model.item.loot;

import com.google.gson.annotations.Expose;
import io.ruin.api.utils.Random;
import io.ruin.cache.ItemDef;
import io.ruin.model.item.Item;
import io.ruin.utility.Broadcast;

public class LootItem {

    @Expose public final int id;

    @Expose public final int min, max;

    @Expose public final int weight;

    @Expose public Broadcast broadcast;

    public LootItem(int id, int amount, int weight) {
        this.id = id;
        this.min = amount;
        this.max = amount;
        this.weight = weight;
    }

    public LootItem(int id, int minAmount, int maxAmount, int weight) {
        this.id = id;
        this.min = minAmount;
        this.max = maxAmount;
        this.weight = weight;
    }

    public LootItem broadcast(Broadcast broadcast) {
        this.broadcast = broadcast;
        return this;
    }

    public Item toItem() {
        Item item = new Item(id, min == max ? min : Random.get(min, max));
        item.lootBroadcast = broadcast; //ehhhhhh
        return item;
    }

    public String getName() {
        ItemDef def = ItemDef.get(id);
        if(def.isNote())
            return ItemDef.get(def.notedId).name + " (noted)";
        return def.name;
    }

}