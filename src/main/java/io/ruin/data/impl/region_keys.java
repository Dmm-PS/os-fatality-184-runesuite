package io.ruin.data.impl;

import io.ruin.api.utils.JsonUtils;
import io.ruin.api.utils.ServerWrapper;
import io.ruin.data.DataFile;
import io.ruin.model.map.MultiZone;
import io.ruin.model.map.Region;
import io.ruin.model.map.dynamic.DynamicMap;

import java.util.Arrays;
import java.util.Map;

public class region_keys implements DataFile {

    @Override
    public String path() {
        return "region_keys.json";
    }

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public Object fromJson(String fileName, String json) {
        Map<Integer, int[]> keys = JsonUtils.fromJson(json, Map.class, Integer.class, int[].class);
        for(int regionId = 0; regionId < Region.LOADED.length; regionId++) {
            Region region = new Region(regionId);
            if((region.keys = keys.get(regionId)) != null && !isValid(region.id, region.keys)) {
                ServerWrapper.logWarning("Invalid Keys for Region (" + regionId + "): base=(" + region.baseX + ", " + region.baseY + ") keys=" + Arrays.toString(region.keys));
                region.keys = null;
            }
            Region.LOADED[regionId] = region;
        }
        for(Region region : Region.LOADED) {
            region.init();
        }
        MultiZone.load();
        DynamicMap.load();
        return keys;
    }

    private static boolean isValid(int id, int[] keys) {
        Region r = new Region(id);
        r.keys = keys;
        try {
            r.getLandscapeData();
            return true;
        } catch(Throwable t) {
            return false;
        }
    }

}