package io.ruin.model.map;

import io.ruin.Server;
import io.ruin.model.entity.Entity;
import io.ruin.model.entity.player.Player;
import io.ruin.utility.Misc;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Projectile {

    public final int gfxId;

    public final int startHeight, endHeight;

    public final int delay;

    public final int durationStart, durationIncrement;

    public final int curve;

    public final int idk;

    private boolean skipTravel;

    private boolean regionBased;

    private int tileOffset = 0;

    public Projectile(int durationStart, int durationIncrement) {
        this(-1, 0, 0, 0, durationStart, durationIncrement, 0, 0);
    }

    public Projectile(int delay, int durationStart, int durationIncrement) {
        this(-1, 0, 0, delay, durationStart, durationIncrement, 0, 0);
    }

    public Projectile(int gfxId, int startHeight, int endHeight, int delay, int durationStart, int durationIncrement, int curve, int idk) {
        this.gfxId = gfxId;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.delay = delay;
        this.durationStart = durationStart;
        this.durationIncrement = durationIncrement;
        this.curve = curve;
        this.idk = idk;
    }

    public Projectile skipTravel() {
        this.skipTravel = true;
        return this;
    }

    public Projectile regionBased() { //probably not the BEST name lol
        this.regionBased = true;
        return this;
    }

    public int send(Entity entity, Entity target) {
        int entityX = entity.getAbsX();
        int entityY = entity.getAbsY();
        int entitySize = entity.getSize();
        if(entitySize > 1) {
            entityX += entitySize / 2;
            entityY += entitySize / 2;
        }
        int targetX = target.getAbsX();
        int targetY = target.getAbsY();
        int targetSize = target.getSize();
        if(targetSize > 1) {
            targetX += targetSize / 2;
            targetY += targetSize / 2;
        }
        if (tileOffset != 0) {
            int diffX = targetX - entityX;
            if (diffX != 0)
                entityX += tileOffset * (diffX / Math.abs(diffX));
            int diffY = targetY - entityY;
            if (diffY != 0)
                entityY += tileOffset * (diffY / Math.abs(diffY));
        }
        int distance = Misc.getDistance(entityX, entityY, targetX, targetY);
        int duration = this.durationStart + (durationIncrement * Math.max(0, distance - 1));
        if(gfxId == -1)
            return delay + duration;
        int targetIndex = target.getIndex() + 1;
        if(target.player != null)
            targetIndex = -targetIndex;
        Iterable<Player> players = regionBased ? entity.getPosition().getRegion().players : entity.localPlayers();
        for(Player p : players) {
            if(skipTravel)
                p.getPacketSender().sendProjectile(gfxId, targetX, targetY, targetX, targetY, targetIndex, startHeight, endHeight, delay, duration, curve, idk);
            else
                p.getPacketSender().sendProjectile(gfxId, entityX, entityY, targetX, targetY, targetIndex, startHeight, endHeight, delay, duration, curve, idk);
        }
        return delay + duration;
    }

    public int send(Entity entity, int srcX, int srcY, Entity target) {
        int targetX = target.getAbsX();
        int targetY = target.getAbsY();
        int targetSize = target.getSize();
        if(targetSize > 1) {
            targetX += targetSize / 2;
            targetY += targetSize / 2;
        }
        if (tileOffset != 0) {
            int diffX = targetX - srcX;
            if (diffX != 0)
                srcX += tileOffset * (diffX / Math.abs(diffX));
            int diffY = targetY - srcY;
            if (diffY != 0)
                srcY += tileOffset * (diffY / Math.abs(diffY));
        }
        int distance = Misc.getDistance(srcX, srcY, targetX, targetY);
        int duration = this.durationStart + (durationIncrement * Math.max(0, distance - 1));
        if(gfxId == -1)
            return duration;
        int targetIndex = target.getIndex() + 1;
        if(target.player != null)
            targetIndex = -targetIndex;
        Iterable<Player> players = regionBased ? entity.getPosition().getRegion().players : entity.localPlayers();
        for(Player p : players) {
            if(skipTravel)
                p.getPacketSender().sendProjectile(gfxId, targetX, targetY, targetX, targetY, targetIndex, startHeight, endHeight, delay, duration, curve, idk);
            else
                p.getPacketSender().sendProjectile(gfxId, srcX, srcY, targetX, targetY, targetIndex, startHeight, endHeight, delay, duration, curve, idk);
        }
        return delay + duration;
    }


    public int send(Entity entity, Position pos) {
       return send(entity, pos.getX(), pos.getY());
    }



    public int send(Entity entity, int targetX, int targetY) {
        int entityX = entity.getAbsX();
        int entityY = entity.getAbsY();
        int entitySize = entity.getSize();
        if(entitySize > 1) {
            entityX += entitySize / 2;
            entityY += entitySize / 2;
        }
        if (tileOffset != 0) {
            int diffX = targetX - entityX;
            if (diffX != 0)
                entityX += tileOffset * (diffX / Math.abs(diffX));
            int diffY = targetY - entityY;
            if (diffY != 0)
                entityY += tileOffset * (diffY / Math.abs(diffY));
        }
        int distance = Misc.getDistance(entityX, entityY, targetX, targetY);
        int duration = this.durationStart + (durationIncrement * Math.max(0, distance - 1));
        if(gfxId == -1)
            return duration;
        int targetIndex = 0;
        Iterable<Player> players = regionBased ? entity.getPosition().getRegion().players : entity.localPlayers();
        for(Player p : players) {
            if(skipTravel)
                p.getPacketSender().sendProjectile(gfxId, targetX, targetY, targetX, targetY, targetIndex, startHeight, endHeight, delay, duration, curve, idk);
            else
                p.getPacketSender().sendProjectile(gfxId, entityX, entityY, targetX, targetY, targetIndex, startHeight, endHeight, delay, duration, curve, idk);
        }
        return duration;
    }

    public int send(Position src, Position dest) {
        return send(src.getX(), src.getY(), dest.getX(), dest.getY());
    }

    public int send(int startX, int startY, int destX, int destY) {
        int duration = durationStart + (durationIncrement * Math.max(0, Misc.getDistance(startX, startY, destX, destY) - 1));
        for(Player p : Region.get(startX, startY).players)
            p.getPacketSender().sendProjectile(gfxId, startX, startY, destX, destY, 0, startHeight, endHeight, delay, duration, curve, idk);
        return duration;
    }

    /**
     * Misc
     */

    public static Projectile[] arrow(int gfxId) {
        return new Projectile[]{
                new Projectile(gfxId, 40, 36, 41, 51, 5, 15, 11),   //regular
                new Projectile(gfxId, 40, 36, 41, 51, 5, 5, 11),    //dark bow arrow 1
                new Projectile(gfxId, 40, 36, 41, 65, 10, 25, 11),  //dark bow arrow 2
        };
    }

    public static Projectile thrown(int gfxId, int idk) {
        return new Projectile(gfxId, 40, 36, 32, 37, 5, 15, idk);
    }

    public static Projectile[] javelin(int gfxId) {
        return new Projectile[]{
                new Projectile(gfxId, 38, 36, 42, 50, 2, 1, 120), //regular
                new Projectile(gfxId, 38, 36, 49, 52, 3, 1, 120), //special
        };
    }

    public static final Projectile BOLT = new Projectile(27, 38, 36, 41, 51, 5, 5, 11);

    public static final Projectile DRAGON_BOLT = new Projectile(1468, 38, 36, 41, 51, 5, 5, 11);

    /**
     * Printing (Must be dumped from RS)
     */

    public static void print(int gfxId) {
        Server.dumpsDb.execute(con -> {
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM projectile_dumps WHERE gfx_id = " + gfxId);
            Map<Projectile, Integer[]> projectiles = new HashMap<>();
            while(resultSet.next()) {
                int distX = resultSet.getInt("dist_x");
                int distY = resultSet.getInt("dist_y");
                int startHeight = resultSet.getInt("start_height");
                int endHeight = resultSet.getInt("end_height");
                int delay = resultSet.getInt("delay");
                int duration = resultSet.getInt("duration");
                int curve = resultSet.getInt("curve");
                int idk = resultSet.getInt("idk");

                Projectile projectile = null;
                Integer[] durations = null;
                for(Map.Entry<Projectile, Integer[]> entry : projectiles.entrySet()) {
                    Projectile p = entry.getKey();
                    Integer[] d = entry.getValue();
                    if(p.startHeight == startHeight && p.endHeight == endHeight
                            && p.delay == delay && p.curve == curve && p.idk == idk) {
                        projectile = p;
                        durations = d;
                        break;
                    }
                }
                if(projectile == null) {
                    projectile = new Projectile(gfxId, startHeight, endHeight, delay, -1, -1, curve, idk);
                    durations = new Integer[50]; //50 is just a random high number distance will never exceed lol
                    projectiles.put(projectile, durations);
                }
                //duration seems to be affected by both x/y rather than just "distance"..
                //oh well this is close enough for now! lol.
                int distance = Math.max(Math.abs(distX), Math.abs(distY));
                durations[distance] = duration;
            }
            if(projectiles.isEmpty()) {
                System.err.println("Projectile with gfx " + gfxId + " not dumped!");
                return;
            }
            projectiles.forEach(Projectile::print);
        });
    }

    private static void print(Projectile projectile, Integer[] durations) {
        List<Integer> hashes = new ArrayList<>();
        for(int distance = 0; distance < durations.length; distance++) {
            Integer duration = durations[distance];
            if(duration == null)
                continue;
            hashes.add(distance << 16 | duration);
        }
        int size = hashes.size();
        int durationStart, durationIncrement;
        boolean skipTravel;
        if(size == 1) {
            //we need more data!
            System.err.println("Projectile with gfx " + projectile.gfxId + ": (Not enough distances dumped, guessing durations...)");
            durationStart = 56;
            durationIncrement = 10;
            skipTravel = false;
        } else {
            int firstHash = hashes.get(0);
            int firstDistance = firstHash >> 16;
            int firstDuration = firstHash & 0xffff;

            int lastHash = hashes.get(size - 1);
            int lastDistance = lastHash >> 16;
            int lastDuration = lastHash & 0xffff;

            if((skipTravel = (firstDistance == 0 && lastDistance == 0))) {
                System.err.println("Projectile with gfx " + projectile.gfxId + ": (Projectile skips travel, guessing durations...)");
                durationStart = 56;
                durationIncrement = 10;
            } else {
                System.err.println("Projectile with gfx " + projectile.gfxId + ":");
                int increment = (lastDuration - firstDuration) / (lastDistance - firstDistance);
                int distance = firstDistance;
                int duration = firstDuration;
                while(distance != 1) {
                    if(distance > 1) {
                        distance--;
                        duration -= increment;
                    } else {
                        distance++;
                        duration += increment;
                    }
                }
                durationStart = duration;
                durationIncrement = increment;
            }
        }
        System.err.println("    public static final Projectile PROJECTILE = new Projectile(" + projectile.gfxId + ", " + projectile.startHeight + ", " + projectile.endHeight + ", " + projectile.delay + ", " + durationStart + ", " + durationIncrement + ", " + projectile.curve + ", " + projectile.idk + ")" + (skipTravel ? ".skipTravel()" : "") + ";");
    }

    public Projectile tileOffset(int tileOffset) {
        this.tileOffset = tileOffset;
        return this;
    }

}