package com.index.chat;

import com.index.IndexMain;
import com.index.data.sql.ChatInfo;
import com.index.data.sql.UserInfo;
import com.index.enums.RestrictionType;
import com.index.model.holders.Moderation;
import com.index.model.holders.User;
import com.index.model.stickers.addSticker;
import com.index.model.stickers.clearSticker;
import com.index.model.stickers.delSticker;
import com.index.model.stickers.listSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class AdminCommandHandler
{
    IndexMain im = new IndexMain();
    User updateUser;
    User replyUser;
    String updateMessage;
    String updateChatID;
    String messageID;

    private void setVariables(Update update)
    {
        updateChatID = String.valueOf(update.getMessage().getChatId());
        updateMessage = update.getMessage().getText().toLowerCase();
        String userID = update.getMessage().getSenderChat() == null ? String.valueOf(update.getMessage().getFrom().getId()) : String.valueOf(update.getMessage().getSenderChat().getId());
        String updateName = update.getMessage().getSenderChat() == null ? update.getMessage().getFrom().getUserName() : update.getMessage().getSenderChat().getTitle();
        updateUser = UserInfo.getInstance().getUser(updateChatID, userID);
        /*
        if (!updateName.toLowerCase().equals(updateUser.getUserName().toLowerCase()))
        {
            updateUser.addToKnowAs(updateUser.getUserName());
            updateUser.setUserName(updateName);
            UserInfo.getInstance().storeMe(updateChatID, userID);
        }
         */
        if (update.getMessage().getReplyToMessage() != null)
        {
            String replyUserID = update.getMessage().getReplyToMessage().getSenderChat() == null ? String.valueOf(update.getMessage().getReplyToMessage().getFrom().getId()) : String.valueOf(update.getMessage().getReplyToMessage().getSenderChat().getId());
            String replyName = update.getMessage().getReplyToMessage().getSenderChat() == null ? update.getMessage().getReplyToMessage().getFrom().getFirstName() : update.getMessage().getReplyToMessage().getSenderChat().getTitle();
            replyUser = UserInfo.getInstance().getUser(updateChatID, replyUserID);
            /*
            if (!replyName.equalsIgnoreCase(replyUser.getUserName().toLowerCase()))
            {
                replyUser.addToKnowAs(replyUser.getUserName());
                replyUser.setUserName(replyName);
                UserInfo.getInstance().storeMe(updateChatID, replyUserID);
            }

             */
        }
        messageID = String.valueOf(update.getMessage().getReplyToMessage() != null ? update.getMessage().getReplyToMessage().getMessageId() : update.getMessage().getMessageId());
    }
    public AdminCommandHandler(Update update)
    {
        setVariables(update);
        // CHAT
        if (updateMessage.startsWith("//chat_info"))
        {
            sendMessage(ChatInfo.getInstance().getChat(updateChatID).getChatInfo(), "null", update.getMessage().getMessageId());
        }
        else if (updateMessage.startsWith("//user_save"))
        {
            UserInfo.getInstance().storeChat(updateChatID);
        }
        else if (updateMessage.startsWith("//chat_moder_add"))
        {
            ChatInfo.getInstance().getChat(updateChatID).addToUserModeration(replyUser.getUserID());
            ChatInfo.getInstance().storeMe(updateChatID);
        }
        else if (updateMessage.startsWith("//chat_moder_remove"))
        {
            ChatInfo.getInstance().getChat(updateChatID).removeUserModeration(replyUser.getUserID());
            ChatInfo.getInstance().storeMe(updateChatID);
        }
        else if (updateMessage.startsWith("//chat_moder_list"))
        {
            StringBuilder message = new StringBuilder();
            List<String> moders = ChatInfo.getInstance().getChat(updateChatID).getUserModeration();
            for (String userID : moders)
            {
                message.append(UserInfo.getInstance().getUser(updateChatID, userID).getUserName());
                if (moders.lastIndexOf(userID) != moders.size() - 1)
                {
                    message.append(", ");
                }
            }
            sendMessage(message.append(";").toString(), "null", update.getMessage().getMessageId());
        }
        else if (updateMessage.startsWith("//chat_admin_add"))
        {
            ChatInfo.getInstance().getChat(updateChatID).addToAdminList(replyUser.getUserID());
            ChatInfo.getInstance().storeMe(updateChatID);
        }
        else if (updateMessage.startsWith("//chat_admin_remove"))
        {
            ChatInfo.getInstance().getChat(updateChatID).removeFromAdminList(replyUser.getUserID());
            ChatInfo.getInstance().storeMe(updateChatID);
        }
        else if (updateMessage.startsWith("//chat_admin_list"))
        {
            StringBuilder message = new StringBuilder();
            List<String> admins = ChatInfo.getInstance().getChat(updateChatID).getAdminsList();
            for (String userID : admins)
            {
                message.append(UserInfo.getInstance().getUser(updateChatID, userID).getUserName());
                if (admins.lastIndexOf(userID) != admins.size() - 1)
                {
                    message.append(", ");
                }
            }
            sendMessage(message.append(";").toString(), "null", update.getMessage().getMessageId());
        }
        else if (updateMessage.startsWith("//delete"))
        {
            String[] text = updateMessage.split(" ");
            String requestMessageID = "";
            for (String token : text)
            {
                if (token.equals("//delete"))
                {
                    continue;
                }
                requestMessageID = token;
            }
            if (requestMessageID.isBlank() || requestMessageID.isEmpty())
            {
                sendMessage("Ошибка", "null", messageID);
                return;
            }
            im.deleteMessage(updateChatID, requestMessageID);
        }
        // STICKERS
        else if (updateMessage.startsWith("//add_s"))
        {
            new addSticker(update);
        }
        else if (updateMessage.startsWith("//del_s"))
        {
            new delSticker(update);
        }
        else if (updateMessage.startsWith("//list_s"))
        {
            new listSticker(update, false);
        }
        else if (updateMessage.contains("//clear_s")) {
            if (ChatInfo.getInstance().getChat(updateChatID) != null)
            {
                new clearSticker(ChatInfo.getInstance().getChat(updateChatID));
            }
        } else if (updateMessage.startsWith("//отписка"))
        {
            StringBuilder message = new StringBuilder("Привет, я отписка и я отвечу на все твои вопросы, которые мы любим здесь обсуждать :)\n");
            message.append("1. .com / .club / .info - только к этим доменам мы имеем непосредственное отношение. Просьба удалить сообщение.\n");
            message.append("2. Списки с просмотренными аниме... Все файлы, которые были на серверах хранятся в виде сжатого бекапа и чтобы получить доступ к ним - нужно разворачивать серверные бекапы - а это много информации.\n");
            message.append("Нет, нельзя получить списки в данный момент времени. Ни для кого. Даже wayback.machine не может вам их восстановить.\n");
            message.append("3. Сайт закрыт на время военных действий или до окончания СВО. Если что изменится - сообщим на канале отдельным постом.\n");
            message.append("4. Не нравится обращение администрации? Хочешь внести предложение? Пиши контакту - @REaltair\n");
            sendMessage(message.toString(), "null", Integer.parseInt(messageID));
        }
        else if (updateMessage.startsWith("//update_s"))
        {
            if (replyUser == null)
            {
                updateUser.setIgnoreStickerCheck(!updateUser.getIgnoreStickerCheck());
                im.SendAnswer(updateChatID, "Index", "УВЕДОМЛЕНИЕ: Для пользователя " + updateUser.getUserName() + " теперь " + (!updateUser.getIgnoreStickerCheck() ? "не проверяются " : "проверяются ") + "отправленные стикеры;");
            }
            else
            {
                replyUser.setIgnoreStickerCheck(!replyUser.getIgnoreStickerCheck());
                im.SendAnswer(updateChatID, "Index", "УВЕДОМЛЕНИЕ: Для пользователя " + replyUser.getUserName() + " теперь " + (!replyUser.getIgnoreStickerCheck() ? "не проверяются " : "проверяются ") + "отправленные стикеры;");
            }
        }
        // MODERATION
        else if (updateMessage.startsWith("//mute"))
        {
            new Moderation().getAction(update, true, RestrictionType.MUTE);
        }
        else if (updateMessage.startsWith("//unmute"))
        {
            new Moderation().getAction(update, true, RestrictionType.NULL);
        }
        else if (updateMessage.startsWith("//getinfo"))
        {
            sendMessage(replyUser == null ? updateUser.getAllInfo() : replyUser.getAllInfo(), "HTML", update.getMessage().getMessageId());
        }
        // TEST THINGS
        if (update.getMessage().getText().toLowerCase().startsWith("//online")) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("Наркутить онлайн");
            button.setSwitchInlineQuery("Онлайн успешно накручен!");
            button.setCallbackData("Онлайн успешно накручен!");

            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(button);
            rowsInline.add(rowInline);
            markupInline.setKeyboard(rowsInline);
            SendMessage message = new SendMessage();
            message.setReplyMarkup(markupInline);
            message.setChatId(String.valueOf(update.getMessage().getChatId()));
            message.setText("Желаете накрутить онлайн на Asterios?");
            try {
                im.execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        else if (updateMessage.startsWith("//stats"))
        {
        }
    }

    private void sendMessage(String message, String format, String replyOn)
    {
        sendMessage(message, format, Integer.parseInt(replyOn));
    }
    private void sendMessage(String message, String format, int replyOn)
    {
        im.SendAnswer(updateChatID, updateUser.getUserName(), message, format, replyOn);
    }
}
