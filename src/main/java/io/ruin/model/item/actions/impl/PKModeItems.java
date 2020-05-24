package io.ruin.model.item.actions.impl;

import io.ruin.cache.ItemDef;
import io.ruin.model.entity.player.Player;
import io.ruin.model.item.Item;
import io.ruin.model.item.actions.ItemAction;

public class PKModeItems {

	private static final int ARMADYL_GODSWORD = 20593;
	private static final int DRAGON_CLAWS = 20784;
	private static final int[] ITEMS = new int[] {ARMADYL_GODSWORD, DRAGON_CLAWS};

	static {
		for (int itemId : ITEMS) {
			ItemAction.registerInventory(itemId, 4, PKModeItems::examine);
			ItemDef.get(itemId).addPostTargetDefendListener((player, item, hit, target) -> {
				int charges = item.getUniqueValue();
				if (--charges <= 0) {
					item.remove();
					player.sendMessage("Your "+ item.getDef().name +" (temp) has degraded.");
				}
				item.setUniqueValue(charges);
			});
		}
	}

	public static void examine(Player player, Item item) {
		player.sendMessage("Your "+ item.getDef().name +" (temp) has "+ item.getUniqueValue() +" uses remaining.");
	}
}
