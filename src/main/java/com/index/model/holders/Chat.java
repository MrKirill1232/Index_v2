package com.index.model.holders;

import com.index.data.sql.ChatInfo;

import java.util.ArrayList;
import java.util.List;

public class Chat {

    private String chatID;
    private String chatName;
    private String chatUrl;
    private boolean needToStore;
    private List<String> adminsList;
    private List<String> userModeration;
    private boolean canSendStickers;
    private boolean canSendGifs;
    private int maxStickerCount;
    private int maxGifCount;
    private long resetTime; // время в минутах ? лучше переделать в календарь ? после которого идет сброс "отпралвенных" файлов для пользователя
    private RestrictionMedia restrictionMedia;
    private List<String> agreedStickers;

    public Chat(String chatID, String chatName, String chatUrl, boolean needToStore, List<String> adminsList, List<String> userModeration,
                boolean canSendStickers, boolean canSendGifs, int maxStickerCount, int maxGifCount, long resetTime, /*RestrictionMedia restrictionMedia,*/
                List<String> agreedStickers) {
        this.chatID = chatID;
        this.chatName = chatName;
        this.chatUrl = chatUrl;
        this.needToStore = needToStore;
        this.adminsList = adminsList;
        this.userModeration = userModeration;
        this.canSendStickers = canSendStickers;
        this.canSendGifs = canSendGifs;
        this.maxStickerCount = maxStickerCount;
        this.maxGifCount = maxGifCount;
        this.resetTime = resetTime;
        //this.restrictionMedia = restrictionMedia;
        this.agreedStickers = agreedStickers;
    }

    public String getChatID()
    {
        return this.chatID;
    }

    public void setChatID(String chatID)
    {
        this.chatID = chatID;
    }
    public String getChatName()
    {
        return this.chatName;
    }

    public void setChatName(String chatName)
    {
        this.chatName = chatName;
    }

    public String getChatUrl()
    {
        return this.chatUrl;
    }

    public void setChatUrl(String chatUrl)
    {
        this.chatUrl = chatUrl;
    }

    public boolean isNeedToStore()
    {
        return this.needToStore;
    }

    public void setNeedToStore(boolean needToStore)
    {
        this.needToStore = needToStore;
    }

    public List<String> getAdminsList()
    {
        return this.adminsList;
    }

    public void addToAdminList(String userId)
    {
        if (!this.adminsList.contains(userId)) this.adminsList.add(userId);
    }

    public void removeFromAdminList(String userId)
    {
        this.adminsList.remove(userId);
    }

    public void setAdminsList(List<String> adminsList)
    {
        this.adminsList = adminsList;
    }

    public boolean isUserAdmin(String userId)
    {
        return this.adminsList != null && this.adminsList.contains(userId);
    }

    public List<String> getUserModeration()
    {
        return this.userModeration;
    }

    public void addToUserModeration(String userId)
    {
        if (!this.userModeration.contains(userId)) this.userModeration.add(userId);
    }

    public void removeUserModeration(String userId)
    {
        this.userModeration.remove(userId);
    }

    public void setUserModeration(List<String> userModeration)
    {
        this.userModeration = userModeration;
    }

    public boolean isUserModerator(String userId)
    {
        return this.userModeration != null && this.userModeration.contains(userId);
    }

    public boolean isCanSendStickers()
    {
        return this.canSendStickers;
    }

    public void setCanSendStickers(boolean canSendStickers)
    {
        this.canSendStickers = canSendStickers;
    }

    public boolean isCanSendGifs()
    {
        return this.canSendGifs;
    }

    public void setCanSendGifs(boolean canSendGifs)
    {
        this.canSendGifs = canSendGifs;
    }

    public int getMaxStickerCount()
    {
        return this.maxStickerCount;
    }

    public void setMaxStickerCount(int maxStickerCount)
    {
        this.maxStickerCount = maxStickerCount;
    }

    public int getMaxGifCount()
    {
        return this.maxGifCount;
    }

    public void setMaxGifCount(int maxGifCount)
    {
        this.maxGifCount = maxGifCount;
    }

    public long getResetTime()
    {
        return this.resetTime;
    }

    public void setResetTime(long resetTime)
    {
        this.resetTime = resetTime;
    }

    public void setRestrictionMedia(RestrictionMedia restrictionMedia)
    {
        this.restrictionMedia = restrictionMedia;
    }
    public RestrictionMedia getRestrictionMedia()
    {
        return this.restrictionMedia;
    }

    public List<String> getAgreedStickerList()
    {
        return this.agreedStickers;
    }

    public void setAgreedStickers(List<String> list)
    {
        this.agreedStickers = new ArrayList<>();
        this.agreedStickers.addAll(list);
        ChatInfo.getInstance().storeMe(chatID);
    }

    public boolean isInAgreedStickerList(String fileId)
    {
        if (this.agreedStickers == null || this.agreedStickers.isEmpty())
        {
            return false;
        }
        return this.agreedStickers.contains(fileId);
    }

    public boolean addToAgreedStickerList(String fileId)
    {
        if (this.agreedStickers == null || this.agreedStickers.isEmpty())
        {
            this.agreedStickers = new ArrayList<>();
        }
        if (!this.agreedStickers.contains(fileId))
        {
            this.agreedStickers.add(fileId);
        }
        if (this.agreedStickers.contains(fileId))
        {
            ChatInfo.getInstance().storeMe(chatID);
            return true;
        }
        else return false;
    }

    public boolean removeFromAgreedStickerList(String fileId)
    {
        if (this.agreedStickers == null || this.agreedStickers.isEmpty() || !this.agreedStickers.contains(fileId))
        {
            return false;
        }
        this.agreedStickers.remove(fileId);
        if (!this.agreedStickers.contains(fileId))
        {
            ChatInfo.getInstance().storeMe(chatID);
            return true;
        }
        else return false;
    }

    public String getChatInfo()
    {
        StringBuilder returnString = new StringBuilder();
        returnString.append("Информация о чате ").append(chatName).append(":").append("\n");
        returnString.append("\t").append("Название чата - ").append(chatName).append(";").append("\n");
        returnString.append("\t").append("ID чата - ").append(chatID).append(";").append("\n");
        returnString.append("\t").append("Ссылка на чат - ").append(chatUrl != null ? chatUrl.isEmpty() ? "не установлено" : chatUrl : "не установлено").append(";").append("\n");
        returnString.append("\t").append("Статус автоматического сохранения - ").append(isNeedToStore() ? "включено" : "отключено").append(";").append("\n");
        returnString.append("\t").append("Статус отправки стикеров - ").append(canSendStickers ? "разрешены" : "запрещены").append(";").append("\n");
        returnString.append("\t").append("Статус отправки ГИФ-файлов - ").append(canSendGifs ? "разрешены" : "запрещены").append(";").append("\n");
        returnString.append("\t").append("Максимальное количество отправленных стикеров - ").append(maxStickerCount).append(";").append("\n");
        returnString.append("\t").append("Максимальное количество отправленных ГИФ-файлов - ").append(maxGifCount).append(";").append("\n");
        returnString.append("\t").append("Установленное время сброса для пользовательских параметров - ").append(resetTime).append("мин").append(";").append("\n");
        returnString.append("\t").append("Количество указанных администраторов - ").append(adminsList.size()).append(";").append("\n");
        returnString.append("\t").append("Количество указанных модераторов - ").append(userModeration.size()).append(";").append("\n");
        return returnString.toString();
    }
}
