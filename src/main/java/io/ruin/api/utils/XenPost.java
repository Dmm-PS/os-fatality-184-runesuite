package io.ruin.api.utils;

import java.util.Map;

public class XenPost {

    private static final String URL = "https://osfatality.com/extra/xenforo/index.php";

    private static final String AUTH = "9fD4wgXfQuTnpfWhBRh4M6AYA9xVPBhxe9xXFDZfBw";

    public static String post(String file, Map<Object, Object> map) {
        map.put("auth", AUTH);
        map.put("file", file);
        return PostWorker.postArray(URL, map);
    }

}
