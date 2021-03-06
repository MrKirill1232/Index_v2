package com.index.chat;

import com.index.IndexMain;
import com.index.data.sql.ChatInfo;
import com.index.data.sql.UserInfo;
import com.index.model.holders.User;
import org.telegram.telegrambots.meta.api.objects.Update;

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
        String command = updateMessage.split(" ", 2)[0];
        Commands requestCommand = Commands.getCommandByString(command);
        if (requestCommand != null)
        {
            try
            {
                requestCommand.makeCommand(update);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        /*
        else if (updateMessage.startsWith("//user_get"))
        {
            String[] split = updateMessage.split(" ");
            if (split.length == 1)
            {
                return;
            }
            sendMessage(UserInfo.getInstance().getUser(updateChatID, split[1]).getAllInfo(), "HTML", messageID);
        }
        else if (updateMessage.startsWith("//user_save"))
        {
            UserInfo.getInstance().storeChat(updateChatID);
            sendMessage("??????????????????????: ?????????????? ?????????????????? ???????????????????? ?? ?????????????????????????? ????????" + ";", "null", messageID);
        }
        else if (updateMessage.startsWith("//chat_moder_add"))
        {
            if (replyUser == null) return;
            ChatInfo.getInstance().getChat(updateChatID).addToUserModeration(replyUser.getUserID());
            ChatInfo.getInstance().storeMe(updateChatID);
            sendMessage("??????????????????????: ???????????????????????? " + replyUser.getUserName() + " ?????????????? ?????????? " + (ChatInfo.getInstance().getChat(updateChatID).getUserModeration().contains(replyUser.getUserID()) ? "???????????????????? " : "????????????????????????") + ";", "null", messageID);
        }
        else if (updateMessage.startsWith("//chat_moder_remove"))
        {
            ChatInfo.getInstance().getChat(updateChatID).removeUserModeration(replyUser.getUserID());
            ChatInfo.getInstance().storeMe(updateChatID);
            sendMessage("??????????????????????: ???????????????????????? " + replyUser.getUserName() + " ?????????????? ?????????? " + (ChatInfo.getInstance().getChat(updateChatID).getUserModeration().contains(replyUser.getUserID()) ? "???????????????????? " : "????????????????????????") + ";", "null", messageID);
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
                sendMessage("????????????", "null", messageID);
                return;
            }
            im.deleteMessage(updateChatID, requestMessageID);
        }
        else if (updateMessage.startsWith("//mass_delete"))
        {
            String[] splits = updateMessage.split(" ");
            new massDelete(updateChatID, splits[1], splits[2]);
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
        } else if (updateMessage.startsWith("//??????????????"))
        {
            StringBuilder message = new StringBuilder("????????????, ?? ?????????????? ?? ?? ???????????? ???? ?????? ???????? ??????????????, ?????????????? ???? ?????????? ?????????? ?????????????????? :)\n");
            message.append("1. .com / .club / .info - ???????????? ?? ???????? ?????????????? ???? ?????????? ???????????????????????????????? ??????????????????. ?????????????? ?????????????? ??????????????????.\n");
            message.append("2. ???????????? ?? ???????????????????????????? ??????????... ?????? ??????????, ?????????????? ???????? ???? ???????????????? ???????????????? ?? ???????? ?????????????? ???????????? ?? ?????????? ???????????????? ???????????? ?? ?????? - ?????????? ?????????????????????????? ?????????????????? ???????????? - ?? ?????? ?????????? ????????????????????.\n");
            message.append("??????, ???????????? ???????????????? ???????????? ?? ???????????? ???????????? ??????????????. ???? ?????? ????????. ???????? wayback.machine ???? ?????????? ?????? ???? ????????????????????????.\n");
            message.append("3. ???????? ???????????? ???? ?????????? ?????????????? ???????????????? ?????? ???? ?????????????????? ??????. ???????? ?????? ?????????????????? - ?????????????? ???? ???????????? ?????????????????? ????????????.\n");
            message.append("4. ???? ???????????????? ?????????????????? ??????????????????????????? ???????????? ???????????? ??????????????????????? ???????? ???????????????? - @REaltair\n");
            sendMessage(message.toString(), "null", Integer.parseInt(messageID));
        }
        else if (updateMessage.startsWith("//update_s"))
        {
            if (replyUser == null)
            {
                updateUser.setIgnoreStickerCheck(!updateUser.getIgnoreStickerCheck());
                im.SendAnswer(updateChatID, "Index", "??????????????????????: ?????? ???????????????????????? " + updateUser.getUserName() + " ???????????? " + (updateUser.getIgnoreStickerCheck() ? "???? ?????????????????????? " : "?????????????????????? ") + "???????????????????????? ??????????????;");
            }
            else
            {
                replyUser.setIgnoreStickerCheck(!replyUser.getIgnoreStickerCheck());
                im.SendAnswer(updateChatID, "Index", "??????????????????????: ?????? ???????????????????????? " + replyUser.getUserName() + " ???????????? " + (replyUser.getIgnoreStickerCheck() ? "???? ?????????????????????? " : "?????????????????????? ") + "???????????????????????? ??????????????;");
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
            button.setText("?????????????????? ????????????");
            button.setSwitchInlineQuery("???????????? ?????????????? ????????????????!");
            button.setCallbackData("???????????? ?????????????? ????????????????!");

            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(button);
            rowsInline.add(rowInline);
            markupInline.setKeyboard(rowsInline);
            SendMessage message = new SendMessage();
            message.setReplyMarkup(markupInline);
            message.setChatId(String.valueOf(update.getMessage().getChatId()));
            message.setText("?????????????? ?????????????????? ???????????? ???? Asterios?");
            try {
                im.execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        else if (updateMessage.startsWith("//stats"))
        {
        }
        else if (updateMessage.startsWith("//umu"))
        {
            try {
                Commands.ADMIN_PING.makeCommand(update);
            } catch (Exception e)
            {
                e.printStackTrace();
            }


            /*
            GetChatAdministrators getChatAdministrators = new GetChatAdministrators(new IndexMain().YummyReChat);
            try {
                List<ChatMember> umu = new IndexMain().execute(getChatAdministrators);
                sendMessage(umu.toString(), "null", 0);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

             */
            /*
            SendPoll sp = new SendPoll();
            sp.setChatId(new IndexMain().YummyReChat);
            sp.setOptions(List.of("umu", "umu", "umu"));
            sp.setQuestion("How to be umu");
            sp.setType("quiz");
            sp.setCorrectOptionId(2);
            sp.setIsAnonymous(false);
            try {
                new IndexMain().execute(sp);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

             */


            /*
            org.telegram.telegrambots.meta.api.objects.InputFile file = new org.telegram.telegrambots.meta.api.objects.InputFile();
            file.setMedia("AgACAgIAAx0CVq806gABEOyQYq3xsiDyM2k_IQqS6KzaWFpKK70AAiu9MRsmqXFJmfhWomVecz4BAAMCAANtAAMkBA");
            SendPhoto photo = new SendPhoto();
            photo.setChatId(updateChatID);
            photo.setPhoto(file);
            photo.setCaption("???? ???????????????? ????????????????");
            try {
                im.execute(photo);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            // sendMessage(update.getMessage().getAnimation().getFileId(), "null", 0);
        }
        else if (updateMessage.startsWith("//parse"))
        {
            ReportTicketsInfo.getInstance().load();
        }
        */
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
