package com.index.model.future;

import com.index.IndexMain;
import com.index.data.sql.UserInfo;

import java.util.Calendar;
import java.util.concurrent.*;

public class AutoSaveManager
{
    public boolean sends = false;
    protected AutoSaveManager()
    {
        ScheduledExecutorService save = Executors.newScheduledThreadPool(1);
        save.scheduleWithFixedDelay(this::save, 60, 60, TimeUnit.MINUTES);
        new IndexMain().SendAnswer(new IndexMain().YummyReChat, "Index", "Следующее сохранение запланировано " + getNextSave().getTime() + ";");
    }

    public void save()
    {
        new IndexMain().SendAnswer(new IndexMain().YummyReChat, "Index", "Следующее сохранение запланировано " + getNextSave().getTime() + ";");
        UserInfo.getInstance().storeAll();
    }

    public Calendar getNextSave()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY,1);
        return calendar;
    }

    public static AutoSaveManager getInstance() {
        return AutoSaveManager.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder
    {
        protected static final AutoSaveManager INSTANCE = new AutoSaveManager();
    }
}
