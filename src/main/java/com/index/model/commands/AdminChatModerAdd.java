package com.index.model.commands;

import com.index.data.sql.ChatInfo;
import com.index.model.holders.Chat;
import com.index.model.holders.UpdateVariables;
import com.index.model.holders.User;
import com.index.sends.SendMessageMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AdminChatModerAdd
{
    public AdminChatModerAdd(Update update)
    {
        UpdateVariables variables = new UpdateVariables(update);
        if (variables.getUpdateChat() == null)
        {
            // new SendMessageMethod(0, chatID, getClass().getSimpleName() + ": Произошла ошибка, не могу найти чат. Повторите выполнение команды, после добавления чата в базу данных;");
            return;
        }
        String chatID = variables.getUpdateChat().getChatID();
        if (variables.getReplyUser() == null)
        {
            new SendMessageMethod(0, chatID, getClass().getSimpleName() + ": Произошла ошибка, не могу найти пользователя. Повторите выполнение команды, ответив на сообщение пользователя;");
            return;
        }
        Chat chat = variables.getUpdateChat();
        User replyUser = variables.getReplyUser();
        chat.addToUserModeration(replyUser.getUserID());
        if (chat.isNeedToStore())
        {
            ChatInfo.getInstance().storeMe(chatID);
        }
        StringBuilder message = new StringBuilder(getClass().getSimpleName());
        message.append(": пользователь ").append(replyUser.getUserName()).append(" получил права ");
        if (chat.getUserModeration().contains(replyUser.getUserID()))
        {
            message.append("модератора;");
        }
        else
        {
            message.append("пользователя;");
        }
        new SendMessageMethod(0, chatID, message.toString());
    }
}
