package com.index.enums;

public enum RestrictionType {
    NULL,
    MUTE,
    BAN;

    public static RestrictionType getRestrictionType(int id)
    {
        return values()[id];
    }

    public static int getRestrictionType(RestrictionType restrictionType)
    {
        int returnValue = -1;
        for (RestrictionType rt : values())
        {
            if (rt == restrictionType)
            {
                returnValue = rt.ordinal();
                break;
            }
        }
        return returnValue;
    }
}
