package com.index;

import com.index.chat.ChatModerationHandler;
import com.index.chat.Commands;
import com.index.model.future.AutoSaveManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MrKirill1232IndexMain extends TelegramLongPollingBot {

    public boolean RESEND = false;
    public boolean d_debug = false;
    @Override
    public void onUpdateReceived(Update update)
    {
        if (update.getMessage() != null && update.getMessage().getText() != null && (update.getMessage().getText().startsWith("/") || update.getMessage().getText().startsWith("//")))
        {
            String command = update.getMessage().getText().split(" ", 2)[0].toLowerCase();
            Commands requestCommand = Commands.getCommandByString(command);
            if (requestCommand != null)
            {
                try
                {
                    requestCommand.makeCommand(update);
                    return;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return;
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
        if (ReplyOn != (0))
        {
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
        return "";
    }

    @Override
    public String getBotToken()
    {
        return ":";
    }

}

