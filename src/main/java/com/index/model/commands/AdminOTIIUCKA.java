package com.index.model.commands;

import com.index.sends.SendMessageMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AdminOTIIUCKA
{
    public AdminOTIIUCKA(Update update)
    {
        StringBuilder message = new StringBuilder("Привет, я отписка и я отвечу на все твои вопросы, которые мы любим здесь обсуждать :)\n");
        message.append("1. .com / .club / .info - только к этим доменам мы имеем непосредственное отношение. Просьба удалить сообщение.\n");
        message.append("2. Списки с просмотренными аниме... Все файлы, которые были на серверах хранятся в виде сжатого бекапа и чтобы получить доступ к ним - нужно разворачивать серверные бекапы - а это много информации.\n");
        message.append("Нет, нельзя получить списки в данный момент времени. Ни для кого. Даже wayback.machine не может вам их восстановить.\n");
        message.append("3. Сайт закрыт на время военных действий или до окончания СВО. Если что изменится - сообщим на канале отдельным постом.\n");
        message.append("4. Не нравится обращение администрации? Хочешь внести предложение? Пиши контакту - @REaltair\n");
        String messageID = update.getMessage().getReplyToMessage() != null ? String.valueOf(update.getMessage().getReplyToMessage().getMessageId()) : String.valueOf(update.getMessage().getMessageId());
        new SendMessageMethod(0, String.valueOf(update.getMessage().getChatId()), message.toString(), messageID);
    }
}
