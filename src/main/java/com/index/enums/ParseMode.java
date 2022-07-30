package com.index.enums;

public enum ParseMode
{
    NONE(null),
    MARKDOWN_V01(org.telegram.telegrambots.meta.api.methods.ParseMode.MARKDOWN),
    MARKDOWN_V02(org.telegram.telegrambots.meta.api.methods.ParseMode.MARKDOWNV2),
    HTML(org.telegram.telegrambots.meta.api.methods.ParseMode.HTML);

    final String _telegramType;

    private ParseMode(String telegramType)
    {
        _telegramType = telegramType;
    }

    public String getTelegramType()
    {
        return _telegramType;
    }

    public ParseMode getTelegramType(String telegramType)
    {
        for (ParseMode search : values())
        {
            if (search.getTelegramType().equalsIgnoreCase(telegramType.toLowerCase()))
            {
                return search;
            }
        }
        return null;
    }
}
