package com.index.model.commands;

import com.index.data.sql.UserInfo;
import com.index.enums.ParseMode;
import com.index.model.holders.UpdateVariables;
import com.index.model.holders.User;
import com.index.sends.SendMessageMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AdminUserGet
{
    public AdminUserGet(Update update)
    {
        UpdateVariables variables = new UpdateVariables(update);
        if (variables.getUpdateChat() == null)
        {
            // new SendMessageMethod(0, chatID, getClass().getSimpleName() + ": Произошла ошибка, не могу найти чат. Повторите выполнение команды, после добавления чата в базу данных;");
            return;
        }
        String chatID = variables.getUpdateChat().getChatID();
        String[] splitMessage = variables.getUpdateMessage().split(" ", 3);
        String info = "";
        User user;
        if (splitMessage.length == 1)
        {
            user = variables.getReplyUser() == null ? variables.getUpdateUser() : variables.getReplyUser();
        }
        else
        {
            user = UserInfo.getInstance().getUser(chatID, splitMessage[1]);
            if (user == null)
            {
                info = info + "null\n";
                user = variables.getUpdateUser();
            }
        }
        info = info + user.getAllInfo();
        new SendMessageMethod(0, chatID, info, variables.getMessageID(), ParseMode.HTML);
    }
}
