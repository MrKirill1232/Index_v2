package com.index.model.commands;

import com.index.data.sql.ChatInfo;
import com.index.data.sql.UserInfo;
import com.index.model.holders.UpdateVariables;
import com.index.sends.SendMessageMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AdminUserSave
{
    public AdminUserSave(Update update)
    {
        UpdateVariables variables = new UpdateVariables(update);
        String chatID = variables.getUpdateChat().getChatID();
        if (!ChatInfo.getInstance().getChatsID().contains(chatID))
        {
            new SendMessageMethod(0, chatID, getClass().getSimpleName() + ": Ошибка, чат не найден в холдере;");
            return;
        }
        UserInfo.getInstance().storeChat(chatID);
        new SendMessageMethod(0, chatID, getClass().getSimpleName() + ": попытка сохранить информацию о пользователях чата;");
    }
}
