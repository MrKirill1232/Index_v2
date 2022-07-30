package com.index;


import com.index.data.sql.ChatInfo;
import com.index.data.sql.BannedFileInfo;
import com.index.data.sql.ReportTicketsInfo;
import com.index.data.sql.UserInfo;
import com.index.model.future.AutoSaveManager;
import com.index.sends.SendMessageMethod;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main
{
    public static void main(String[] args) {
        Config.load();
        DataBaseConnection.init();
        ChatInfo.getInstance();
        UserInfo.getInstance();
        AutoSaveManager.getInstance();
        ReportTicketsInfo.getInstance();
        new BannedFileInfo();
        try
        {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            IndexMain im = new IndexMain();
            botsApi.registerBot(im);
            botsApi.registerBot(new MrKirill1232IndexMain());
        } catch (TelegramApiException e)
        {
            e.printStackTrace();
        }
        if (Params.DEBUG)
        {
            new SendMessageMethod(1, Params.CHAT_YUMMY_CHAT_TECH, "Бот запущен в режиме отладки. Сообщения обрабатываются только от некоторых пользователей.");
        }
        else
        {
            new SendMessageMethod(1, Params.CHAT_YUMMY_CHAT_TECH, "Бот запущен в обычном режиме. Если были команды которые были не обработаны - повторите их.");
        }
    }
}