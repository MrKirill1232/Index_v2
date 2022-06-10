package com.index.model.forwarding;

import com.index.IndexMain;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Forward {

    IndexMain im = new IndexMain();

    public Forward(String type, boolean isActual, Update update)
    {
        if (!isActual)
        {
            return;
        }
        if (type.equalsIgnoreCase("delete"))
        {
            Forwarding(update);
        }
        else if (type.equalsIgnoreCase("translate"))
        {
            Translate(update);
        }
    }

    private void Forwarding (Update update){
        /* forwarding
         * Пересылается все сообщения с yummy чата в чат модератора
         * */
        String message;
        if (update.getMessage().getSenderChat() == null)
        {
            message =
                    "<code>" + update.getMessage().getFrom().getId() + "</code>\n" +
                    "<code>" + update.getMessage().getMessageId().toString() + "</code>";
            if (update.getMessage().getReplyToMessage() != null)
            {
                message += "\n<code>" + update.getMessage().getReplyToMessage().getMessageId() + "</code>";
            }
            message += "\n<code>" + update.getMessage().getFrom().getFirstName() + "</code>" ;
        }
        else
        {
            message =
                    "<code>" + update.getMessage().getSenderChat().getId() + "</code>\n" +
                    "<code>" + update.getMessage().getMessageId().toString() + "</code>";
            if (update.getMessage().getReplyToMessage() != null)
            {
                message += "\n<code>" + update.getMessage().getReplyToMessage().getMessageId() + "</code>";
            }
            message += "\n<code>" + update.getMessage().getSenderChat().getTitle() + "</code>";

        }
        im.SendAnswer(im.YummyReChat, "Index", message, "HTML", 0);
        if (update.getMessage().getNewChatMembers().stream().findFirst().isPresent()){
            im.SendAnswer(im.YummyReChat, "Index", "Присоединился к чату Yummy Anime");
        }
        else
        {
            ForwardMessage re = new ForwardMessage();
            re.setMessageId(update.getMessage().getMessageId());
            re.setChatId(String.valueOf(im.YummyReChat));
            re.setFromChatId(String.valueOf(im.YummyChannel_CHAT));
            re.setDisableNotification(true);
            try {
                im.execute(re);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void Translate (Update update){
        if (update.getMessage().getFrom().getId() == 499220683)
        {
            //im.SendAnswer(im.YummyReChat, "Index", "To Aru Majutsu No Index");
        }
        new Send(update, "Meow");
    }
}
