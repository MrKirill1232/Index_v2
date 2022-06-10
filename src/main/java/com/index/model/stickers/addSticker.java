package com.index.model.stickers;

import com.index.IndexMain;
import com.index.data.sql.ChatInfo;
import com.index.model.holders.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.StringTokenizer;

public class addSticker {

    IndexMain im = new IndexMain();
    StringBuilder newMessage = new StringBuilder();

    String chat_id;
    String name;
    String orig_message;
    String sticker_url;

    public addSticker(Update update) {
        name = update.getMessage().getFrom().getFirstName();
        chat_id = String.valueOf(update.getMessage().getChatId());
        orig_message = update.getMessage().getText();
        Chat chat = ChatInfo.getInstance().getChat(chat_id);
        final StringTokenizer st = new StringTokenizer(orig_message, " ");
        st.nextToken();
        if (       ( update.getMessage().getReplyToMessage() == null && ( !st.hasMoreTokens() ))
                || ( (update.getMessage().getReplyToMessage() != null && !update.getMessage().getReplyToMessage().hasSticker()) && ( !st.hasMoreTokens() ) )  )
        {
            newMessage.append("Для добавление стикер-пака в список исключений, " +
                    "отправьте команду, ответив на один из стикеров стикер-пака или написав ТОКЕН стикер-пака;");
            im.SendAnswer(chat_id, name, String.valueOf(newMessage));
            return;
        }
        else
        {
            if ( update.getMessage().getReplyToMessage() == null && st.hasMoreTokens() )
            {
                sticker_url = st.nextToken();
                if ( st.hasMoreTokens() )
                {
                    newMessage.append("Для добавление стикер-пака в список исключений, " +
                            "отправьте команду, ответив на один из стикеров стикер-пака или написав ТОКЕН стикер-пака;");
                    im.SendAnswer(chat_id, name, newMessage.toString());
                    return;
                }
                if ( sticker_url.startsWith("https://t.me/addstickers/") )
                {
                    sticker_url = sticker_url.substring(25);
                }
            }
            else if ( update.getMessage().getReplyToMessage().hasSticker() )
            {
                sticker_url = update.getMessage().getReplyToMessage().getSticker().getSetName();
                im.deleteMessage(chat_id, String.valueOf(update.getMessage().getReplyToMessage().getMessageId()));
            }

            if ( chat.getAgreedStickerList().contains(sticker_url) )
            {
                newMessage.append("Стикер-пак ").append(sticker_url).append(" уже добавлен в список исключений;");
            }
            else if ( chat.addToAgreedStickerList(sticker_url) )
            {
                newMessage.append("Стикер-пак ").append(sticker_url).append(" успешно добавлен в список исключений;");
            }
            else
            {
                newMessage.append("Произошла ошибка при добавлении нового стикер-пака;");
            }
            im.deleteMessage(chat_id, String.valueOf(update.getMessage().getMessageId()));
        }
        im.SendAnswer(chat_id, name, String.valueOf(newMessage));
    }
}
