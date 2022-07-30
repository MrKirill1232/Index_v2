package com.index.sends;

import com.index.IndexMain;
import com.index.MrKirill1232IndexMain;
import com.index.enums.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class SendMessageMethod
{
    private final SendMessage _sendMessage = new SendMessage();

    public SendMessageMethod(Update update)
    {
        new SendMessageMethod(0, String.valueOf(update.getMessage().getChatId()), "response");
    }

    public SendMessageMethod(int bot, String chatID, String text)
    {
        new SendMessageMethod(bot, chatID, text, null, null, null, false, false, false, false);
    }

    public SendMessageMethod(int bot, String chatID, String text, String replyMessage)
    {
        new SendMessageMethod(bot, chatID, text, replyMessage, null, null, false, false, false, false);
    }

    public SendMessageMethod(int bot, String chatID, String text, String replyMessage, ParseMode parseMode)
    {
        new SendMessageMethod(bot, chatID, text, replyMessage, parseMode, null, false, false, false, false);
    }

    public SendMessageMethod(int bot, String chatID, String text, String replyMessage, ParseMode parseMode, ReplyKeyboardMarkup replyKeyboardMarkup)
    {
        new SendMessageMethod(bot, chatID, text, replyMessage, parseMode, replyKeyboardMarkup, false, false, false, false);
    }

    public SendMessageMethod(int bot, String chatID, String text, String replyMessage, ParseMode parseMode, ReplyKeyboardMarkup replyKeyboardMarkup, boolean disableWEBPreview)
    {
        new SendMessageMethod(bot, chatID, text, replyMessage, parseMode, replyKeyboardMarkup, disableWEBPreview, false, false, false);
    }

    public SendMessageMethod(int bot, String chatID, String text, String replyMessage, ParseMode parseMode, ReplyKeyboardMarkup replyKeyboardMarkup, boolean disableWEBPreview, boolean allowSendingWithoutReply)
    {
        new SendMessageMethod(bot, chatID, text, replyMessage, parseMode, replyKeyboardMarkup, disableWEBPreview, allowSendingWithoutReply, false, false);
    }

    public SendMessageMethod(int bot, String chatID, String text, String replyMessage, ParseMode parseMode, ReplyKeyboardMarkup replyKeyboardMarkup, boolean disableWEBPreview, boolean allowSendingWithoutReply, boolean disableNotifications)
    {
        new SendMessageMethod(bot, chatID, text, replyMessage, parseMode, replyKeyboardMarkup, disableWEBPreview, allowSendingWithoutReply, disableNotifications, false);
    }

    public SendMessageMethod(int bot, String chatID, String text, String replyMessage, ParseMode parseMode, ReplyKeyboardMarkup replyKeyboardMarkup, boolean disableWEBPreview, boolean allowSendingWithoutReply, boolean disableNotifications, boolean isProtectedContent)
    {
        _sendMessage.setChatId(chatID);
        _sendMessage.setText(text);
        if (replyMessage != null && !replyMessage.isBlank() && !replyMessage.isEmpty())
        {
            _sendMessage.setReplyToMessageId(Integer.parseInt(replyMessage));
        }
        if (parseMode != null && parseMode.getTelegramType() != null)
        {
            _sendMessage.setParseMode(parseMode.getTelegramType());
        }
        if (replyKeyboardMarkup != null)
        {
            _sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        _sendMessage.setDisableWebPagePreview(disableWEBPreview);
        _sendMessage.setAllowSendingWithoutReply(allowSendingWithoutReply);
        if (disableNotifications)
        {
            _sendMessage.disableNotification();
        }
        _sendMessage.setProtectContent(isProtectedContent);

        executeMessage(bot);
    }

    private void executeMessage(int bot)
    {
        try
        {
            switch (bot)
            {
                case 0:
                {
                    new IndexMain().executeAsync(_sendMessage);
                    break;
                }
                case 1:
                {
                    new MrKirill1232IndexMain().executeAsync(_sendMessage);
                    break;
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
