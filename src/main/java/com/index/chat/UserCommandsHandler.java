package com.index.chat;

import com.index.IndexMain;
import com.index.data.sql.ReportTicketsInfo;
import com.index.enums.RestrictionType;
import com.index.model.holders.Chat;
import com.index.model.holders.Moderation;
import com.index.model.holders.UpdateVariables;
import com.index.model.holders.User;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

public class UserCommandsHandler {

    IndexMain im = new IndexMain();
    Moderation mod = new Moderation();
    String newmessage;

    Chat updateChat;
    String original_message;
    User updateUser;
    User replyUser;

    List<String> user_moderation = new ArrayList<>();

    private void setVariables(Update update)
    {
        UpdateVariables uv = new UpdateVariables(update);
        if (uv.getMessage() == null)
        {
            return;
        }
        Message message = uv.getMessage();
        original_message = uv.getUpdateMessage();
        updateChat = uv.getUpdateChat();
        updateUser = uv.getUpdateUser();
        replyUser = message.getReplyToMessage() != null ? uv.getReplyUser() : null;
        user_moderation.addAll(updateChat.getUserModeration());
        user_moderation.addAll(updateChat.getAdminsList());

    }

    public UserCommandsHandler(Update update) {
        setVariables(update);
        if (user_moderation.contains(updateUser.getUserID()))
        {
            if (original_message.startsWith("/mute"))
            {
                mod.getAction(update, true, RestrictionType.MUTE);
            }
        }
        if (original_message.startsWith("/report"))
        {
            ReportTicketsInfo.getInstance().addNewReport(update);
        }
    }
}
