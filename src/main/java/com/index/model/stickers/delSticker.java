package com.index.model.stickers;

import com.index.IndexMain;
import com.index.data.sql.ChatInfo;
import com.index.model.holders.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.StringTokenizer;

public class delSticker {
    IndexMain im = new IndexMain();
    String newmessage = "";

    String chat_id;
    String name;
    String orig_message;
    String sticker_url;

    public delSticker (Update update){
        name = update.getMessage().getFrom().getFirstName();
        chat_id = String.valueOf(update.getMessage().getChatId());
        orig_message = update.getMessage().getText();
        Chat chat = ChatInfo.getInstance().getChat(chat_id);
        final StringTokenizer st = new StringTokenizer(orig_message);
        st.nextToken();
        if (       ( update.getMessage().getReplyToMessage() == null && ( !st.hasMoreTokens() ))
                || ( (update.getMessage().getReplyToMessage() != null && !update.getMessage().getReplyToMessage().hasSticker()) && ( !st.hasMoreTokens() ) )  )
        {
            newmessage = "Для удаления стикер-пака из списка исключений, отправьте команду, " +
                    "ответив на один из стикеров стикер-пака или написав ТОКЕН стикер-пака;";
            im.SendAnswer(chat_id, name, newmessage);
            return;
        }
        else {
            if ( update.getMessage().getReplyToMessage() == null && st.hasMoreTokens() )
            {
                sticker_url = st.nextToken();
                if ( sticker_url == null )
                {
                    newmessage = "Для удаления стикер-пака из списка исключений, отправьте команду,\n" +
                            "ответив на один из стикеров стикер-пака или написав ТОКЕН стикер-пака;";
                    im.SendAnswer(chat_id, name, newmessage);
                    return;
                }
                else if ( sticker_url.startsWith("https://t.me/addstickers/") )
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
                if (chat.removeFromAgreedStickerList(sticker_url))
                {
                    newmessage = "Стрикер-пак " + sticker_url + " удален из списка игнорируемых стикеров;";
                }
                else {
                    newmessage = "АДМИН, ТІ ЧУРКА ЕБАНІАЯ";
                }
            }
        }
        im.SendAnswer(chat_id, name, newmessage);
    }
}
