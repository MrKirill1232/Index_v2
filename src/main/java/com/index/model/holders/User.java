package com.index.model.holders;

import com.index.enums.RestrictionType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class User {
    private String userID;
    private String userName;
    private int stickerCount;
    private int gifCount;
    private String nextMessageReset;
    private RestrictionType restrictionType;
    private String restrictionTime;
    private List<String> knowAs;
    private boolean ignoreStickerCheck;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss MM/dd/yyyy", Locale.getDefault(Locale.Category.FORMAT));

    public User(String userID, String userName, int stickerCount, int gifCount, String nextMessageReset, RestrictionType restrictionType, String restrictionTime, List<String> knowAs, boolean ignoreStickerCheck)
    {
        this.userID = userID;
        this.userName = userName;
        this.stickerCount = stickerCount;
        this.gifCount = gifCount;
        this.nextMessageReset = nextMessageReset;
        this.restrictionType = restrictionType;
        this.restrictionTime = restrictionTime;
        this.knowAs = knowAs;
        this.ignoreStickerCheck = ignoreStickerCheck;
    }
    public String getUserID()
    {
        return this.userID;
    }

    public void setUserID(String userID)
    {
        this.userID = userID;
    }

    public String getUserName()
    {
        return this.userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public int getStickerCount()
    {
        return this.stickerCount;
    }

    public void setStickerCount(int stickerCount)
    {
        this.stickerCount = stickerCount;
    }

    public int getGifCount()
    {
        return this.gifCount;
    }

    public void setGifCount(int gifCount)
    {
        this.gifCount = gifCount;
    }

    public Calendar getNextMessageReset()
    {
        Calendar calendar = Calendar.getInstance();
        if (!nextMessageReset.isEmpty())
        {
            calendar.setTimeInMillis(Long.parseLong(nextMessageReset));
        }
        else return null;
        return calendar;
    }

    public void setNextMessageReset(Calendar nextMessageReset)
    {
        this.nextMessageReset = String.valueOf(nextMessageReset.getTimeInMillis());
    }

    public void setNextMessageReset(String nextMessageReset)
    {
        this.nextMessageReset = nextMessageReset;
    }

    public void setNextMessageReset(long nextMessageReset)
    {
        this.nextMessageReset = String.valueOf(nextMessageReset);
    }

    public RestrictionType getRestrictionType()
    {
        return this.restrictionType;
    }

    public void setRestrictionType(RestrictionType restrictionType)
    {
        this.restrictionType = restrictionType;
    }

    public void setRestrictionType(int restrictionType)
    {
        this.restrictionType = RestrictionType.getRestrictionType(restrictionType);
    }

    public Calendar getRestrictionTime()
    {
        Calendar calendar = Calendar.getInstance();
        if (!restrictionTime.isEmpty())
        {
            calendar.setTimeInMillis(Long.parseLong(restrictionTime));
        }
        else return null;
        return calendar;
    }

    public void setRestrictionTime(Calendar restrictionTime)
    {
        this.restrictionTime = String.valueOf(restrictionTime.getTimeInMillis());
    }

    public void setRestrictionTime(String restrictionTime)
    {
        this.restrictionTime = restrictionTime;
    }

    public void setRestrictionTime(long restrictionTime)
    {
        this.restrictionTime = String.valueOf(restrictionTime);
    }

    public List<String> getKnowAs()
    {
        return this.knowAs;
    }

    public void setKnowAs(List<String> knowAs)
    {
        this.knowAs = knowAs;
    }

    public void addToKnowAs(String knowAsValue)
    {
        if (this.knowAs == null)
        {
            knowAs = new ArrayList<>();
        }
        this.knowAs.add(knowAsValue);
    }

    public boolean getIgnoreStickerCheck()
    {
        return this.ignoreStickerCheck;
    }

    public void setIgnoreStickerCheck(boolean ignoreStickerCheck)
    {
        this.ignoreStickerCheck = ignoreStickerCheck;
    }

    public String getAllInfo(){
        return  "Базовая информация о пользователи:\n" +
                "\tИмя пользователя - " + getUserName() + "\n" +
                "\tИД пользователя - " + getUserID() + "\n" +
                "\tСсылка на пользователя - " + "<a href=\"tg://user?id=" + getUserID() + "\">" + getUserName() + "</a>" + "\n" +
                "\tСсылка на пользователя - " + "<a href=\"tg://channel?id=" + getUserID() + "\">" + getUserName() + "</a>" + "\n" +
                "\tИзвестен как - " + (getKnowAs() == null ? "" : getKnowAs()) + "\n" +
                "Информация о модерации:\n" +
                "\tСтатус модерации стикеров - " + (getIgnoreStickerCheck() ? "не проверяются;" : "проверяются") + "\n" +
                "\tКакие ограничения применяются - " + (getRestrictionType()) + "\n" +
                (getRestrictionType() == RestrictionType.NULL ?
                        "" :
                        (getRestrictionTime().getTimeInMillis() > System.currentTimeMillis() ?
                                "\tВремя снятия ограничений - " +
                                        (getRestrictionTime() != null ?
                                                DATE_FORMAT.format(getRestrictionTime().getTime()) + " - " + getRestrictionTime().getTimeInMillis() :
                                                "0") :
                                "") + "\n") +
                "\tВремя следующего сброса параметров ниже - " + (getNextMessageReset() != null ?  DATE_FORMAT.format(getNextMessageReset().getTime()) : "0") + "\n" +
                "\tКоличество отправленных стикеров - " + getStickerCount() + "\n" +
                "\tКоличество отправленных GIF файлов - " + getGifCount() + "\n";
    }
}
