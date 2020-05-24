package io.ruin.model.item.actions.impl;

import io.ruin.api.utils.NumberUtils;
import io.ruin.api.utils.Random;
import io.ruin.api.utils.StringUtils;
import io.ruin.cache.Color;
import io.ruin.model.inter.Interface;
import io.ruin.model.inter.InterfaceHandler;
import io.ruin.model.inter.InterfaceType;
import io.ruin.model.inter.actions.SimpleAction;
import io.ruin.model.item.Item;
import io.ruin.model.item.actions.ItemAction;
import io.ruin.model.stat.StatType;

public enum SkillLamp {

    ATTACK(3, StatType.Attack, true, true),
    STRENGTH(4, StatType.Strength, true, true),
    RANGED(5, StatType.Ranged, true, true),
    MAGIC(6, StatType.Magic, true, true),
    DEFENCE(7, StatType.Defence, true, true),
    HITPOINTS(8, StatType.Hitpoints, true, true),
    PRAYER(9, StatType.Prayer, true, true),
    AGILITY(10, StatType.Agility, false, false),
    HERBLORE(11, StatType.Herblore, false, true),
    THIEVING(12, StatType.Thieving, false, false),
    CRAFTING(13, StatType.Crafting, false, true),
    RUNECRAFTING(14, StatType.Runecrafting, false, true),
    SLAYER(22, StatType.Slayer, false, false),
    FARMING(23, StatType.Farming, false, true),
    MINING(15, StatType.Mining, false, true),
    SMITHING(16, StatType.Smithing, false, true),
    FISHING(17, StatType.Fishing, false, false),
    COOKING(18, StatType.Cooking, false, false),
    FIRE_MAKING(19, StatType.Firemaking, false, true),
    WOODCUTTING(20, StatType.Woodcutting, false, true),
    FLETCHING(21, StatType.Fletching, false, true),
    CONSTRUCTION(24, StatType.Construction, false, true),
    HUNTER(25, StatType.Hunter, false, true);

    private int childId;
    private StatType statType;
    private boolean combat;
    private boolean disabled;

    SkillLamp(int childId, StatType statType, boolean combat, boolean disabled) {
        this.childId = childId;
        this.statType = statType;
        this.combat = combat;
        this.disabled = disabled;
    }

    public static final int SKILL_CAMP = 2528;

    static {
        ItemAction.registerInventory(SKILL_CAMP, "rub", (player, item) -> {
            player.openInterface(InterfaceType.MAIN, Interface.SKILL_LAMP);
            player.getPacketSender().sendSkillinterface("");
        });

        InterfaceHandler.register(Interface.SKILL_LAMP, h -> {
            for (SkillLamp skill : SkillLamp.values()) {
                h.actions[skill.childId] = (SimpleAction) player -> {
                    player.openInterface(InterfaceType.MAIN, Interface.SKILL_LAMP);
                    String skillName = StringUtils.getFormattedEnumName(skill);
                    if (skill.combat) {
                        player.sendMessage(Color.DARK_RED.wrap("You can't add experience to a combat stat!"));
                        return;
                    }
                    if (skill.disabled) {
                        player.sendMessage(Color.DARK_RED.wrap(skillName + " is currently disabled!"));
                        return;
                    }
                    player.sendMessage(Color.DARK_GREEN.wrap("You have selected the skill: " + skillName));
                    player.openInterface(InterfaceType.MAIN, Interface.SKILL_LAMP);
                    player.selectedSkillLampSkill = skill.statType;
                    player.getPacketSender().sendSkillinterface(skillName);
                };
            }
            h.actions[26] = (SimpleAction) player -> {
                Item lamp = player.getInventory().findItem(SKILL_CAMP);
                if (lamp == null)
                    return;

                int experience = Random.get(25000, 50000);
                player.closeInterface(InterfaceType.MAIN);
                lamp.remove();
                player.getStats().addXp(player.selectedSkillLampSkill, experience, false);
                player.sendMessage(Color.DARK_GREEN.wrap("You have been rewarded " + NumberUtils.formatNumber(experience) + " " + player.selectedSkillLampSkill.name() + " experience."));
            };
        });

    }
}
