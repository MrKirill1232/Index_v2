package com.index.model.holders;

import com.index.DataBaseConnection;
import com.index.IndexMain;
import com.index.data.sql.UserInfo;
import com.index.enums.RestrictionType;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Moderation {

    IndexMain im = new IndexMain();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss MM/dd/yyyy", Locale.getDefault(Locale.Category.FORMAT));
    UserInfo ifh = UserInfo.getInstance();
    private String message;

    public boolean getAction(Update update, boolean announceType, RestrictionType restrictionType)
    {
        if (restrictionType == RestrictionType.MUTE)
        {
            return callMute(update, announceType);
        }
        else
        {
            return callUnmute(update);
        }
    }

    public boolean callMute(Update update, boolean announceType)
    {
        Message temp = update.getMessage();
        String chat_id = String.valueOf(update.getMessage().getChatId());
        StringBuilder user_id = new StringBuilder();
        String call_name = temp.getSenderChat() == null ? temp.getFrom().getFirstName() : temp.getSenderChat().getTitle();
        String mute_name = temp.getReplyToMessage() == null ? null : temp.getReplyToMessage().getSenderChat() == null ? temp.getReplyToMessage().getFrom().getFirstName() : temp.getReplyToMessage().getSenderChat().getTitle();
        StringBuilder comment = new StringBuilder();
        Calendar time = null;
        message = update.getMessage().getText().toLowerCase();
        String[] message_s = message.split(" ");
        try {
            for (String messageNode : message_s) {
                if (message.equals("//mute") || message.equals("/mute"))
                {
                    im.SendAnswer(chat_id, call_name, "Ошибка обработки команды //mute. Проверьте написание команды\n//mute секунд/минут/часов время ИД пользователя (необязательно если пересланное)");
                    return false;
                }
                if (messageNode.equals("//mute") || messageNode.equals("/mute"))
                {
                    continue;
                }
                if (user_id.isEmpty() && temp.getReplyToMessage() == null)
                {
                    if (isUserID(messageNode))
                    {
                        user_id.append(messageNode);
                        if (mute_name == null)
                        {
                            mute_name = ifh.getUser(chat_id, user_id.toString()).getUserName();
                        }
                        continue;
                    }
                }
                if (time == null) {
                    if (messageNode.contains("д"))
                    {
                        time = Calendar.getInstance();
                        time.add(Calendar.DATE, Integer.parseInt(messageNode.replace("д", "")));
                    }
                    else if (messageNode.contains("ч"))
                    {
                        time = Calendar.getInstance();
                        time.add(Calendar.HOUR, Integer.parseInt(messageNode.replace("ч", "")));
                    }
                    else if (messageNode.contains("м"))
                    {
                        time = Calendar.getInstance();
                        time.add(Calendar.MINUTE, Integer.parseInt(messageNode.replace("м", "")));
                    }
                    // eng C
                    else if (messageNode.contains("c"))
                    {
                        time = Calendar.getInstance();
                        if (Integer.parseInt(messageNode.replace("c", "")) == 0)
                        {
                            time.add(Calendar.DATE, -2);
                        }
                        else time.add(Calendar.SECOND, Integer.parseInt(messageNode.replace("c", "")));
                    }
                    // Cyrillic с
                    else if (messageNode.contains("с"))
                    {
                        time = Calendar.getInstance();
                        if (Integer.parseInt(messageNode.replace("с", "")) == 0)
                        {
                            time.add(Calendar.DATE, -2);
                        }
                        else time.add(Calendar.SECOND, Integer.parseInt(messageNode.replace("с", "")));
                    }
                }
                else
                {
                    comment.append(comment.isEmpty() ? "" : " ").append(messageNode);
                }
            }
        }
        catch (NullPointerException ignored)
        {
            im.SendAnswer(chat_id, call_name, "Ошибка обработки команды //mute. Проверьте написание команды\n//mute секунд/минут/часов время ИД пользователя (необязательно если пересланное)\nNull");
            return false;
        }
        if (time == null) {
            im.SendAnswer(chat_id, call_name, "Ошибка обработки команды //mute. Проверьте написание команды\n//mute секунд/минут/часов время ИД пользователя (необязательно если пересланное)");
            return false;
        }
        user_id = !user_id.isEmpty() ? user_id : new StringBuilder(temp.getReplyToMessage().getSenderChat() == null ? String.valueOf(temp.getReplyToMessage().getFrom().getId()) : String.valueOf(temp.getReplyToMessage().getSenderChat().getId()));
        return callMute(chat_id, user_id.toString(), call_name, mute_name, comment.toString(), time, update, announceType);
    }

    public boolean callMute(String chatID, String userID, String callName, String muteName, String comment, Calendar time, Update update, Boolean announceType) {
        return callMute(chatID, userID, callName, muteName, comment, time, update, announceType, true);
    }
    public boolean callMute(String chatID, String userID, String callName, String muteName, String comment, Calendar time, Update update, boolean announceType, boolean storeUpdate)
    {
        String banMessage = "Применены ограничения на отправку сообщений для пользователя " + muteName + " модератором " + callName + "." + (comment.isEmpty() ? "" : " Причина обозначена как \"" + comment + "\".") +
                "\nЕсли Вы не согласны с блокировкой - напишите @REAltair . Снятие ограничений произойдет " + DATE_FORMAT.format(time.getTime()) + ".";
        String errorMessage = "Ошибка при применении ограничений на отправку сообщений для пользователя " + muteName + " модератором " + callName + ".";
        int replyMessage = update.getMessage().getReplyToMessage() != null ? update.getMessage().getReplyToMessage().getMessageId() : 0;
        if (UserInfo.getInstance().getUser(chatID, userID) == null)
        {
            UserInfo.getInstance().addUser(update);
        }
        if (Long.parseLong(userID) > 0)
        {
            RestrictChatMember mute = new RestrictChatMember();
            mute.setChatId(chatID);
            mute.setUserId(Long.parseLong(userID));
            mute.setPermissions(new ChatPermissions(false, false, false, false, false, false, false, false));
            mute.setUntilDate(Math.toIntExact((long) time.getTimeInMillis() / 1000L));
            try {
                im.execute(mute);
                if (announceType) im.SendAnswer(chatID, callName, banMessage, "null", replyMessage);
                updateUser(chatID, userID, time);
                updateDateBase(chatID, ifh.getUser(chatID, userID), comment, storeUpdate ? update : null);
                return true;
            } catch (TelegramApiException e) {
                im.SendAnswer(chatID, callName, errorMessage, "null", replyMessage);
                return false;
            } catch (Exception e)
            {
                im.SendAnswer(chatID, callName, "Интересная ошибка " + e, "null", replyMessage);
            }
        }
        updateUser(chatID, userID, time);
        return updateDateBase(chatID, ifh.getUser(chatID, userID), comment, update);
    }

    public boolean callUnmute(Update update)
    {
        Message temp = update.getMessage();
        String chatID = String.valueOf(update.getMessage().getChatId());
        StringBuilder userID = new StringBuilder();
        String call_name = temp.getSenderChat() == null ? temp.getFrom().getFirstName() : temp.getSenderChat().getTitle();
        String mute_name = temp.getReplyToMessage() == null ? null : temp.getReplyToMessage().getSenderChat() == null ? temp.getReplyToMessage().getFrom().getFirstName() : temp.getReplyToMessage().getSenderChat().getTitle();
        if (userID.isEmpty() && temp.getReplyToMessage() == null)
        {
            try {
                String[] message_s = message.split(" ");
                for (String messageNode : message_s) {
                    if (messageNode.equals("//unmute") || messageNode.equals("/unmute")) {
                        continue;
                    }
                    if (isUserID(messageNode)) {
                        userID.append(messageNode);
                        if (mute_name == null) {
                            mute_name = ifh.getUser(chatID, userID.toString()).getUserName();
                        }
                    }
                }
            }
            catch (NullPointerException ignored)
            {
                // can be going if message has only //unmute without additional variables like "//unmute *user_id*"
                userID = new StringBuilder(temp.getSenderChat() == null ? String.valueOf(temp.getFrom().getId()) : String.valueOf(temp.getSenderChat().getId()));
                mute_name = ifh.getUser(chatID, userID.toString()).getUserName();
            }
        }
        else if (temp.getReplyToMessage() == null)
        {
            im.SendAnswer(chatID, call_name, "Ошибка обработки команды //unmute. Проверьте написание команды\n//unmute ИД пользователя (необязательно если пересланное)");
            return false;
        }
        userID = !userID.isEmpty() ? userID : new StringBuilder(temp.getReplyToMessage().getSenderChat() == null ? String.valueOf(temp.getReplyToMessage().getFrom().getId()) : String.valueOf(temp.getReplyToMessage().getSenderChat().getId()));
        String replyMessage = update.getMessage().getReplyToMessage() != null ? String.valueOf(update.getMessage().getReplyToMessage().getMessageId()) : "0";
        if (UserInfo.getInstance().getUser(chatID, userID.toString()) == null)
        {
            UserInfo.getInstance().addUser(update);
        }
        return callUnmute(chatID, userID.toString(), mute_name, replyMessage);
    }

    public boolean callUnmute(String chatID, String userID, String userName, String replyMessageId)
    {
        String unmuteMessage = "Снимаю ограничения с пользователя " + userName + ".";
        String unmuteErrorMessage = "Ошибка при попытке снять ограничения с пользователя " + userName + ".";
        if (Long.parseLong(userID) > 0)
        {
            RestrictChatMember unmute = new RestrictChatMember();
            unmute.setChatId(chatID);
            unmute.setUserId(Long.parseLong(userID));
            unmute.setPermissions(new ChatPermissions(true, true, false, true, false, false, false, false));
            unmute.setUntilDate(Math.toIntExact((Calendar.getInstance().getTimeInMillis() / 1000) + 10));
            try {
                im.execute(unmute);
                im.SendAnswer(chatID, "Index", unmuteMessage, "null", Integer.parseInt(replyMessageId));
                updateUser(chatID, userID, null);
                return true;
            } catch (Exception e) {
                im.SendAnswer(chatID, "Index", unmuteErrorMessage, "null", Integer.parseInt(replyMessageId));
                return false;
            }
        }
        updateUser(chatID, userID, null);
        im.SendAnswer(chatID, "Index", unmuteMessage, "null", Integer.parseInt(replyMessageId));
        return true;
    }

    private static final String ADD_RESTRICTED_USER_IN_TABLE = "INSERT INTO restrictions (chat_id,user_id,restriction_type,restriction_time,comment,bot_comment) VALUES (?,?,?,?,?,?)";
    public boolean updateDateBase(String chatID, User user, String comment, Update update)
    {
        try (Connection con = DataBaseConnection.getConnection();
        PreparedStatement statement = con.prepareStatement(ADD_RESTRICTED_USER_IN_TABLE))
        {
            statement.setString(1, String.valueOf(chatID));
            statement.setString(2, String.valueOf(user.getUserID()));
            statement.setString(3, String.valueOf(user.getRestrictionType().ordinal()));
            statement.setString(4, String.valueOf(user.getRestrictionTime().getTimeInMillis()));
            statement.setString(5, String.valueOf(comment));
            statement.setString(6, update != null ? String.valueOf(update) : "null");
            statement.execute();
            statement.closeOnCompletion();
            return true;
        }
        catch (Exception e)
        {
            im.SendAnswer(im.YummyReChat, "Index", "Ошибка выполнения команды updateDateBase + ADD_RESTRICTED_USER_IN_TABLE: "+e);
            return false;
        }
    }
    public void updateUser(String chatID, String userID, Calendar time)
    {
        User user = ifh.getUser(chatID, userID);
        if (time != null)
        {
            user.setRestrictionTime(time);
            user.setRestrictionType(RestrictionType.MUTE);
        }
        else
        {
            user.setRestrictionTime(0);
            user.setRestrictionType(RestrictionType.NULL);
        }
        ifh.storeMe(chatID, userID);
    }
    public boolean isUserID(String message)
    {
        try
        {
            if (Character.isDigit(message.charAt(0)))
            {
                return true;
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return false;
    }
}
