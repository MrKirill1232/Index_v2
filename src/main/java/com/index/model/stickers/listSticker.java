package com.index.model.stickers;

import com.index.IndexMain;
import com.index.data.sql.ChatInfo;
import com.index.data.sql.UserInfo;
import com.index.model.holders.Chat;
import com.index.model.holders.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class listSticker {

    Message message;
    Chat chat;
    User user;
    String stickerName;
    String lastSticker;
    boolean isQuery;
    int messageID;
    boolean lastInList = false;
    private void setVariables(Update update)
    {
        message = isQuery ? update.getCallbackQuery().getMessage() : update.getMessage();
        chat = ChatInfo.getInstance().getChat(String.valueOf(message.getChatId()));
        user = UserInfo.getInstance().getUser(chat.getChatID(), String.valueOf(message.getFrom().getId()));
        if (isQuery)
        {
            String[] tokens = update.getCallbackQuery().getData().split(";");
            stickerName = tokens[1];
            messageID = message.getMessageId();
        }
        else /**/ stickerName = "newList";
    }

    public listSticker(Update update, boolean isQuery)
    {
        this.isQuery = isQuery;
        setVariables(update);
        if (isQuery) /**/ editMessage();
        else /**/ sendMessage();
    }

    private InlineKeyboardMarkup getInlineMarkup()
    {
        return new InlineKeyboardMarkup(List.of(
                lastInList ?
                        List.of(generateButton("Начало", "sticker_list;newList")) :
                        List.of(generateButton("Начало", "sticker_list;newList"), generateButton("След.", "sticker_list;" + lastSticker))));
    }

    private void sendMessage()
    {
        SendMessage message = new SendMessage();
        message.setChatId(chat.getChatID());
        message.setText(generateList());
        message.enableHtml(true);
        message.setReplyMarkup(getInlineMarkup());
        try
        {
            new IndexMain().execute(message);
        }
        catch (TelegramApiException ignored)
        {
        }
    }

    private void editMessage()
    {
        EditMessageText message = new EditMessageText();
        message.setChatId(chat.getChatID());
        message.setText(generateList());
        message.setReplyMarkup(getInlineMarkup());
        message.enableHtml(true);
        message.setMessageId(messageID);
        try
        {
            new IndexMain().execute(message);
        }
        catch (TelegramApiException e)
        {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, "Index", " " + e);
        }
    }

    private InlineKeyboardButton generateButton(String text, String data)
    {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(data);
        return button;
    }

    private String generateList()
    {
        List<String> stickerList = chat.getAgreedStickerList();
        if (stickerName.equals("newList")) /**/ isQuery = false;
        if (stickerList.isEmpty()) /**/ return "Список игнорируемых стикеров пуст.";
        if (!stickerList.contains(stickerName) && isQuery) /**/ return "Неправильно составленный запрос;";
        StringBuilder returnList = new StringBuilder("Список разрешённых стикеров:\n");
        int currentIndex = 0;
        int shownIndex = 0;
        boolean check = false;
        for (String sticker : stickerList)
        {
            shownIndex++;
            if (check || !isQuery || sticker.equals(stickerName))
            {
                check = true;
                currentIndex++;
            }
            else /**/ continue;
            lastSticker = sticker;
            returnList.append(shownIndex).append(". ").append(sticker).append(" - <a href=\"https://t.me/addstickers/").append(sticker).append("\">[*клик*]</a>\n");
            if (returnList.length() >= 3500 || currentIndex >= 50 || (!stickerList.isEmpty() && stickerList.lastIndexOf(lastSticker) == stickerList.size() - 1))
            {
                break;
            }
        }
        if (!stickerList.isEmpty() && stickerList.lastIndexOf(lastSticker) == stickerList.size() - 1)
        {
            lastInList = true;
            returnList.append("Достигнут конец списка.");
        }
        return returnList.toString();
    }
}
