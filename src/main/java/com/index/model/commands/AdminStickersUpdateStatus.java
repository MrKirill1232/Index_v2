package com.index.model.commands;

import com.index.model.holders.Chat;
import com.index.model.holders.UpdateVariables;
import com.index.model.holders.User;
import com.index.sends.SendMessageMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AdminStickersUpdateStatus
{
    public AdminStickersUpdateStatus(Update update)
    {
        UpdateVariables variables = new UpdateVariables(update);
        if (variables.getUpdateChat() == null)
        {
            return;
        }
        User user;
        if (variables.getReplyUser() == null)
        {
            if (variables.getUpdateUser() == null)
            {
                new SendMessageMethod(0, variables.getUpdateChat().getChatID(), getClass().getSimpleName() + ": дурашка;");
                return;
            }
            user = variables.getUpdateUser();
        }
        else
        {
            user = variables.getReplyUser();
        }
        user.setIgnoreStickerCheck(!user.getIgnoreStickerCheck());
        new SendMessageMethod(0, variables.getUpdateChat().getChatID(), getClass().getSimpleName() + ": Изменил статус проверки стикеров для пользователя " + user.getUserName() + " на " + user.getIgnoreStickerCheck() + ";");
    }
}
