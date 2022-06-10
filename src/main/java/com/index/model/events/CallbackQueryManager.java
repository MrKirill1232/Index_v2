package com.index.model.events;

import com.index.data.sql.ChatInfo;
import com.index.data.sql.UserInfo;
import com.index.model.holders.Chat;
import com.index.model.holders.User;
import com.index.model.stickers.listSticker;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Simple OnCallBackQuery implementation =_=
 */
public class CallbackQueryManager {
    CallbackQuery updateQuery;
    Chat updateChat;
    User updateUser;
    String dataMessage;

    private void setVariables(Update update)
    {
        updateQuery = update.getCallbackQuery();
        if (updateQuery == null) return;
        updateChat = ChatInfo.getInstance().getChat(String.valueOf(updateQuery.getMessage().getChatId()));
        updateUser = UserInfo.getInstance().getUser(updateChat.getChatID(), String.valueOf(updateQuery.getFrom().getId()));
        dataMessage = updateQuery.getData();
    }
    public CallbackQueryManager(Update update)
    {
        if (update == null) return;
        setVariables(update);

        if (dataMessage.startsWith("sticker_list;"))
        {
            if (!isRestricted() && getNormalPermissions())
            {
                new listSticker(update, true);
            }
        }

    }

    // Проверка присутствует ли какое-либо ограничение у пользователя
    public boolean isRestricted()
    {
        return updateUser.getRestrictionTime().getTimeInMillis() > System.currentTimeMillis();
    }
    // Проверка является ли пользователь модератором или администратором
    public boolean getNormalPermissions()
    {
        return getAdminPermissions() || updateChat.getUserModeration().contains(updateUser.getUserID());
    }
    // Проверка является ли пользователь администратором
    public boolean getAdminPermissions()
    {
        return updateChat.getAdminsList().contains(updateUser.getUserID());
    }

}
