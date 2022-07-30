package com.index.model.stickers;

import com.index.IndexMain;
import com.index.data.sql.ChatInfo;
import com.index.model.holders.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

public class clearSticker
{
    private List<String> returnList = new ArrayList<>();

    public clearSticker(Update update)
    {
        Chat chat = ChatInfo.getInstance().getChat(String.valueOf(update.getMessage().getChatId()));
        List<String> stickers = chat.getAgreedStickerList();
        for (String url : stickers)
        {
            if (returnList.contains(url))
            {
                continue;
            }
            returnList.add(url);
        }
        chat.setAgreedStickers(returnList);
        new IndexMain().SendAnswer(chat.getChatID(), "Index", "Уведомление: Список стикеров был очищен от дубликатов;");
    }
}
