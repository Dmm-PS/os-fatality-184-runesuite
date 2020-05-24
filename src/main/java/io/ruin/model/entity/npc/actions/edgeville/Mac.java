package io.ruin.model.entity.npc.actions.edgeville;

import io.ruin.cache.ItemDef;
import io.ruin.model.entity.npc.NPC;
import io.ruin.model.entity.npc.NPCAction;
import io.ruin.model.entity.player.Player;
import io.ruin.model.inter.Interface;
import io.ruin.model.inter.InterfaceHandler;
import io.ruin.model.inter.InterfaceType;
import io.ruin.model.inter.actions.SimpleAction;
import io.ruin.model.inter.dialogue.OptionsDialogue;
import io.ruin.model.inter.dialogue.YesNoDialogue;
import io.ruin.model.inter.utils.Option;
import io.ruin.model.item.Item;
import io.ruin.model.item.actions.impl.MaxCape;
import io.ruin.model.item.containers.shop.Shop;
import io.ruin.model.item.containers.shop.ShopCurrency;
import io.ruin.model.item.containers.shop.ShopItem;
import io.ruin.model.skills.herblore.Potion;
import io.ruin.model.stat.StatType;

import java.util.ArrayList;

import static io.ruin.cache.ItemID.COINS_995;

public class Mac {

    static {
        StatType[] types = StatType.values();
        ArrayList<ShopItem> capes = new ArrayList<>(types.length);
        for(StatType type : types) {
            capes.add(new CapeItem(type, false));
            capes.add(new CapeItem(type, true));
        }
        capes.add(new CapeItem()); //max cape
        Shop capeShop = new Shop("Mac's Cape Emporium", (ShopCurrency.COINS), true, capes);

        NPCAction.register(6481, "buy-capes", (p, n) -> capeShop.open(p));
        NPCAction.register(6481, "decant-potions", Mac::decantPotions);
        NPCAction.register(6481, "reset-levels", Mac::resetLevels);

        InterfaceHandler.register(Interface.POTION_DECANTING, h -> {
            h.actions[3] = (SimpleAction) p -> Potion.decant(p, 1);
            h.actions[4] = (SimpleAction) p -> Potion.decant(p, 2);
            h.actions[5] = (SimpleAction) p -> Potion.decant(p, 3);
            h.actions[6] = (SimpleAction) p -> Potion.decant(p, 4);
        });
    }

    /**
     * Decant potions
     */

    private static void decantPotions(Player player, NPC npc) {
        player.openInterface(InterfaceType.CHATBOX, Interface.POTION_DECANTING);
    }

    /**
     * Resetting levels
     */

    private static final int RESET_COST = 500000;

    private static void resetLevels(Player player, NPC npc) {
        player.dialogue(
                new OptionsDialogue(
                        new Option("Reset Attack", () -> resetLevel(player, StatType.Attack)),
                        new Option("Reset Strength", () -> resetLevel(player, StatType.Strength)),
                        new Option("Reset Defence", () -> resetLevel(player, StatType.Defence)),
                        new Option("Reset Ranged", () -> resetLevel(player, StatType.Ranged)),
                        new Option("More...", () -> {
                            player.dialogue(new OptionsDialogue(
                                    new Option("Reset Prayer", () -> resetLevel(player, StatType.Prayer)),
                                    new Option("Reset Magic", () -> resetLevel(player, StatType.Magic)),
                                    new Option("Previous...", () -> resetLevels(player, npc))
                            ));
                        })
                )
        );
    }

    private static void resetLevel(Player player, StatType stat) {
        player.dialogue(
                new YesNoDialogue("Reset your " + stat.name() + " level to 1?", "This action cannot be undone and will cost you 500,000 coins.", COINS_995, RESET_COST, () -> {
                    Item coins = player.getInventory().findItem(COINS_995);
                    if(coins == null || coins.getAmount() < RESET_COST) {
                        player.sendMessage("You don't have enough coins to reset your " + stat.name() + " level.");
                        return;
                    }
                    if(!player.getEquipment().isEmpty()) {
                        player.sendMessage("You must remove all equipment before resetting levels.");
                        return;
                    }
                    coins.remove(RESET_COST);
                    player.getPrayer().deactivateAll();
                    player.getStats().get(stat).resetTo1();
                    player.getCombat().updateLevel();
                    player.sendMessage("Your " + stat.name() + " level has been reset.");
                })
        );
    }

    private static final int ECO_PRICE = 99000;
    private static final int PVP_PRICE = 50;


    /**
     * Cape Item (For shop)
     */

    private static final class CapeItem extends ShopItem {

        private StatType type;

        private CapeItem() {
            this(null, false);
        }

        private CapeItem(StatType type, boolean trimmed) {
            super((type == null ? 13280 : (trimmed ? type.trimmedCapeId : type.regularCapeId)),
                    (type == null ? ECO_PRICE * 23 : ECO_PRICE));
            this.type = type;
        }

        @Override
        public int getDisplayId(Player player) {
            if(type == null ? !MaxCape.unlocked(player) : player.getStats().get(type).fixedLevel < 99) {
                ItemDef def = getDef();
                if(def.hasPlaceholder())
                    return def.placeholderMainId;
            }
            return getId();
        }

        @Override
        public void buy(Player player, ShopCurrency currency, int amount) {
            if(amount <= 0)
                return;
            if(amount > 1) {
                player.sendMessage("You can only buy one of these capes at a time.");
                return;
            }
            if(type == null) {
                if(!MaxCape.unlocked(player)) {
                    player.sendMessage("You need to be maxed in all trainable stats to purchase this cape.");
                    return;
                }
            } else {
                if(player.getStats().get(type).fixedLevel < 99) {
                    player.sendMessage("You need " + type.descriptiveName + " level of 99 to buy this cape.");
                    return;
                }
                if(getId() == type.trimmedCapeId && player.getStats().total99s < 2) {
                    player.sendMessage("You need at least two maxed stats to buy trimmed capes.");
                    return;
                }
            }
            if(player.getInventory().getFreeSlots() < 2) {
                player.sendMessage("You need at least 2 free slots to buy this cape and the hood that comes with it.");
                return;
            }
            int currencyAmount = currency.getAmount(player);
            if(currencyAmount < price) {
                player.sendMessage("You don't have enough " + currency.name + " to buy this cape.");
                return;
            }
            player.getInventory().add(type == null ? 13281 : type.hoodId, 1);
            player.getInventory().add(getId(), 1);
            currency.remove(player, price);
        }

    }

}