package com.index.model.events;

import com.index.data.sql.ChatInfo;
import com.index.data.sql.ReportTicketsInfo;
import com.index.data.sql.UserInfo;
import com.index.model.holders.Chat;
import com.index.model.holders.ReportTicketHolder;
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
        // String userID = updateQuery.getMessage().getSenderChat() == null ? String.valueOf(updateQuery.getMessage().getFrom().getId()) : String.valueOf(updateQuery.getMessage().getSenderChat().getId());
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
        if (dataMessage.startsWith("reportticket;"))
        {
            if (getAdminPermissions())
            {
                if (dataMessage.startsWith("reportticket;forwardReportMessage_") || dataMessage.startsWith("reportticket;forwardRequestMessage_"))
                {
                    ReportTicketsInfo.getInstance().tryForwardMessage(update);
                }
                else if (dataMessage.startsWith("reportticket;close_"))
                {
                    ReportTicketsInfo.getInstance().closeRequest(update);
                }
                else if (dataMessage.startsWith("reportticket;update_"))
                {
                    ReportTicketsInfo.getInstance().updateButtons(update);
                }
                else if (dataMessage.startsWith("reportticket;forwardReportUser_") || dataMessage.startsWith("reportticket;forwardRequestUser_"))
                {
                    ReportTicketsInfo.getInstance().getUserFromRequest(update);
                }
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
