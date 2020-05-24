package io.ruin.model.stat;

import io.ruin.api.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public enum StatType {

    Attack(1,
            -1, 10.0,
            9747, 9748, 9749
    ),
    Defence(5,
            -1, 10.0,
            9753, 9754, 9755
    ),
    Strength(2,
            -1, 10.0,
            9750, 9751, 9752
    ),
    Hitpoints(6,
            -1, 10.0,
            9768, 9769, 9770
    ),
    Ranged(3,
            -1, 10.0,
            9756, 9757, 9758
    ),
    Prayer(7,
            25, 10.0,
            9759, 9760, 9761
    ),
    Magic(4,
            -1, 10.0,
            9762, 9763, 9764
    ),
    Cooking(16,
            10.0, 10.0,
            9801, 9802, 9803
    ),
    Woodcutting(18,
            10.0, 10.0,
            9807, 9808, 9809
    ),
    Fletching(19,
            10.0, 10.0,
            9783, 9784, 9785
    ),
    Fishing(15,
            10.0, 10.0,
            9798, 9799, 9800
    ),
    Firemaking(17,
            10.0, 10.0,
            9804, 9805, 9806
    ),
    Crafting(11,
            10.0, 10.0,
            9780, 9781, 9782
    ),
    Smithing(14,
            10.0, 10.0,
            9795, 9796, 9797
    ),
    Mining(13,
            10.0, 10.0,
            9792, 9793, 9794
    ),
    Herblore(9,
            10.0, 10.0,
            9774, 9775, 9776
    ),
    Agility(8,
            15.0, 10.0,
            9771, 9772, 9773
    ),
    Thieving(10,
            10.0, 10.0,
            9777, 9778, 9779
    ),
    Slayer(20,
            15.0, 15.0,
            9786, 9787, 9788
    ),
    Farming(21,
            10.0, 10.0,
            9810, 9811, 9812
    ),
    Runecrafting(12,
            15.0, 10.0,
            9765, 9766, 9767
    ),
    Hunter(23,
            10.0, 10.0,
            9948, 9949, 9950
    ),
    Construction(22,
            10.0, 10.0,
            9789, 9790, 9791
    );

    public final int clientId;

    public final double defaultXpMultiplier, after99XpMultiplier;

    public final int regularCapeId, trimmedCapeId, hoodId;

    public final String descriptiveName;

    StatType(int clientId, double defaultXpMultiplier, double after99XpMultiplier, int regularCapeId, int trimmedCapeId, int hoodId) {
        this.clientId = clientId;
        this.defaultXpMultiplier = defaultXpMultiplier;
        this.after99XpMultiplier = after99XpMultiplier;
        this.regularCapeId = regularCapeId;
        this.trimmedCapeId = trimmedCapeId;
        this.hoodId = hoodId;

        String name = name();
        if(StringUtils.vowelStart(name))
            descriptiveName = "an " + name;
        else
            descriptiveName = "a " + name;
    }

    /**
     * Get by id
     */

    public static StatType get(int id) {
        return values()[id];
    }

    /**
     * Get by name
     */

    public static StatType get(String name) {
        for(StatType type : values()) {
            if(name.equalsIgnoreCase(type.name()))
                return type;
        }
        switch(name.toLowerCase()) {
            case "att":
            case "atk":
                return Attack;
            case "str":
                return Strength;
            case "def":
                return Defence;
            case "hp":
                return Hitpoints;
            case "wc":
                return Woodcutting;
            case "rc":
                return Runecrafting;
            case "fm":
                return Firemaking;
            case "con":
                return Construction;
        }
        return null;
    }

    /**
     * Client order
     */

    public static final StatType[] CLIENT_ORDER;

    static {
        ArrayList<StatType> list = new ArrayList<>(Arrays.asList(values()));
        list.sort(Comparator.comparingInt(s -> s.clientId));
        CLIENT_ORDER = list.toArray(new StatType[list.size()]);
    }

}
