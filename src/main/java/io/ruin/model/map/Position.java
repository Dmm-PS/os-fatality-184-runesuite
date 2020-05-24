package io.ruin.model.map;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Position {

    @Expose private int x, y, z;

    private int firstChunkX, firstChunkY;

    public Position(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        updateFirstChunk();
    }

    public void updateFirstChunk() {
        firstChunkX = x >> 3;
        firstChunkY = y >> 3;
    }

    public boolean updateRegion() {
        int diffX = firstChunkX - getChunkX();
        int diffY = firstChunkY - getChunkY();
        int size = Region.CLIENT_SIZE;
        int updateSize = ((size >> 3) / 2) - 1;
        if(Math.abs(diffX) >= updateSize || Math.abs(diffY) >= updateSize) {
            updateFirstChunk();
            return true;
        }
        return false;
    }

    public Position copy() {
        return new Position(x, y, z);
    }

    public void copy(Position other) {
        x = other.getX();
        y = other.getY();
        z = other.getZ();
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Position translate(int changeX, int changeY, int changeZ) {
        x += changeX;
        y += changeY;
        z += changeZ;
        return this;
    }

    public Position translate(int changeX, int changeY) {
        x += changeX;
        y += changeY;
        return this;
    }


    public Position relative(int changeX, int changeY, int changeZ) {
        return copy().translate(changeX, changeY, changeZ);
    }

    public Position relative(int changeX, int changeY) {
        return relative(changeX, changeY, 0);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getFirstChunkX() {
        return firstChunkX;
    }

    public int getFirstChunkY() {
        return firstChunkY;
    }

    public int getChunkX() {
        return x >> 3;
    }

    public int getChunkY() {
        return y >> 3;
    }

    public int getLocalX() {
        return x - 8 * (getChunkX() - 6);
    }

    public int getLocalY() {
        return y - 8 * (getChunkY() - 6);
    }

    public int getBaseLocalX() {
        return x - 8 * (firstChunkX - 6);
    }

    public int getBaseLocalY() {
        return y - 8 * (firstChunkY - 6);
    }

    public int getSceneX() {
        return x - ((getChunkX() - 6) << 3);
    }

    public int getSceneY() {
        return y - ((getChunkY() - 6) << 3);
    }

    public int getSceneX(Position pos) {
        return x - ((pos.getFirstChunkX() - 6) << 3);
    }

    public int getSceneY(Position pos) {
        return y - ((pos.getFirstChunkY() - 6) * 8);
    }

    public int getLocalX(int baseRegionX) {
        return x - 8 * (baseRegionX - 6);
    }

    public int getLocalY(int baseRegionY) {
        return y - 8 * (baseRegionY - 6);
    }

    public static int getLocal(int abs, int chunk) {
        return abs - 8 * (chunk - 6);
    }

    public int getTileHash() {
        return y + (x << 14) + (z << 28);
    }

    public int getRegionHash() {
        return (y >> 13) + ((x >> 13) << 8) + (z << 16);
    }

    public Region getRegion() {
        return Region.get(x, y);
    }

    public int regionId() {
        return ((x >> 6) << 8 | y >> 6);
    }

    public Tile getTile() {
        return Tile.get(x, y, z, true);
    }

    public boolean isWithinDistance(Position other) {
        return isWithinDistance(other, 14);
    }

    public boolean isWithinDistance(Position other, int distance) {
        return isWithinDistance(other, true, distance);
    }

    public boolean isWithinDistance(Position other, boolean checkHeight, int distance) {
        return (!checkHeight || other.z == z) && Math.abs(x - other.x) <= distance && Math.abs(y - other.y) <= distance;
    }

    public boolean inBounds(Bounds bounds) {
        return bounds.inBounds(x, y, z, 0);
    }

    public boolean inBounds(Bounds bounds, int range) {
        return bounds.inBounds(x, y, z, range);
    }

    public boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }

    public boolean equals(int x, int y, int z) {
        return this.x == x && this.y == y && this.z == z;
    }

    public boolean equals(Position pos) {
        return pos.x == x && pos.y == y && pos.z == z;
    }

    public List<Position> area(int radius, Predicate<Position> filter) {
        List<Position> list = new ArrayList<>((int)Math.pow((1 + radius), 2));
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= +radius; y++) {
                Position pos = relative(x, y);
                if (filter.test(pos))
                    list.add(pos);
            }
        }
        return list;
    }

    public List<Position> area(int radius) {
        return area(radius, p -> true);
    }

    public int unitVectorX(Position target) {
        int diff = target.getX() - getX();
        if (diff != 0)
            diff /= Math.abs(diff);
        return diff;
    }

    public int unitVectorY(Position target) {
        int diff = target.getY() - getY();
        if (diff != 0)
            diff /= Math.abs(diff);
        return diff;
    }

    @Override
    public String toString() {
        return "[x=" + x + ", y=" + y + ", z=" + z + "]";
    }

    public Position localPosition() { // this feels really wrong but i dont wanna create another class...
        return new Position(getX() & 63, getY() & 63, getZ());
    }

    public int distance(Position position) {
        int dx = position.getX() - x;
        int dz = position.getY() - y;
        return (int) Math.sqrt(dx * dx + dz * dz);
    }

    public int component1() {
        return x;
    }

    public int component2() {
        return y;
    }

    public int component3() {
        return z;
    }
}
