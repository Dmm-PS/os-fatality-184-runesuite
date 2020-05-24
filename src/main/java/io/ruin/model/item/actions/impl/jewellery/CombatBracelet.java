package io.ruin.model.item.actions.impl.jewellery;

import io.ruin.model.map.Bounds;

public enum CombatBracelet {

    SIX(11972, 6, 11974),
    FIVE(11974, 5, 11118),
    FOUR(11118, 4, 11120),
    THREE(11120, 3, 11122),
    TWO(11122, 2, 11124),
    ONE(11124, 1, 11126),
    UNCHARGED(11126, 0, -1);

    private final int id, charges, replacementId;

    CombatBracelet(int id, int charges, int replacementId) {
        this.id = id;
        this.charges = charges;
        this.replacementId = replacementId;
    }

    static {
        JeweleryTeleports teleports = new JeweleryTeleports("bracelet", false,
                new JeweleryTeleports.Teleport("todo1", new Bounds(1111, 2222, 1111, 2222, 0)),
                new JeweleryTeleports.Teleport("todo2", new Bounds(1111, 2222, 1111, 2222, 0)),
                new JeweleryTeleports.Teleport("todo3", new Bounds(1111, 2222, 1111, 2222, 0)),
                new JeweleryTeleports.Teleport("todo4", new Bounds(1111, 2222, 1111, 2222, 0)),
                new JeweleryTeleports.Teleport("todo5", new Bounds(1111, 2222, 1111, 2222, 0))
        );
        for(CombatBracelet bracelet : values())
            teleports.register(bracelet.id, bracelet.charges, bracelet.replacementId);
    }

}
