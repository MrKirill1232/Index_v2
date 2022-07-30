package com.index.model.commands;

import com.index.chat.Commands;
import com.index.model.holders.Chat;
import com.index.model.holders.UpdateVariables;
import com.index.model.holders.User;
import com.index.sends.SendMessageMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UserChatHelp
{
    public UserChatHelp(Update update)
    {
        UpdateVariables variables = new UpdateVariables(update);
        if (variables.getUpdateChat() == null)
        {
            return;
        }
        Chat chat = variables.getUpdateChat();
        User user = variables.getReplyUser() != null ? variables.getReplyUser() : variables.getUpdateUser();
        int accessLevel = chat.getAdminsList().contains(user.getUserID()) ? 2 : chat.getUserModeration().contains(user.getUserID()) ? 1 : 0;
        StringBuilder help = new StringBuilder("Список разрешенных команды:\n");
        for (Commands c : Commands.values())
        {
            if (c.getAccessLevel() > accessLevel)
            {
                continue;
            }
            help.append(" * ").append(c.getCommand()).append(" - ").append(c.getComment()).append(" ").append(c.getClazz().getSimpleName()).append(" ").append(c.getAccessLevel());
            help.append("\n");
        }
        new SendMessageMethod(0, chat.getChatID(), help.toString());
    }
}
