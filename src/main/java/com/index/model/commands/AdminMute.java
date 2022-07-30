package com.index.model.commands;

import com.index.enums.RestrictionType;
import com.index.model.holders.Moderation;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AdminMute
{
    public AdminMute(Update update)
    {
        new Moderation().getAction(update, true, RestrictionType.MUTE);
    }
}
