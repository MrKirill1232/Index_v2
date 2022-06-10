package com.index.chat;

import com.index.IndexMain;
import com.index.data.sql.UserInfo;
import com.index.enums.RestrictionMediaType;
import com.index.model.events.CallbackQueryManager;
import com.index.model.forwarding.Forward;
import com.index.model.holders.*;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ChatModerationHandler {

    IndexMain im = new IndexMain();
    Moderation mod = new Moderation();
    Chat updateChat;
    User updateUser;
    User replyUser;
    String updateMessage;
    Long currentTime;
    /**
     * @apiNote Time, when variables will be reset for user
     */
    Long nextResetTime;
    /**
     * @apiNote If user has any restrictions - this variable will contain time when it will be gone
     */
    Long currentRestrictionTime;
    String messageID;
    /**
     * @apiNote Time, when update been creates
     */
    Long updateTime;
    List<String> ignoreUsers;
    List<String> userModeration;
    boolean skip_check;
    boolean isActual;
    boolean restricted;
    private void setVariables(Update update){
        UpdateVariables uv = new UpdateVariables(update);
        if (uv.getMessage() == null)
        {
            return;
        }
        updateChat = uv.getUpdateChat();
        updateUser = uv.getUpdateUser();
        if (updateChat == null || updateUser == null)
        {
            return;
        }
        replyUser = uv.getReplyUser();
        updateMessage = uv.getUpdateMessage();
        currentTime = uv.getCurrentTime();
        nextResetTime = uv.getNextResetTime();
        currentRestrictionTime = uv.getCurrentRestrictionTime();
        messageID = uv.getMessageID();
        // updateTime = uv.getUpdateTime();
        ignoreUsers = uv.getIgnoreUsers();
        userModeration = uv.getUserModeration();
        skip_check = uv.isSkip_check();
        restricted = uv.isRestricted();
    }
    protected void deleteMessage()
    {
        im.deleteMessage(updateChat.getChatID(), messageID);
    }
    protected void sendMessage(String message)
    {
        im.SendAnswer(updateChat.getChatID(), "Index", message, "null", 0);
    }
    protected void callMute(Calendar time, String comment, boolean announce, Update bot_comment)
    {
        mod.callMute(updateChat.getChatID(), updateUser.getUserID(), "Index", updateUser.getUserName(), comment, time, bot_comment, announce, false);
    }
    private boolean isActual()
    {
        Calendar update_time_calendar = Calendar.getInstance();
        update_time_calendar.setTimeInMillis(updateTime * 1000L);
        update_time_calendar.add(Calendar.MINUTE, 10);
        long umu_1 = System.currentTimeMillis();
        return ((update_time_calendar.getTimeInMillis()) >= (umu_1));
    }
    public ChatModerationHandler(Update update)
    {
        if (update.getMessage() == null && !update.hasCallbackQuery())
        {
            return;
        }
        updateTime = update.getMessage() == null ? (long) update.getCallbackQuery().getMessage().getDate() : (long) update.getMessage().getDate();
        isActual = isActual();
        if (update.getCallbackQuery() != null && isActual)
        {
            new CallbackQueryManager(update);
            return;
        }
        setVariables(update);
        if (update.getMessage().getChatId() > 0)
        {
            new Forward("delete", false, update);
            return;
        }
        if (updateChat.getChatID().equals(im.YummyChannel_CHAT) && im.RESEND)
        {
            new Forward("Forwarding", isActual, update);
        }
        if (updateChat.getChatID().equals(im.YummyReChat) && (
                updateMessage == null || (!updateMessage.startsWith("!") && !updateMessage.startsWith("/") && !updateMessage.startsWith("//"))))
        {
            new Forward("Translate", isActual, update);
        }
        if (isActual && restricted)
        {
            deleteMessage();
            return;
        }
        if ((updateMessage != null && updateMessage.startsWith("/") && !updateMessage.startsWith("//")) &&
                (
                        (userModeration != null && userModeration.contains(updateUser.getUserID())) || (ignoreUsers != null && ignoreUsers.contains(updateUser.getUserID()))
                ))
        {
            new UserCommandsHandler(update);
            return;
        }
        if ((updateMessage != null && updateMessage.startsWith("//")) && ( ignoreUsers != null && ignoreUsers.contains(updateUser.getUserID())))
        {
            new AdminCommandHandler(update);
            return;
        }
        if (updateUser.getNextMessageReset().getTimeInMillis() < currentTime)
        {
            resetFields();
        }
        CheckRestriction(update);
    }

    protected String sendGreeting()
    {
        return "Привет " + updateUser.getUserName() +"! Добро пожаловать в " + "Чат YummyAnime" + " :)\n" +
                "Правила можешь найти здесь - [*клик*](https://t.me/c/1454322922/65922/)\n" +
                "Если интересует как обойти блокировку сайта - [*клик*](https://t.me/c/1454322922/21351/)";
    }

    private void CheckRestriction(Update update)
    {
        if (skip_check) return;
        Calendar calendar = Calendar.getInstance();
        Message temp = update.getMessage();
        RestrictionMedia rMedia = updateChat.getRestrictionMedia();
        String file_id;
        boolean need_to_announce = false;

        if (updateMessage != null)
        {
            List <String> vanya = List.of("Иванка", "Иваня", "Иванюха", "Иванюша", "Ивася", "Ивасик", "Иваха", "Иваша", "Иша", "Ишута",
                    "Ваня", "Ванюха", "Ванюша", "Ванюра", "Ванюся", "Ванюта", "Ванютя", "Ванята", "Ива", "Ванька", "Ваню", "Ivanc7");
            if (updateMessage.contains("꙰") || updateMessage.contains("⃟") || updateMessage.toLowerCase().contains("shianime.org"))
            {
                deleteMessage();
                if (!restricted)
                {
                    calendar.add(Calendar.MINUTE, 100);
                    callMute(calendar, "Текст сообщения подозрительного характера - автоматическая блокировка;", true, update);
                    need_to_announce = true;
                }
            }
            if (isActual && UserInfo.getInstance().getUser(im.YummyChannel_CHAT, "466294009").getNextMessageReset().getTimeInMillis() > currentTime)
            {
                for (String token : updateMessage.toLowerCase().split(" "))
                {
                    if (!token.startsWith("іван") && (token.length() == 3 || token.length() >= 4) && token.contains("ван"))
                    {
                        im.SendAnswer(updateChat.getChatID(), "Index", "Іван! \uD83E\uDD2C\uD83E\uDD2C\uD83E\uDD2C", "null", Integer.parseInt(messageID));
                        break;
                    }
                }
            }
        }
        if (temp.hasAnimation())
        {
            file_id = temp.getAnimation().getFileUniqueId();
            if (rMedia.checkIsInRestrictionMedia(RestrictionMediaType.GIF, file_id))
            {
                new Forward("DELETE", isActual, update);
                deleteMessage();
                if (!restricted)
                {
                    calendar.add(Calendar.MINUTE, 10);
                    callMute(calendar, "GIF файлы подозрительного характера - автоматическая блокировка;", true, update);
                    need_to_announce = true;
                }
            }
            else
            {
                int max_available_gif = updateChat.getMaxGifCount();
                int gif_count = updateUser.getGifCount();
                if (gif_count > max_available_gif)
                {
                    new Forward("DELETE", isActual, update);
                    deleteMessage();
                    if (!restricted)
                    {
                        calendar.add(Calendar.MINUTE, 2);
                        callMute(calendar, "Спам GIF файлами - автоматическая блокировка;", true, update);
                    }
                }
                else
                {
                    if (isActual) updateUser.setGifCount(gif_count + 1);
                }
            }
        }
        if (temp.hasVideo())
        {
            file_id = temp.getVideo().getFileUniqueId();
            if (rMedia.checkIsInRestrictionMedia(RestrictionMediaType.VIDEO, file_id))
            {
                new Forward("DELETE", isActual, update);
                deleteMessage();
                if (!restricted)
                {
                    calendar.add(Calendar.MINUTE, 10);
                    callMute(calendar, "Видео файлы подозрительного характера - автоматическая блокировка;\n" + update, true, update);
                    need_to_announce = true;
                }
            }
        }
        if (temp.hasSticker())
        {
            file_id = temp.getSticker().getSetName();
            if (rMedia.checkIsInRestrictionMedia(RestrictionMediaType.STICKER, file_id))
            {
                new Forward("DELETE", isActual, update);
                deleteMessage();
                if (!restricted)
                {
                    calendar.add(Calendar.MINUTE, 10);
                    callMute(calendar, "Стикеры подозрительного характера - автоматическая блокировка;", true, update);
                }
            }
            else
            {
                int sticker_available_count = updateChat.getMaxStickerCount();
                int sticker_count = updateUser.getStickerCount();
                if (!updateUser.getIgnoreStickerCheck() &&
                        (file_id == null || !updateChat.getAgreedStickerList().contains(file_id)))
                {
                    new Forward("DELETE", isActual, update);
                    deleteMessage();
                }
                if (sticker_count > sticker_available_count)
                {
                    new Forward("DELETE", isActual, update);
                    deleteMessage();
                    if (!restricted)
                    {
                        calendar.add(Calendar.MINUTE, 2);
                        callMute(calendar, "Спам стрикерами - Автоматическая блокировка;", true, update);
                    }
                }
                if (isActual)
                {
                    updateUser.setStickerCount(sticker_count + 1);
                }
            }
            if (isActual && file_id != null && file_id.equals("StikersPapich"))
            {
                new IndexMain().SendAnswer(updateChat.getChatID(), "Index", "Нет, Мамич.", "null", Integer.parseInt(messageID));
            }
        }
        if (       update.getMessage().hasVoice()
                || update.getMessage().hasDocument()
                || update.getMessage().hasText()
                || update.getMessage().hasAudio()
                || update.getMessage().hasContact()
                || update.getMessage().hasDice()
                || update.getMessage().hasLocation()
                || update.getMessage().hasPassportData()
                || update.getMessage().hasPhoto()
                || update.getMessage().hasPoll()
                || update.getMessage().hasReplyMarkup()
                || update.getMessage().hasSuccessfulPayment()
                || update.getMessage().hasInvoice()
                || update.getMessage().hasVideo()
                || update.getMessage().hasVideoNote()
            )
        {
        }
        if (temp.hasViaBot())
        {
            if (rMedia.checkIsInRestrictionMedia(RestrictionMediaType.BOT, String.valueOf(temp.getViaBot().getId())))
            {
                new Forward("DELETE", isActual, update);
                deleteMessage();
            }
        }
        if (temp.hasPhoto())
        {
            file_id = Objects.requireNonNull(temp.getPhoto().stream().max(Comparator.comparing(PhotoSize::getFileId)).orElse(null)).getFileUniqueId();
            if (rMedia.checkIsInRestrictionMedia(RestrictionMediaType.PHOTO, file_id))
            {
                new Forward("DELETE", isActual, update);
                deleteMessage();
                if (!restricted)
                {
                    calendar.add(Calendar.MINUTE, 10);
                    callMute(calendar, "Фото файлы подозрительного характера - автоматическая блокировка;", true, update);
                    need_to_announce = true;
                }
            }
        }
        if (temp.getNewChatMembers().stream().findFirst().isPresent())
        {
            new Forward("DELETE", isActual, update);
            deleteMessage();
            if (false)
            {
                sendMessage(sendGreeting());
            }
        }
        if (isActual && need_to_announce)
        {
            sendMessage("@MrKirill1232 - иди сюда, тут расчленёнка от " + update.getMessage().getFrom());
        }
        if (isActual && (Math.random() * 100) < 0.5)
        {
            org.telegram.telegrambots.meta.api.objects.InputFile file = new org.telegram.telegrambots.meta.api.objects.InputFile();
            //java.io.File photo_f = new File("photo_2022-02-06_17-23-09.jpg");
            //file.setMedia(photo_f);
            file.setMedia("CgACAgIAAx0CVq806gABDn9tYpjQXn3pYSymA3-fjfXwdU1CB1IAAsgaAALytcFLZEp5v-Dau2EkBA");
            SendAnimation animation = SendAnimation.builder()
                    .replyToMessageId(Integer.parseInt(messageID))
                    .animation(file)
                    .chatId(updateChat.getChatID())
                    .build();
            try {
                im.execute(animation);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void resetFields()
    {
        updateUser.setNextMessageReset(nextResetTime);
        updateUser.setStickerCount(0);
        updateUser.setGifCount(0);
    }
}
