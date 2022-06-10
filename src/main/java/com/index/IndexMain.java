package com.index;

import com.index.chat.ChatModerationHandler;
import com.index.model.future.AutoSaveManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class IndexMain extends TelegramLongPollingBot {

    //public String YummyChannel_CHAT = "-1001604709313";    // YummyChat вроде когда бота переписывал - резервного бота на тех чат переключил
    public String YummyChannel_CHAT = "-1001454322922";    // YummyChat
    public String YummyReChat = "-1001604709313";
    public boolean RESEND = true;
    public boolean d_debug = false;
    @Override
    public void onUpdateReceived(Update update)
    {
        if (d_debug &&
                (update.getMessage() != null && update.getMessage().getFrom() != null && String.valueOf(update.getMessage().getFrom().getId()).equals("499220683")))
        {
            new ChatModerationHandler(update);
            if (!AutoSaveManager.getInstance().sends)
            {
                AutoSaveManager.getInstance().sends = true;
                SendAnswer(YummyChannel_CHAT, "Index", "Бот запущен в режиме отладки. Сообщения обрабатываются только от пользователя @MrKirill1232.");
            }
        }
        else if (!d_debug)
        {
            if (!AutoSaveManager.getInstance().sends)
            {
                AutoSaveManager.getInstance().sends = true;
                SendAnswer(YummyChannel_CHAT, "Index", "Бот запущен в обычном режиме.");
            }
            new ChatModerationHandler(update);
        }
    }

    public void SendAnswer(String ChatID, String userName, String text)
    {
        SendAnswer(ChatID, userName, text, "null", 0);
    }

    public void SendAnswer(String ChatID, String userName, String text, String syntaxis, int ReplyOn)
    {
        SendMessage message = new SendMessage();
        switch (syntaxis)
        {
            case "Markdown" -> message.enableMarkdown(true);
            case "HTML" -> message.enableHtml(true);
            case "null" ->
            {
                message.enableMarkdown(false);
                message.enableHtml(false);
                message.enableMarkdownV2(false);
            }
        }
        message.setChatId(ChatID);
        message.setText(text);
        if (ReplyOn != (0)){
            message.setReplyToMessageId(ReplyOn);
        }
        try
        {
            execute(message);
        } catch (Exception e)
        {
            //TelegramApiException
            System.err.println("Ошибка при выполнении " + getClass().getSimpleName() + " SendAnswer\n" + e);
        }
    }

    public void deleteMessage(String chatID, long messageID) {
        deleteMessage(chatID, String.valueOf(messageID));
    }
    public void deleteMessage(String chatID, String messageID)
    {
        DeleteMessage delete = new DeleteMessage();
        delete.setChatId(chatID);
        delete.setMessageId(Integer.parseInt(messageID));
        try
        {
            execute(delete);
        }
        catch (Exception ignored)
        {
            //TelegramApiException
        }
    }
    @Override
    public String getBotUsername()
    {
        return Config.BOT_NAME;
    }

    @Override
    public String getBotToken()
    {
        return Config.BOT_TOKEN;
    }

}

