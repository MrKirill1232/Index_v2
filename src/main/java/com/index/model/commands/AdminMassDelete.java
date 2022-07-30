package com.index.model.commands;

import com.index.IndexMain;
import com.index.model.holders.UpdateVariables;
import com.index.sends.SendMessageMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AdminMassDelete
{
    private String chatID;
    private long messageID01;
    private long messageID02;

    public AdminMassDelete(Update update)
    {
        UpdateVariables variables = new UpdateVariables(update);
        if (variables.getUpdateChat() == null)
        {
            return;
        }
        String[] splitMessage = variables.getUpdateMessage().split(" ", 4);
        chatID = variables.getUpdateChat().getChatID();
        messageID01 = Long.parseLong(splitMessage[1]);
        messageID02 = Long.parseLong(splitMessage[2]);
        Executor service = Executors.newSingleThreadExecutor();
        service.execute(this::startToDelete);
    }

    private void startToDelete()
    {
        for (long ID = messageID01; ID <= messageID02; ID++)
        {
            new IndexMain().deleteMessage(chatID, ID);
        }
        new SendMessageMethod(0, chatID, getClass().getSimpleName() + ": Поток завершил выполнение, сообщения удалены;");
    }
}
