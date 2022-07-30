package com.index.model.commands;

import com.index.model.holders.UpdateVariables;
import com.index.sends.SendMessageMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AdminPing
{
    public AdminPing(Update update)
    {
        UpdateVariables variables = new UpdateVariables(update);
        long responseTime = variables.getCurrentTime() - variables.getUpdateTime();
        String responseMessage = getClass().getSimpleName() + ": pong => " + responseTime + " ms / " + (responseTime / 1000L) + " s;";
        new SendMessageMethod(0, variables.getUpdateChat().getChatID(), responseMessage);
    }
}
