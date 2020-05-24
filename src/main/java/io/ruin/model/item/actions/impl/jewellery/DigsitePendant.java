package io.ruin.model.item.actions.impl.jewellery;

import io.ruin.model.map.Bounds;

public enum DigsitePendant {

    FIVE(11194, 5, 11193),
    FOUR(11193, 4, 11192),
    THREE(11192, 3, 11191),
    TWO(11191, 2, 11190),
    ONE(11190, 1, -1);

    private final int id, charges, replacementId;

    DigsitePendant(int id, int charges, int replacementId) {
        this.id = id;
        this.charges = charges;
        this.replacementId = replacementId;
    }

    static {
        JeweleryTeleports teleports = new JeweleryTeleports("pendant", false,
                new JeweleryTeleports.Teleport("todo1", new Bounds(1111, 2222, 1111, 2222, 0)),
                new JeweleryTeleports.Teleport("todo2", new Bounds(1111, 2222, 1111, 2222, 0)),
                new JeweleryTeleports.Teleport("todo3", new Bounds(1111, 2222, 1111, 2222, 0)),
                new JeweleryTeleports.Teleport("todo4", new Bounds(1111, 2222, 1111, 2222, 0)),
                new JeweleryTeleports.Teleport("todo5", new Bounds(1111, 2222, 1111, 2222, 0))
        );
        for(DigsitePendant pendant : values())
            teleports.register(pendant.id, pendant.charges, pendant.replacementId);
    }

}

