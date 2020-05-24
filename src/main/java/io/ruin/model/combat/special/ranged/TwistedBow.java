package io.ruin.model.combat.special.ranged;

import io.ruin.cache.ItemDef;
import io.ruin.model.combat.AttackStyle;
import io.ruin.model.combat.AttackType;
import io.ruin.model.combat.Hit;
import io.ruin.model.combat.HitType;
import io.ruin.model.combat.special.Special;
import io.ruin.model.entity.Entity;
import io.ruin.model.entity.player.Player;
import io.ruin.model.item.Item;
import io.ruin.model.item.containers.Equipment;
import io.ruin.model.map.Projectile;

public class TwistedBow implements Special {

    private static final Projectile[] PROJECTILES = {
            new Projectile(1099, 44, 3, 43, 31, 0, 5, 11),
            new Projectile(1099, 44, 3, 43, 31, 0, 25, 11),
    };

    @Override
    public boolean accept(ItemDef def, String name) {
        return name.contains("twisted bow");
    }

    @Override
    public boolean handle(Player player, Entity target, AttackStyle style, AttackType type, int maxDamage) {
        Item ammo = player.getEquipment().get(Equipment.SLOT_AMMO);
        if (ammo == null || ammo.getAmount() < 2) {
            player.sendMessage("You need at least two arrows in your quiver to use this special attack.");
            return false;
        }
        player.animate(426);
        player.graphics(player.getCombat().rangedData.doubleDrawbackId, 96, 0);

        Hit[] hits = new Hit[PROJECTILES.length];
        for (int i = 0; i < PROJECTILES.length; i++) {
            int delay = PROJECTILES[i].send(player, target);
            Hit hit = new Hit(player, style, type)
                    .randDamage(maxDamage)
                    .boostDamage(0.50)
                    .boostAttack(0.45)
                    .clientDelay(delay);
            hit.postDefend(t -> {
                hit.type = HitType.DAMAGE;
                hit.damage = Math.max(8, hit.damage);
            }).postDamage(t -> t.graphics(1100, 96, 0));
            hits[i] = hit;
        }

        player.getCombat().removeAmmo(ammo, hits);
        target.hit(hits);
        return true;
    }

    @Override
    public int getDrainAmount() {
        return 55;
    }

}