package com.index.model.holders;

import com.index.enums.RestrictionMediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestrictionMedia {
    private Map<RestrictionMediaType, List<String>> restrictionMedia;

    public RestrictionMedia(Map<RestrictionMediaType, List<String>> restrictionMedia)
    {
        this.restrictionMedia = restrictionMedia;
    }

    public List<String> getRestrictionMedia(RestrictionMediaType restrictionMediaType)
    {
        if (this.restrictionMedia == null || this.restrictionMedia.isEmpty() || this.restrictionMedia.get(restrictionMediaType) == null || this.restrictionMedia.get(restrictionMediaType).isEmpty())
        {
            return null;
        }
        return this.restrictionMedia.get(restrictionMediaType);
    }
    public boolean addToRestrictionMedia(RestrictionMediaType restrictionMediaType, String fileId)
    {
        if (this.restrictionMedia == null) {
            restrictionMedia = new HashMap<>();
        }
        if (this.restrictionMedia.isEmpty() || this.restrictionMedia.get(restrictionMediaType) == null)
        {
            restrictionMedia.put(restrictionMediaType, new ArrayList<>());
        }
        try
        {
            List<String> restrictionIds = restrictionMedia.get(restrictionMediaType);
            if (!restrictionIds.contains(fileId)) restrictionIds.add(fileId);
            return true;
        }
        catch (Exception ex)
        {
            // Send Message
            return false;
        }
    }
    public boolean checkIsInRestrictionMedia(RestrictionMediaType restrictionMediaType, String fileId)
    {
        if (this.restrictionMedia == null || this.restrictionMedia.isEmpty() || this.restrictionMedia.get(restrictionMediaType) == null || this.restrictionMedia.get(restrictionMediaType).isEmpty())
        {
            return false;
        }
        List <String> filesIDs = this.restrictionMedia.get(restrictionMediaType);
        boolean is = filesIDs.contains(fileId);
        return is;
    }
}
