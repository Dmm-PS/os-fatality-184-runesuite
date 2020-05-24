package io.ruin.data.impl.npcs;

import com.google.gson.annotations.Expose;
import io.ruin.api.utils.JsonUtils;
import io.ruin.cache.NPCDef;
import io.ruin.data.DataFile;
import io.ruin.model.World;
import io.ruin.model.achievements.Achievement;
import io.ruin.model.entity.npc.NPCAction;
import io.ruin.model.item.containers.shop.Shop;
import io.ruin.model.item.containers.shop.ShopCurrency;
import io.ruin.model.item.containers.shop.ShopItem;

import java.util.ArrayList;
import java.util.List;

public class npc_shops implements DataFile {

    @Override
    public String path() {
        return "npcs/shops/" + (World.isPVP() ? "pvp" : "eco") + "/*.json";
    }

    @Override
    public Object fromJson(String fileName, String json) {
        TempShop s = JsonUtils.fromJson(json, TempShop.class);
        List<ShopItem> items = new ArrayList<>();
        for(TempItem i : s.items)
            items.add(new ShopItem(i.id, i.price, -1, i.achievement));
        Shop shop = new Shop(s.name, s.currency, s.ironman, items);
        for(TempNpc n : s.npcs) {
            NPCDef.get(n.id).shop = shop;
            NPCAction.register(n.id, n.option, (player, npc) -> {
                if(player.getGameMode().isIronMan() && !shop.ironman) {
                    player.sendMessage("You can't access this shop as an ironman!");
                    return;
                }
                shop.open(player);
            });
        }
        return s;
    }

    private static final class TempShop {
        @Expose String name;
        @Expose ShopCurrency currency;
        @Expose boolean ironman;
        @Expose TempNpc[] npcs;
        @Expose TempItem[] items;
    }

    private static final class TempItem {
        @Expose int id;
        @Expose int price;
        @Expose Achievement achievement;
    }

    private static final class TempNpc {
        @Expose int id;
        @Expose String option;
    }

}