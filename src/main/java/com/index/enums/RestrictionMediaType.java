package com.index.enums;

public enum RestrictionMediaType {
    PHOTO,
    GIF,
    STICKER,
    VIDEO,
    BOT;

    public static RestrictionMediaType getRestrictionMediaType(int id)
    {
        return values()[id];
    }

}
