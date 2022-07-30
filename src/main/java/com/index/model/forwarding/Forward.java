package com.index.model.forwarding;

import com.index.IndexMain;
import com.index.MrKirill1232IndexMain;
import com.index.Params;
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
        if (type.equalsIgnoreCase("forwarding"))
        {
            forwardFOX(update);
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

    private void forwardFOX(Update update)
    {
        ForwardMessage re = new ForwardMessage();
        re.setMessageId(update.getMessage().getMessageId());
        re.setChatId(String.valueOf(im.YummyChannel_CHAT));
        re.setFromChatId("-1001750517257");
        re.setDisableNotification(true);
        try {
            im.execute(re);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
        new MrKirill1232IndexMain().SendAnswer(im.YummyReChat, "Index", message, "HTML", 0);
        if (update.getMessage().getNewChatMembers().stream().findFirst().isPresent())
        {
            new MrKirill1232IndexMain().SendAnswer(im.YummyReChat, "Index", "Присоединился к чату Yummy Anime");
        }
        else
        {
            ForwardMessage re = new ForwardMessage();
            re.setMessageId(update.getMessage().getMessageId());
            re.setChatId(String.valueOf(im.YummyReChat));
            re.setFromChatId(String.valueOf(im.YummyChannel_CHAT));
            re.setDisableNotification(true);
            try
            {
                im.execute(re);
            } catch (TelegramApiException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void Translate (Update update)
    {
        if (String.valueOf(update.getMessage().getFrom().getId()).equals(Params.USER_ALTAIR))
        {
            return;
        }
        new Send(update, "Meow");
    }
}
