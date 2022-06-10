package com.index.model.stickers;

import com.index.IndexMain;
import com.index.model.holders.Chat;

import java.util.ArrayList;
import java.util.List;

public class clearSticker
{
    private List<String> returnList = new ArrayList<>();

    public clearSticker(Chat chat)
    {
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
