package com.index.model.commands;

import com.index.data.sql.ChatInfo;
import com.index.enums.ParseMode;
import com.index.model.holders.Chat;
import com.index.model.holders.UpdateVariables;
import com.index.sends.SendMessageMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AdminChatInfo
{
    public AdminChatInfo(Update update)
    {
        UpdateVariables variables = new UpdateVariables(update);
        String chatID = variables.getUpdateChat().getChatID();
        if (chatID == null || chatID.isEmpty() || chatID.isBlank())
        {
            return;
        }
        Chat chatInformation = ChatInfo.getInstance().getChat(chatID);
        if (chatInformation == null)
        {
            return;
        }
        String chatInfo = chatInformation.getChatInfo();
        new SendMessageMethod(0, chatID, chatInfo, variables.getMessageID(), ParseMode.HTML);
    }
}
