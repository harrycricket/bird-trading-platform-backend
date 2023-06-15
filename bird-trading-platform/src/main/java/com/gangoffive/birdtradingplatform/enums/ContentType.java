package com.gangoffive.birdtradingplatform.enums;

import java.util.HashMap;

public enum ContentType {
    gif,
    jpeg,
    jpg,
    png,
    tiff,
    ico,
    svg,
    avi,
    mp4,
    mpeg,
    ogv,
    ts,
    webm;
    private static HashMap<ContentType, String> type;
    static {
        type = new HashMap<>();
        type.put(gif, "image/gif");
        type.put(jpeg, "image/jpeg");
        type.put(jpg, "image/jpeg");
        type.put(png, "image/png");
        type.put(tiff, "image/tiff");
        type.put(ico, "image/vnd.microsoft.icon");
        type.put(svg, "image/svg+xml");
        type.put(avi, "video/x-msvideo");
        type.put(mp4, "video/mp4");
        type.put(mpeg, "video/mpeg");
        type.put(ogv, "video/ogg");
        type.put(ts, "video/mp2t");
        type.put(webm, "video/webm");
    }

    public static String getValue(ContentType key) {
        return type.get(key);
    }

    public static void main(String[] args) {
        String contentType = "";
        for (ContentType type: ContentType.values()) {
            if (type.name().equalsIgnoreCase("webm")) {
                contentType = ContentType.getValue(type);
                break;
            }
        }
        System.out.println(contentType);
    }
}
