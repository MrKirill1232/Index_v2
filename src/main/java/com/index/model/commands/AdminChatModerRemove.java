package com.index.model.commands;

import com.index.data.sql.ChatInfo;
import com.index.model.holders.Chat;
import com.index.model.holders.UpdateVariables;
import com.index.model.holders.User;
import com.index.sends.SendMessageMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AdminChatModerRemove
{
    public AdminChatModerRemove(Update update)
    {
        UpdateVariables variables = new UpdateVariables(update);
        if (variables.getUpdateChat() == null)
        {
            return;
        }
        Chat chat = variables.getUpdateChat();
        if (variables.getReplyUser() == null)
        {
            new SendMessageMethod(0, chat.getChatID(), getClass().getSimpleName() + ": Произошла ошибка, не могу найти пользователя. Повторите выполнение команды, ответив на сообщение пользователя;");
            return;
        }
        String chatID = chat.getChatID();
        User replyUser = variables.getReplyUser();
        chat.removeUserModeration(chatID);
        ChatInfo.getInstance().storeMe(chatID);
        StringBuilder message = new StringBuilder(getClass().getSimpleName());
        if (chat.getUserModeration().contains(replyUser.getUserID()))
        {
            message.append(": у пользователя ").append(replyUser.getUserName()).append(" все еще остаются права пользовательской модерации;");
        }
        else
        {
            message.append(": у пользователя ").append(replyUser.getUserName()).append(" более нет прав пользовательской модерации;");
        }
        new SendMessageMethod(0, chatID, message.toString());
    }
}
