package com.index.model.commands;

import com.index.Params;
import com.index.data.sql.ChatInfo;
import com.index.enums.RestrictionMediaType;
import com.index.model.holders.Chat;
import com.index.model.holders.UpdateVariables;
import com.index.sends.SendMessageMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Comparator;
import java.util.Objects;

public class AdminChatRestrictionAdd
{
    public AdminChatRestrictionAdd(Update update)
    {
        UpdateVariables variables = new UpdateVariables(update);
        if (variables.getUpdateChat() == null || variables.getUpdateMessage() == null)
        {
            return;
        }
        Chat chat = variables.getUpdateChat();
        String[] splitData = variables.getUpdateMessage().split(" ", 3);
        if (splitData.length == 1)
        {
            new SendMessageMethod(0, chat.getChatID(), getClass().getSimpleName() + ": Недостаточно параметров;");
            return;
        }
        RestrictionMediaType rmt;
        try
        {
            rmt = RestrictionMediaType.valueOf(splitData[1]);
        }
        catch (Exception ignored)
        {
            new SendMessageMethod(0, chat.getChatID(), getClass().getSimpleName() + ": Неизвестный параметр;");
            return;
        }
        if (rmt == null)
        {
            new SendMessageMethod(0, chat.getChatID(), getClass().getSimpleName() + ": Неизвестный параметр;");
            return;
        }
        final String fileID;
        if (variables.getMessage() == null)
        {
            new SendMessageMethod(0, chat.getChatID(), getClass().getSimpleName() + ": Не могу найти сообщение;");
            return;
        }
        Message message = variables.getMessage().getReplyToMessage();
        if (message == null)
        {
            new SendMessageMethod(0, chat.getChatID(), getClass().getSimpleName() + ": Не могу найти отвеченное сообщение;");
            return;
        }
        switch (rmt)
        {
            case BOT:
            {
                if (!message.hasViaBot())
                {
                    new SendMessageMethod(0, chat.getChatID(), getClass().getSimpleName() + ": Не могу найти ИД бота;");
                    return;
                }
                fileID = String.valueOf(message.getViaBot().getId());
                break;
            }
            case GIF:
            {
                if (!message.hasAnimation())
                {
                    new SendMessageMethod(0, chat.getChatID(), getClass().getSimpleName() + ": Не могу найти гифку;");
                    return;
                }
                fileID = String.valueOf(message.getAnimation().getFileId());
                break;
            }
            case VIDEO:
            {
                if (!message.hasVideo())
                {
                    new SendMessageMethod(0, chat.getChatID(), getClass().getSimpleName() + ": Не могу найти видео;");
                }
                fileID = String.valueOf(message.getVideo().getFileId());
                break;
            }
            case STICKER:
            {
                if (!message.hasSticker())
                {
                    new SendMessageMethod(0, chat.getChatID(), getClass().getSimpleName() + ": Не могу найти стикер;");
                    return;
                }
                fileID = String.valueOf(message.getSticker().getSetName());
                break;
            }
            case PHOTO:
            {
                if (!message.hasPhoto())
                {
                    new SendMessageMethod(0, chat.getChatID(), getClass().getSimpleName() + ": Не могу найти фото;");
                    return;
                }
                fileID = Objects.requireNonNull(message.getPhoto().stream().max(Comparator.comparing(PhotoSize::getFileId)).orElse(null)).getFileId();
                if (fileID == null)
                {
                    new SendMessageMethod(0, chat.getChatID(), getClass().getSimpleName() + ": Не могу получить ИД фото;");
                    return;
                }
                break;
            }
            case FORWARD:
            {
                if (message.getForwardFromChat() == null)
                {
                    new SendMessageMethod(0, chat.getChatID(), getClass().getSimpleName() + ": Не могу получить ИД чата, с которого переслано сообщение;");
                    return;
                }
                fileID = String.valueOf(message.getForwardFromChat().getId());
                break;
            }
            default:
            {
                fileID = null;
                new SendMessageMethod(0, chat.getChatID(), getClass().getSimpleName() + ": Не понимаю что хотите сделять;");
                return;
            }
        }
        if (chat.getRestrictionMedia().addToRestrictionMedia(rmt, fileID))
        {
            new SendMessageMethod(0, chat.getChatID(), getClass().getSimpleName() + ": Файл добавлен в список заблокированных;");
        }
        else
        {
            new SendMessageMethod(0, chat.getChatID(), getClass().getSimpleName() + ": Произошла ошибка при добавлении файла в список заблокированных;");
        }
        if (chat.getChatID().equals(Params.CHAT_YUMMY_CHAT_TECH))
        {
            if (ChatInfo.getInstance().getChat(Params.CHAT_YUMMY_CHAT).getRestrictionMedia().addToRestrictionMedia(rmt, fileID))
            {
                new SendMessageMethod(1, Params.CHAT_YUMMY_CHAT_TECH, getClass().getSimpleName() + ": Файл добавлен в список заблокированных для Ями Чата;");
            }
            else
            {
                new SendMessageMethod(1, Params.CHAT_YUMMY_CHAT_TECH, getClass().getSimpleName() + ": Произошла ошибка при добавлении файла в список заблокированных для Ями Чата;");
            }
        }
    }
}
