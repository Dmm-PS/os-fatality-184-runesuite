package io.ruin.model.item.actions.impl.boxes.mystery;

import io.ruin.cache.Icon;
import io.ruin.model.entity.player.Player;
import io.ruin.model.inter.Interface;
import io.ruin.model.inter.InterfaceHandler;
import io.ruin.model.inter.InterfaceType;
import io.ruin.model.inter.actions.SimpleAction;
import io.ruin.model.item.Item;
import io.ruin.model.item.ItemContainer;
import io.ruin.model.item.actions.ItemAction;
import io.ruin.model.item.loot.LootItem;
import io.ruin.model.item.loot.LootTable;
import io.ruin.utility.Broadcast;

import static io.ruin.cache.ItemID.COINS_995;
import static io.ruin.model.item.actions.impl.boxes.mystery.VoteMysteryBox.VIEW_REWARDS_WIDGET;
import static io.ruin.model.item.actions.impl.boxes.mystery.VoteMysteryBox.updateRewards;

public class SuperMysteryBox extends ItemContainer {

    private static final int SUPER_MYSTERY_BOX = 290;

    private static final int EASTER_EGG = 21227;

    private static final LootTable PVP_MYSTERY_BOX_TABLE = new LootTable().addTable(1,
            new LootItem(4151, 1, 40), //Abyssal whip
            new LootItem(11128, 1, 40), //Berserker necklace
            new LootItem(11840, 1, 40), //Dragon boots
            new LootItem(12002, 1, 20), //Occult necklace
            new LootItem(6585, 1, 20), //Amulet of fury
            new LootItem(12902, 1, 20), //Toxic staff of the dead
            new LootItem(11791, 1, 20), //Staff of the dead
            new LootItem(11785, 1, 20), //Armadyl crossbow
            new LootItem(4224, 1, 20), //New crystal shield
            new LootItem(12831, 1, 20), //Blessed spirit shield
            new LootItem(11926, 1, 20), //Odium ward
            new LootItem(11924, 1, 20), //Malediction ward
            new LootItem(12379, 1, 20), //Rune cane
            new LootItem(12373, 1, 20), //Dragon cane
            new LootItem(12363, 1, 20), //Bronze dragon mask
            new LootItem(6889, 1, 20), //Master's book
            new LootItem(12900, 1, 20), //Uncharged toxic trident
            new LootItem(20724, 1, 20), //Imbued heart
            new LootItem(11908, 1, 20), //Uncharged trident
            new LootItem(12371, 1, 20), //Lava dragon mask
            new LootItem(21634, 1, 20), //Ancient wyvern shield
            new LootItem(22003, 1, 20), //Dragonfire ward
            new LootItem(11284, 1, 20), //Dragonfire shield
            new LootItem(22545, 1, 20), //Viggora's chainmace
            new LootItem(22550, 1, 20), //Craw's bow
            new LootItem(22555, 1, 20), //Thammaron's sceptre
            new LootItem(12931, 1, 10), //Serpentine helm
            new LootItem(13235, 1, 10), //Eternal boots
            new LootItem(13237, 1, 10), //Pegasian boots
            new LootItem(13239, 1, 10), //Primordial boots
            new LootItem(11828, 1, 10), //Armadyl chestplate
            new LootItem(11830, 1, 10), //Armadyl chainskirt
            new LootItem(11826, 1, 10), //Armadyl helmet
            new LootItem(11834, 1, 10), //Bandos tassets
            new LootItem(11832, 1, 10), //Bandos chestplate
            new LootItem(11808, 1, 10), //Zamorak godsword
            new LootItem(11806, 1, 10), //Saradomin godsword
            new LootItem(11804, 1, 10), //Bandos godsword
            new LootItem(11773, 1, 10), //Berserker ring (i)
            new LootItem(11772, 1, 10), //Warriors ring (i)
            new LootItem(11771, 1, 10), //Archers ring (i)
            new LootItem(11770, 1, 10), //Seers ring (i))
            new LootItem(20517, 1, 10), //Elder chaos top
            new LootItem(20520, 1, 10), //Elder chaos bottom
            new LootItem(20595, 1, 10), //Elder chaos hood
            new LootItem(13652, 1, 6), //Dragon claws
            new LootItem(19553, 1, 6), //Amulet of torture
            new LootItem(19547, 1, 6), //Necklace of anguish
            new LootItem(19544, 1, 6), //Tormented bracelet
            new LootItem(19550, 1, 6), //Ring of suffering
            new LootItem(20017, 1, 4), //Ring of coins
            new LootItem(6583, 1, 4), //Ring of stone
            new LootItem(20005, 1, 4), //Ring of nature
            new LootItem(11806, 1, 2), //Armadyl godsword
            new LootItem(12821, 1, 2).broadcast(Broadcast.GLOBAL), //Spectral spirit shield
            new LootItem(13271, 1, 2), //Abyssal dagger
            new LootItem(11785, 1, 2), //Armadyl crossbow
            new LootItem(13576, 1, 2), //Dragon warhammer
            new LootItem(2581, 1, 2), //Robin hood hat
            new LootItem(12596, 1, 2), //Rangers' tunic
            new LootItem(22981, 1, 1), //Ferocious gloves
            new LootItem(22975, 1, 1), //Brimstone ring
            new LootItem(22978, 1, 1), //Dragon hunter lance
            new LootItem(1053, 1, 1).broadcast(Broadcast.GLOBAL), //Green halloween mask
            new LootItem(1055, 1, 1).broadcast(Broadcast.GLOBAL), //Blue halloween mask
            new LootItem(1057, 1, 1).broadcast(Broadcast.GLOBAL), //Red halloween mask
            new LootItem(11847, 1, 1).broadcast(Broadcast.GLOBAL), //Black halloween mask
            new LootItem(1050, 1, 1).broadcast(Broadcast.GLOBAL), //Santa hat
            new LootItem(13343, 1, 1).broadcast(Broadcast.GLOBAL), //Black santa hat
            new LootItem(13344, 1, 1).broadcast(Broadcast.GLOBAL), //Inverted santa hat
            new LootItem(1038, 1, 1).broadcast(Broadcast.GLOBAL), //Red party hat
            new LootItem(1040, 1, 1).broadcast(Broadcast.GLOBAL), //Yellow party hat
            new LootItem(1042, 1, 1).broadcast(Broadcast.GLOBAL), //Blue party hat
            new LootItem(1044, 1, 1).broadcast(Broadcast.GLOBAL), //Green party hat
            new LootItem(1046, 1, 1).broadcast(Broadcast.GLOBAL), //Purple party hat
            new LootItem(1048, 1, 1).broadcast(Broadcast.GLOBAL), //White  party hat
            new LootItem(11862, 1, 1).broadcast(Broadcast.GLOBAL), //Black party hat
            new LootItem(11863, 1, 1).broadcast(Broadcast.GLOBAL), //Rainbow party hat
            new LootItem(12399, 1, 1).broadcast(Broadcast.GLOBAL), //Partyhat & specs
            new LootItem(962, 1, 1).broadcast(Broadcast.GLOBAL), // Xmas cracker
            new LootItem(1050, 1, 1).broadcast(Broadcast.GLOBAL), // Santa hat
            new LootItem(1419, 1, 1), // Scythe
            new LootItem(1037, 1, 1), // Bunny ears
            new LootItem(22326, 1, 1), //Justiciar helm
            new LootItem(22327, 1, 1), //Justiciar body
            new LootItem(22328, 1, 1), //Justiciar legs
            new LootItem(21018, 1, 1), //Ancestral hat
            new LootItem(21021, 1, 1), //Ancestral body
            new LootItem(21024, 1, 1), //Ancestral skirt
            new LootItem(12422, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age wand
            new LootItem(12424, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age bow
            new LootItem(12426, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age sword
            new LootItem(12437, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age cloak
            new LootItem(10330, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age range top
            new LootItem(10332, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age range legs
            new LootItem(10334, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age range coif
            new LootItem(10336, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age range vanbraces
            new LootItem(10338, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age robe top
            new LootItem(10340, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age robe
            new LootItem(10342, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age mage hat
            new LootItem(10344, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age amulet
            new LootItem(10346, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age platelegs
            new LootItem(10348, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age platebody
            new LootItem(10350, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age fullhelm
            new LootItem(10352, 1, 1).broadcast(Broadcast.GLOBAL) // 3rd age kiteshield
    );

    private static final LootTable ECO_MYSTERY_BOX_TABLE = new LootTable().addTable(1,
            new LootItem(COINS_995, 4000000, 6000000, 30), //6M coins
            new LootItem(COINS_995, 4000000, 8000000, 30), //8M coins
            new LootItem(4151, 1, 30), //Abyssal whip
            new LootItem(11128, 1, 30), //Berserker necklace
            new LootItem(11840, 1, 30), //Dragon boots
            new LootItem(12002, 1, 15), //Occult necklace
            new LootItem(6585, 1, 15), //Amulet of fury
            new LootItem(12902, 1, 15), //Toxic staff of the dead
            new LootItem(11791, 1, 15), //Staff of the dead
            new LootItem(11785, 1, 15), //Armadyl crossbow
            new LootItem(4224, 1, 15), //New crystal shield
            new LootItem(12831, 1, 15), //Blessed spirit shield
            new LootItem(11926, 1, 15), //Odium ward
            new LootItem(11924, 1, 15), //Malediction ward
            new LootItem(12379, 1, 15), //Rune cane
            new LootItem(12373, 1, 15), //Dragon cane
            new LootItem(12363, 1, 15), //Bronze dragon mask
            new LootItem(6889, 1, 15), //Master's book
            new LootItem(12900, 1, 15), //Uncharged toxic trident
            new LootItem(20724, 1, 15), //Imbued heart
            new LootItem(11908, 1, 15), //Uncharged trident
            new LootItem(12371, 1, 15), //Lava dragon mask
            new LootItem(21634, 1, 15), //Ancient wyvern shield
            new LootItem(22003, 1, 15), //Dragonfire ward
            new LootItem(11284, 1, 15), //Dragonfire shield
            new LootItem(22545, 1, 15), //Viggora's chainmace
            new LootItem(22550, 1, 15), //Craw's bow
            new LootItem(22555, 1, 15), //Thammaron's sceptre
            new LootItem(12931, 1, 5), //Serpentine helm
            new LootItem(13235, 1, 5), //Eternal boots
            new LootItem(13237, 1, 5), //Pegasian boots
            new LootItem(13239, 1, 5), //Primordial boots
            new LootItem(11828, 1, 5), //Armadyl chestplate
            new LootItem(11830, 1, 5), //Armadyl chainskirt
            new LootItem(11826, 1, 5), //Armadyl helmet
            new LootItem(11834, 1, 5), //Bandos tassets
            new LootItem(11832, 1, 5), //Bandos chestplate
            new LootItem(11808, 1, 5), //Zamorak godsword
            new LootItem(11806, 1, 5), //Saradomin godsword
            new LootItem(11804, 1, 5), //Bandos godsword
            new LootItem(11773, 1, 5), //Berserker ring (i)
            new LootItem(11772, 1, 5), //Warriors ring (i)
            new LootItem(11771, 1, 5), //Archers ring (i)
            new LootItem(11770, 1, 5), //Seers ring (i))
            new LootItem(20517, 1, 5), //Elder chaos top
            new LootItem(20520, 1, 5), //Elder chaos bottom
            new LootItem(20595, 1, 5), //Elder chaos hood
            new LootItem(13652, 1, 3), //Dragon claws
            new LootItem(19553, 1, 3), //Amulet of torture
            new LootItem(19547, 1, 3), //Necklace of anguish
            new LootItem(19544, 1, 3), //Tormented bracelet
            new LootItem(19550, 1, 3), //Ring of suffering
            new LootItem(20017, 1, 2), //Ring of coins
            new LootItem(6583, 1, 2), //Ring of stone
            new LootItem(20005, 1, 2), //Ring of nature
            new LootItem(COINS_995, 5000000, 10000000, 2), //Coins
            new LootItem(COINS_995, 8000000, 12000000, 2), //Coins
            new LootItem(11806, 1, 2), //Armadyl godsword
            new LootItem(12821, 1, 2).broadcast(Broadcast.GLOBAL), //Spectral spirit shield
            new LootItem(13271, 1, 2), //Abyssal dagger
            new LootItem(11785, 1, 2).broadcast(Broadcast.GLOBAL), //Armadyl crossbow
            new LootItem(13576, 1, 2).broadcast(Broadcast.GLOBAL), //Dragon warhammer
            new LootItem(2581, 1, 2), //Robin hood hat
            new LootItem(12596, 1, 2), //Rangers' tunic
            new LootItem(22981, 1, 1), //Ferocious gloves
            new LootItem(22975, 1, 1), //Brimstone ring
            new LootItem(22978, 1, 1), //Dragon hunter lance
            new LootItem(1053, 1, 1).broadcast(Broadcast.GLOBAL), //Green halloween mask
            new LootItem(1055, 1, 1).broadcast(Broadcast.GLOBAL), //Blue halloween mask
            new LootItem(1057, 1, 1).broadcast(Broadcast.GLOBAL), //Red halloween mask
            new LootItem(11847, 1, 1).broadcast(Broadcast.GLOBAL), //Black halloween mask
            new LootItem(1050, 1, 1).broadcast(Broadcast.GLOBAL), //Santa hat
            new LootItem(13343, 1, 1).broadcast(Broadcast.GLOBAL), //Black santa hat
            new LootItem(13344, 1, 1).broadcast(Broadcast.GLOBAL), //Inverted santa hat
            new LootItem(1038, 1, 1).broadcast(Broadcast.GLOBAL), //Red party hat
            new LootItem(1040, 1, 1).broadcast(Broadcast.GLOBAL), //Yellow party hat
            new LootItem(1042, 1, 1).broadcast(Broadcast.GLOBAL), //Blue party hat
            new LootItem(1044, 1, 1).broadcast(Broadcast.GLOBAL), //Green party hat
            new LootItem(1046, 1, 1).broadcast(Broadcast.GLOBAL), //Purple party hat
            new LootItem(1048, 1, 1).broadcast(Broadcast.GLOBAL), //White  party hat
            new LootItem(11862, 1, 1).broadcast(Broadcast.GLOBAL), //Black party hat
            new LootItem(11863, 1, 1).broadcast(Broadcast.GLOBAL), //Rainbow party hat
            new LootItem(12399, 1, 1).broadcast(Broadcast.GLOBAL), //Partyhat & specs
            new LootItem(962, 1, 1).broadcast(Broadcast.GLOBAL), // Xmas cracker
            new LootItem(1050, 1, 1).broadcast(Broadcast.GLOBAL), // Santa hat
            new LootItem(21003, 1, 4).broadcast(Broadcast.GLOBAL), //Elder maul
            new LootItem(1419, 1, 1).broadcast(Broadcast.GLOBAL), // Scythe
            new LootItem(1037, 1, 1).broadcast(Broadcast.GLOBAL), // Bunny ears
            new LootItem(21006, 1, 3).broadcast(Broadcast.GLOBAL), //Kodai wand
            new LootItem(21006, 1, 4).broadcast(Broadcast.GLOBAL), //Vesta's longsword
            new LootItem(21006, 1, 4).broadcast(Broadcast.GLOBAL), //Statius's warhammer
            new LootItem(12422, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age wand
            new LootItem(12424, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age bow
            new LootItem(12426, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age sword
            new LootItem(12437, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age cloak
            new LootItem(10330, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age range top
            new LootItem(10332, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age range legs
            new LootItem(10334, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age range coif
            new LootItem(10336, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age range vanbraces
            new LootItem(10338, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age robe top
            new LootItem(10340, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age robe
            new LootItem(10342, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age mage hat
            new LootItem(10344, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age amulet
            new LootItem(10346, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age platelegs
            new LootItem(10348, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age platebody
            new LootItem(10350, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age fullhelm
            new LootItem(10352, 1, 1).broadcast(Broadcast.GLOBAL) // 3rd age kiteshield
    );

    private static final LootTable EASTER_EGG_TABLE = new LootTable().addTable(1,
            new LootItem(COINS_995, 4000000, 6000000, 30), //6M coins
            new LootItem(COINS_995, 4000000, 8000000, 30), //8M coins
            new LootItem(4151, 1, 30), //Abyssal whip
            new LootItem(22351, 1, 10), //Eggshell body
            new LootItem(22353, 1, 10), //Eggshell legs
            new LootItem(21214, 1, 10), //Easter egg helm
            new LootItem(1037, 1, 10), //Bunny ears
            new LootItem(13182, 1, 10), //Bunny feet
            new LootItem(13663, 1, 10), //Bunny top
            new LootItem(13664, 1, 10), //Bunny legs
            new LootItem(13665, 1, 10), //Bunny paws
            new LootItem(6585, 1, 15), //Amulet of fury
            new LootItem(12902, 1, 15), //Toxic staff of the dead
            new LootItem(11791, 1, 15), //Staff of the dead
            new LootItem(11785, 1, 15), //Armadyl crossbow
            new LootItem(4224, 1, 15), //New crystal shield
            new LootItem(12831, 1, 15), //Blessed spirit shield
            new LootItem(11926, 1, 15), //Odium ward
            new LootItem(11924, 1, 15), //Malediction ward
            new LootItem(12379, 1, 15), //Rune cane
            new LootItem(12373, 1, 15), //Dragon cane
            new LootItem(12363, 1, 15), //Bronze dragon mask
            new LootItem(6889, 1, 15), //Master's book
            new LootItem(12900, 1, 15), //Uncharged toxic trident
            new LootItem(20724, 1, 15), //Imbued heart
            new LootItem(11908, 1, 15), //Uncharged trident
            new LootItem(12371, 1, 15), //Lava dragon mask
            new LootItem(21634, 1, 15), //Ancient wyvern shield
            new LootItem(22003, 1, 15), //Dragonfire ward
            new LootItem(11284, 1, 15), //Dragonfire shield
            new LootItem(22545, 1, 15), //Viggora's chainmace
            new LootItem(22550, 1, 15), //Craw's bow
            new LootItem(22555, 1, 15), //Thammaron's sceptre
            new LootItem(12931, 1, 5), //Serpentine helm
            new LootItem(13235, 1, 5), //Eternal boots
            new LootItem(13237, 1, 5), //Pegasian boots
            new LootItem(13239, 1, 5), //Primordial boots
            new LootItem(11828, 1, 5), //Armadyl chestplate
            new LootItem(11830, 1, 5), //Armadyl chainskirt
            new LootItem(11826, 1, 5), //Armadyl helmet
            new LootItem(11834, 1, 5), //Bandos tassets
            new LootItem(11832, 1, 5), //Bandos chestplate
            new LootItem(11808, 1, 5), //Zamorak godsword
            new LootItem(11806, 1, 5), //Saradomin godsword
            new LootItem(11804, 1, 5), //Bandos godsword
            new LootItem(11773, 1, 5), //Berserker ring (i)
            new LootItem(11772, 1, 5), //Warriors ring (i)
            new LootItem(11771, 1, 5), //Archers ring (i)
            new LootItem(11770, 1, 5), //Seers ring (i))
            new LootItem(20517, 1, 5), //Elder chaos top
            new LootItem(20520, 1, 5), //Elder chaos bottom
            new LootItem(20595, 1, 5), //Elder chaos hood
            new LootItem(13652, 1, 3), //Dragon claws
            new LootItem(19553, 1, 3), //Amulet of torture
            new LootItem(19547, 1, 3), //Necklace of anguish
            new LootItem(19544, 1, 3), //Tormented bracelet
            new LootItem(19550, 1, 3), //Ring of suffering
            new LootItem(20017, 1, 2), //Ring of coins
            new LootItem(6583, 1, 2), //Ring of stone
            new LootItem(20005, 1, 2), //Ring of nature
            new LootItem(COINS_995, 5000000, 10000000, 2), //Coins
            new LootItem(COINS_995, 8000000, 12000000, 2), //Coins
            new LootItem(11806, 1, 2), //Armadyl godsword
            new LootItem(12821, 1, 2).broadcast(Broadcast.GLOBAL), //Spectral spirit shield
            new LootItem(13271, 1, 2), //Abyssal dagger
            new LootItem(11785, 1, 2).broadcast(Broadcast.GLOBAL), //Armadyl crossbow
            new LootItem(13576, 1, 2).broadcast(Broadcast.GLOBAL), //Dragon warhammer
            new LootItem(2581, 1, 2), //Robin hood hat
            new LootItem(12596, 1, 2), //Rangers' tunic
            new LootItem(20997, 1, 2).broadcast(Broadcast.GLOBAL), // Twisted bow
            new LootItem(22981, 1, 1), //Ferocious gloves
            new LootItem(22975, 1, 1), //Brimstone ring
            new LootItem(22978, 1, 1), //Dragon hunter lance
            new LootItem(1053, 1, 1).broadcast(Broadcast.GLOBAL), //Green halloween mask
            new LootItem(1055, 1, 1).broadcast(Broadcast.GLOBAL), //Blue halloween mask
            new LootItem(1057, 1, 1).broadcast(Broadcast.GLOBAL), //Red halloween mask
            new LootItem(11847, 1, 1).broadcast(Broadcast.GLOBAL), //Black halloween mask
            new LootItem(1050, 1, 1).broadcast(Broadcast.GLOBAL), //Santa hat
            new LootItem(13343, 1, 1).broadcast(Broadcast.GLOBAL), //Black santa hat
            new LootItem(13344, 1, 1).broadcast(Broadcast.GLOBAL), //Inverted santa hat
            new LootItem(1038, 1, 1).broadcast(Broadcast.GLOBAL), //Red party hat
            new LootItem(1040, 1, 1).broadcast(Broadcast.GLOBAL), //Yellow party hat
            new LootItem(1042, 1, 1).broadcast(Broadcast.GLOBAL), //Blue party hat
            new LootItem(1044, 1, 1).broadcast(Broadcast.GLOBAL), //Green party hat
            new LootItem(1046, 1, 1).broadcast(Broadcast.GLOBAL), //Purple party hat
            new LootItem(1048, 1, 1).broadcast(Broadcast.GLOBAL), //White  party hat
            new LootItem(11862, 1, 1).broadcast(Broadcast.GLOBAL), //Black party hat
            new LootItem(11863, 1, 1).broadcast(Broadcast.GLOBAL), //Rainbow party hat
            new LootItem(12399, 1, 1).broadcast(Broadcast.GLOBAL), //Partyhat & specs
            new LootItem(962, 1, 1).broadcast(Broadcast.GLOBAL), // Xmas cracker
            new LootItem(1050, 1, 1).broadcast(Broadcast.GLOBAL), // Santa hat
            new LootItem(1419, 1, 1).broadcast(Broadcast.GLOBAL), // Scythe
            new LootItem(1037, 1, 1).broadcast(Broadcast.GLOBAL), // Bunny ears
            new LootItem(12422, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age wand
            new LootItem(12424, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age bow
            new LootItem(12426, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age sword
            new LootItem(12437, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age cloak
            new LootItem(10330, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age range top
            new LootItem(10332, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age range legs
            new LootItem(10334, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age range coif
            new LootItem(10336, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age range vanbraces
            new LootItem(10338, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age robe top
            new LootItem(10340, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age robe
            new LootItem(10342, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age mage hat
            new LootItem(10344, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age amulet
            new LootItem(10346, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age platelegs
            new LootItem(10348, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age platebody
            new LootItem(10350, 1, 1).broadcast(Broadcast.GLOBAL), // 3rd age fullhelm
            new LootItem(10352, 1, 1).broadcast(Broadcast.GLOBAL) // 3rd age kiteshield
    );

    public static void open(Player player, Item item) {
        InterfaceHandler.register(Interface.SUPER_MYSTERY_BOX, h -> {
            h.actions[7] = (SimpleAction) SuperMysteryBox::spin;
            h.actions[19] = (SimpleAction) SuperMysteryBox::claimReward;
            h.actions[21] = (SimpleAction) SuperMysteryBox::discardReward;
            h.actions[23] = (SimpleAction) SuperMysteryBox::openRewards;
        });

        if(player.isVisibleInterface(Interface.SUPER_MYSTERY_BOX)) {
            player.sendMessage("You need to claim or discard your reward before doing this!");
            return;
        }

        player.easterEgg = item.getId() == EASTER_EGG;
        if(player.claimedBox) {
            player.getBox().clear();
            generateReward(player);
            player.getBox().sendUpdates();
            player.openInterface(InterfaceType.MAIN, 702);
            player.getPacketSender().sendClientScript(10034, "ssii", "Spins", "Get ready to test your luck with our wheel of fortune! Click the \"spin\" button when you're ready. There's all sorts of items to be won within this " + (player.easterEgg ? "easter egg" : "mystery spinning box") + "!<br><br>If you wish to skip the rolling, you can click \"Spin\" again.<br><br><col=ffff00>Will you walk away with riches... or with rubbish? Good luck!", 15, 0);
        } else {
            player.getBox().sendUpdates();
            player.openInterface(InterfaceType.MAIN, 702);
            player.getPacketSender().sendClientScript(10034, "ssii", "Spins", "Get ready to test your luck with our wheel of fortune! Click the \"spin\" button when you're ready. There's all sorts of items to be won within this " + (player.easterEgg ? "easter egg" : "mystery spinning box") + "!<br><br>If you wish to skip the rolling, you can click \"Spin\" again.<br><br><col=ffff00>Will you walk away with riches... or with rubbish? Good luck!", 15, 1);
        }
    }

    private static void generateReward(Player player) {
        LootTable table = ECO_MYSTERY_BOX_TABLE;
        if(player.easterEgg)
            table = EASTER_EGG_TABLE;
        for(int i = 0; i < 24; i ++)
            player.getBox().add(table.rollItem());
        Item reward = player.getBox().get(15);
        if(reward == null)
            generateReward(player);
    }

    private static void spin(Player player) {
        if(player.easterEgg) {
            Item easterEgg = player.getInventory().findItem(EASTER_EGG);
            if(easterEgg == null) {
                player.closeInterface(InterfaceType.MAIN);
                return;
            }
            player.claimedBox = false;
            easterEgg.remove();
        } else {
            Item superMysteryBox = player.getInventory().findItem(SUPER_MYSTERY_BOX);
            if(superMysteryBox == null) {
                player.closeInterface(InterfaceType.MAIN);
                return;
            }
            player.claimedBox = false;
            superMysteryBox.remove();
        }
    }

    private static void claimReward(Player player) {
        Item reward = player.getBox().get(15);
        player.getInventory().add(reward);
        player.claimedBox = true;
        player.sendMessage("You get " + reward.getDef().descriptiveName + " from the " + (player.easterEgg ? "Easter egg." : "Super Mystery Box."));
        if (reward.lootBroadcast != null)
            Broadcast.GLOBAL.sendNews(Icon.MYSTERY_BOX, player.easterEgg ? "Easter Egg" : "Super Mystery Box", "" + player.getName() + " just received " + reward.getDef().descriptiveName + "!");
        player.getBox().clear();
        if(player.isVisibleInterface(Interface.SUPER_MYSTERY_BOX))
            player.closeInterface(InterfaceType.MAIN);
    }

    private static void discardReward(Player player) {
        player.closeInterface(InterfaceType.MAIN);
        player.claimedBox = true;
        player.sendMessage("You discard your Super Mystery Box reward.");
        player.getBox().clear();
    }

    public static void openRewards(Player player) {
        player.openInterface(InterfaceType.MAIN, VIEW_REWARDS_WIDGET);
        player.closeInterface(InterfaceType.INVENTORY);
        updateRewards(player, ECO_MYSTERY_BOX_TABLE);
    }

    static {
        ItemAction.registerInventory(SUPER_MYSTERY_BOX, "open", SuperMysteryBox::open);
        ItemAction.registerInventory(EASTER_EGG, "open", SuperMysteryBox::open);
    }
}
