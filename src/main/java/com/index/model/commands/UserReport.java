package com.index.model.commands;

import com.index.data.sql.ReportTicketsInfo;
import com.index.model.holders.UpdateVariables;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UserReport
{
    public UserReport(Update update)
    {
        UpdateVariables variables = new UpdateVariables(update);
        if (variables.getUpdateChat() == null)
        {
            // new SendMessageMethod(0, chatID, getClass().getSimpleName() + ": Произошла ошибка, не могу найти чат. Повторите выполнение команды, после добавления чата в базу данных;");
            return;
        }
        if (variables.getReplyUser() == null)
        {
            return;
        }
        ReportTicketsInfo.getInstance().addNewReport(update);
    }
}
