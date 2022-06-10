package com.index.chat;

import com.index.IndexMain;
import com.index.enums.RestrictionType;
import com.index.model.holders.Moderation;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

public class UserCommandsHandler {

    IndexMain im = new IndexMain();
    Moderation mod = new Moderation();
    String newmessage;

    String chat_id;
    String original_message;
    String user_id;
    String update_name;

    List<String> user_moderation = new ArrayList<>();

    private void setVariables(Update update){
        Message temp = update.getMessage();
        original_message = temp.getText() != null ? temp.getText() : temp.getCaption() != null ? temp.getCaption() : null;
        user_id = temp.getSenderChat() == null ? String.valueOf(temp.getFrom().getId()) : String.valueOf(temp.getSenderChat().getId());
        update_name = temp.getSenderChat() == null ? temp.getFrom().getFirstName() : temp.getSenderChat().getTitle();
        chat_id = String.valueOf(temp.getChatId());
    }

    public UserCommandsHandler(Update update) {
        setVariables(update);
        if (original_message.startsWith("/mute")){
            mod.getAction(update, true, RestrictionType.MUTE);
        }
    }
}
