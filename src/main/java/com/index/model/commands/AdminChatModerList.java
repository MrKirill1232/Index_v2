package com.index.model.commands;

import com.index.data.sql.UserInfo;
import com.index.enums.ParseMode;
import com.index.model.holders.Chat;
import com.index.model.holders.UpdateVariables;
import com.index.model.holders.User;
import com.index.sends.SendMessageMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class AdminChatModerList
{
    public AdminChatModerList(Update update)
    {
        UpdateVariables variables = new UpdateVariables(update);
        if (variables.getUpdateChat() == null)
        {
            return;
        }
        Chat chat = variables.getUpdateChat();
        StringBuilder message = new StringBuilder();
        List<String> moders = chat.getUserModeration();
        if (moders == null || moders.isEmpty())
        {
            new SendMessageMethod(0, chat.getChatID(), message.append("список пуст;").toString(), variables.getMessageID());
            return;
        }
        for (String userID : moders)
        {
            User user = UserInfo.getInstance().getUser(chat.getChatID(), userID);
            if (user != null)
            {
                message.append("<a href=\"tg://user?id=").append(user.getUserID()).append("\">").append(user.getUserName()).append("</a>");
            }
            else
            {
                message.append(userID);
            }
            if (moders.lastIndexOf(userID) != moders.size() - 1)
            {
                message.append(", ");
            }
        }
        new SendMessageMethod(0, chat.getChatID(), message.toString(), variables.getMessageID(), ParseMode.HTML, null, false, false, true);
    }
}
