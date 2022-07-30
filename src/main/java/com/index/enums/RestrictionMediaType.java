package com.index.enums;

public enum RestrictionMediaType {
    PHOTO,
    GIF,
    STICKER,
    VIDEO,
    BOT,
    FORWARD;

    public static RestrictionMediaType getRestrictionMediaType(int id)
    {
        return values()[id];
    }

}
