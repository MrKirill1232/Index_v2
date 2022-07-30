package com.index.model.commands;

import com.index.IndexMain;
import com.index.Params;
import com.index.data.sql.ChatInfo;
import com.index.model.holders.UpdateVariables;
import com.index.sends.SendMessageMethod;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

import java.util.List;

public class AdminChatSetAdmin
{
    public AdminChatSetAdmin(Update update)
    {
        UpdateVariables uv = new UpdateVariables(update);
        if (uv.getUpdateChat() == null)
        {
            return;
        }
        if (uv.getUpdateUser() != null && !uv.getUpdateUser().getUserID().equals(Params.USER_MRKIRILL1232))
        {
            return;
        }
        List<ChatMember> admins;
        try
        {
            admins = new IndexMain().execute(new GetChatAdministrators(uv.getUpdateChat().getChatID()));
        }
        catch (Exception e)
        {
            new SendMessageMethod(0, uv.getUpdateChat().getChatID(), getClass() + ": Ошибка при получении списка администраторов" + e);
            return;
        }
        for (ChatMember cm : admins)
        {
            uv.getUpdateChat().addToAdminList(String.valueOf(cm.getUser().getId()));
        }
        new SendMessageMethod(0, uv.getUpdateChat().getChatID(), getClass() + ": Администраторы для чата назначены");
    }

}
