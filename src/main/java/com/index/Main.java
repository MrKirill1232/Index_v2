package com.index;


import com.index.data.sql.ChatInfo;
import com.index.data.sql.BannedFileInfo;
import com.index.data.sql.UserInfo;
import com.index.model.future.AutoSaveManager;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        Config.load();
        DataBaseConnection.init();
        ChatInfo.getInstance();
        UserInfo.getInstance();
        AutoSaveManager.getInstance();
        new BannedFileInfo();
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            IndexMain im = new IndexMain();
            botsApi.registerBot(im);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}