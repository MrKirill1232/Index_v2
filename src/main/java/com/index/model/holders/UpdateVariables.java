package com.index.model.holders;

import com.index.data.sql.ChatInfo;
import com.index.data.sql.UserInfo;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Calendar;
import java.util.List;

public class UpdateVariables
{
    Chat updateChat;
    User updateUser;
    User replyUser;
    String updateMessage;
    Long currentTime;
    Long nextResetTime;
    Long currentRestrictionTime;
    String messageID;
    Long updateTime;
    List<String> ignoreUsers;
    List<String> userModeration;
    boolean skip_check;
    boolean restricted;
    Message message;

    public UpdateVariables(Update update)
    {
        currentTime = System.currentTimeMillis();
        message = update.getMessage() != null ? update.getMessage() : update.getCallbackQuery() != null ? update.getCallbackQuery().getMessage() : null;
        if (message == null) return;
        UserInfo usersInfo = UserInfo.getInstance();
        ChatInfo chatsInfo = ChatInfo.getInstance();
        String chatID = String.valueOf(message.getChatId());
        updateChat = chatsInfo.getChat(chatID);
        if (updateChat == null) /**/ if (!chatsInfo.addChat(update)) /**/ return;
        if (false) //!temp.getChat().getTitle().equals(updateChat.getChatName()))
        {
            updateChat.setChatName(message.getChat().getTitle());
        }
        String userID = message.getSenderChat() == null ? String.valueOf(message.getFrom().getId()) : String.valueOf(message.getSenderChat().getId());
        String updateName = message.getSenderChat() == null ? message.getFrom().getFirstName() : message.getSenderChat().getTitle();
        updateUser = usersInfo.getUser(chatID, userID) == null ? usersInfo.addUser(update) : usersInfo.getUser(chatID, userID);
        if (updateUser == null) /**/ return;
        /*
        if (!updateName.equalsIgnoreCase(updateUser.getUserName().toLowerCase()))
        {
            updateUser.addToKnowAs(updateUser.getUserName());
            updateUser.setUserName(updateName);
            UserInfo.getInstance().storeMe(updateChat.getChatID(), updateUser.getUserID());
        }
         */
        if (message.getReplyToMessage() != null)
        {
            String replyUserID = message.getSenderChat() == null ? String.valueOf(message.getFrom().getId()) : String.valueOf(message.getSenderChat().getId());
            String replyUserName = message.getSenderChat() == null ? message.getFrom().getFirstName() : message.getSenderChat().getTitle();
            replyUser = usersInfo.getUser(chatID, replyUserID) == null ? usersInfo.addUser(update) : usersInfo.getUser(chatID, replyUserID);
            if (replyUser == null) /**/ return;
            /*
            if (!replyUserName.equalsIgnoreCase(replyUser.getUserName().toLowerCase()))
            {
                replyUser.addToKnowAs(replyUser.getUserName());
                replyUser.setUserName(replyUserName);
                UserInfo.getInstance().storeMe(updateChat.getChatID(), replyUser.getUserID());
            }
             */
        }
        currentRestrictionTime = updateUser.getRestrictionTime() != null ? updateUser.getRestrictionTime().getTimeInMillis() : 0L;
        if (currentRestrictionTime < currentTime)
        {
            currentRestrictionTime = 0L;
        }
        restricted = currentRestrictionTime > currentTime;
        //nextResetTime = updateUser.getNextMessageReset() != null ? updateUser.getNextMessageReset().getTimeInMillis() : 0;

        // MESSAGE VARIABLES
        messageID = String.valueOf(message.getMessageId());
        updateMessage = message.getText() != null ? message.getText() : message.getCaption() != null ? message.getCaption() : null;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + Math.toIntExact(updateChat.getResetTime()));
        nextResetTime = calendar.getTimeInMillis();
        userModeration = updateChat.getUserModeration();
        ignoreUsers = updateChat.getAdminsList();
        if (chatID.equals(String.valueOf("-1001604709313"))) // re chat
        {
            skip_check = true;
        }
        updateTime = update.getMessage() == null ? (long) update.getCallbackQuery().getMessage().getDate() : (long) update.getMessage().getDate();
    }

    public Chat getUpdateChat()
    {
        return updateChat;
    }

    public User getUpdateUser()
    {
        return updateUser;
    }

    public User getReplyUser()
    {
        return replyUser;
    }

    public String getUpdateMessage()
    {
        return updateMessage;
    }

    public Long getCurrentTime()
    {
        return currentTime;
    }

    public Long getNextResetTime()
    {
        return nextResetTime;
    }

    public Long getCurrentRestrictionTime()
    {
        return currentRestrictionTime;
    }

    public String getMessageID()
    {
        return messageID;
    }

    public Long getUpdateTime()
    {
        return updateTime;
    }

    public List<String> getIgnoreUsers()
    {
        return ignoreUsers;
    }

    public List<String> getUserModeration()
    {
        return userModeration;
    }

    public boolean isSkip_check()
    {
        return skip_check;
    }

    public boolean isRestricted()
    {
        return restricted;
    }

    public Message getMessage()
    {
        return message;
    }
}
