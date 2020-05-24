package io.ruin.model.item.actions.impl.jewellery;

import io.ruin.model.map.Bounds;

public enum SkillsNecklace {

    SIX(11968, 6, 11970),
    FIVE(11970, 5, 11105),
    FOUR(11105, 4, 11107),
    THREE(11107, 3, 11109),
    TWO(11109, 2, 11111),
    ONE(11111, 1, 11113),
    UNCHARGED(11113, 0, -1);

    private final int id, charges, replacementId;

    SkillsNecklace(int id, int charges, int replacementId) {
        this.id = id;
        this.charges = charges;
        this.replacementId = replacementId;
    }

    static {
        JeweleryTeleports teleports = new JeweleryTeleports("necklace", false,
                new JeweleryTeleports.Teleport("todo1", new Bounds(1111, 2222, 1111, 2222, 0)),
                new JeweleryTeleports.Teleport("todo2", new Bounds(1111, 2222, 1111, 2222, 0)),
                new JeweleryTeleports.Teleport("todo3", new Bounds(1111, 2222, 1111, 2222, 0)),
                new JeweleryTeleports.Teleport("todo4", new Bounds(1111, 2222, 1111, 2222, 0)),
                new JeweleryTeleports.Teleport("todo5", new Bounds(1111, 2222, 1111, 2222, 0))
        );
        for(SkillsNecklace necklace : values())
            teleports.register(necklace.id, necklace.charges, necklace.replacementId);
    }

}
