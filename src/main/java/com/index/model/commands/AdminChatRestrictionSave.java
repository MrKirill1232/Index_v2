package com.index.model.commands;

import com.index.Params;
import com.index.data.sql.BannedFileInfo;
import com.index.data.sql.ChatInfo;
import com.index.model.holders.UpdateVariables;
import com.index.sends.SendMessageMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AdminChatRestrictionSave
{
    public AdminChatRestrictionSave(Update update)
    {
        UpdateVariables variables = new UpdateVariables(update);
        if (variables.getMessage() == null)
        {
            return;
        }
        variables.getUpdateChat().getRestrictionMedia().storeMe(variables.getUpdateChat().getChatID());
        String[] splitMessage = variables.getUpdateMessage().split(" ");
        if (variables.getUpdateChat().getChatID().equals(Params.CHAT_YUMMY_CHAT_TECH) && splitMessage.length != 1)
        {
            variables.getUpdateChat().getRestrictionMedia().storeMe(Params.CHAT_YUMMY_CHAT);
        }
    }
}
