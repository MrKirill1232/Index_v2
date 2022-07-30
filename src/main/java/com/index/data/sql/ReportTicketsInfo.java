package com.index.data.sql;

import com.index.DataBaseConnection;
import com.index.IndexMain;
import com.index.model.forwarding.Send;
import com.index.model.holders.ReportTicketHolder;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.ChatInviteLink;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReportTicketsInfo
{
    private static final String TICKET_SELECT_INFO = "SELECT * FROM report_tickets";
    private static final String TICKET_REPLACE_INFO = "REPLACE INTO report_tickets (`update`, `id`, `status`) VALUES (?, ?, ?)";
    private final Map<String, Map<String, ReportTicketHolder>> _reportTickets = new TreeMap<>(new TreeMap<>());
    private long _lastID = 0;

    public ReportTicketsInfo()
    {
        load();
    }

    private boolean isActual(long updateTime)
    {
        Calendar update_time_calendar = Calendar.getInstance();
        update_time_calendar.setTimeInMillis(updateTime * 1000L);
        update_time_calendar.add(Calendar.MINUTE, 10);
        long umu_1 = System.currentTimeMillis();
        return ((update_time_calendar.getTimeInMillis()) >= (umu_1));
    }
    public void addNewReport(Update update)
    {
        Message message = update.getMessage();
        if (message == null)
        {
            return;
        }
        String userID = message.getSenderChat() == null ? String.valueOf(message.getFrom().getId()) : String.valueOf(message.getSenderChat().getId());
        if (message.getReplyToMessage() == null)
        {
            if (!isActual(update.getCallbackQuery() != null && update.getCallbackQuery().getMessage() != null ? update.getCallbackQuery().getMessage().getDate() : 0))
            {
                return;
            }
            new IndexMain().SendAnswer(String.valueOf(message.getChatId()), UserInfo.getInstance().getUser(String.valueOf(message.getChatId()), userID).getUserName(), "Не вижу сообщения о котором нужно сообщить. Ответьте командой \"/report\" на нужное сообщение;");
            return;
        }
        ReportTicketHolder newHolder = new ReportTicketHolder(_lastID + 1L, message, true, false);
        if (_reportTickets.getOrDefault(newHolder.getChatID(), null) == null)
        {
            _reportTickets.put(newHolder.getChatID(), new HashMap<>());
        }
        Map<String, ReportTicketHolder> tickets = _reportTickets.get(newHolder.getChatID());
        if (tickets.containsKey(String.valueOf(newHolder.getReportedMessage().getMessageId())))
        {
            new IndexMain().SendAnswer(newHolder.getChatID(), UserInfo.getInstance().getUser(newHolder.getChatID(), newHolder.getRequestorUserID()).getUserName(), "На это сообщение уже поступала жалоба;");
            return;
        }
        _lastID = _lastID + 1;
        tickets.put(String.valueOf(newHolder.getReportedMessage().getMessageId()), newHolder);
        _reportTickets.replace(newHolder.getChatID(), tickets);
        new IndexMain().SendAnswer(newHolder.getChatID(), "Index", "Спасибо за сообщение. Жалоба отправлена модератору;");
        sendMessageReportToTech(newHolder.getChatID(), String.valueOf(newHolder.getReportedMessage().getMessageId()));
        storeMe(newHolder.getChatID(), String.valueOf(newHolder.getReportedMessage().getMessageId()));
    }

    public void load()
    {
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(TICKET_SELECT_INFO);
             ResultSet rset = st.executeQuery())
        {
            while (rset.next())
            {
                try
                {
                    _lastID = rset.getLong("id");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return;
                }
                final boolean status = rset.getBoolean("status");
                if (status)
                {
                    //continue;
                }
                Message message = getMessageFromSerializeByteArray(rset.getBytes("update"));
                ReportTicketHolder rth = new ReportTicketHolder(_lastID, message, false, false);
                if (_reportTickets.containsKey(rth.getChatID()))
                {
                    Map<String, ReportTicketHolder> temp = _reportTickets.get(rth.getChatID());
                    temp.putIfAbsent(String.valueOf(rth.getReportedMessage().getMessageId()), rth);
                    _reportTickets.replace(rth.getChatID(), temp);
                }
                else
                {
                    Map<String, ReportTicketHolder> temp = new TreeMap<>();
                    temp.putIfAbsent(String.valueOf(rth.getReportedMessage().getMessageId()), rth);
                    _reportTickets.put(rth.getChatID(), temp);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    synchronized void storeMe()
    {
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement statement = con.prepareStatement(TICKET_REPLACE_INFO))
        {
            for (String chatID : _reportTickets.keySet())
            {
                for (String messageID : _reportTickets.get(chatID).keySet())
                {
                    ReportTicketHolder rth = _reportTickets.get(chatID).get(messageID);
                    if (!rth.getChange())
                    {
                        continue;
                    }
                    statement.setBytes(1, getSerializeByteArrayFromMessage(rth.getRequestorMessage()));
                    statement.setLong(2, rth.getID());
                    statement.setBoolean(3, rth.getStatus());
                    statement.addBatch();
                }
            }
            statement.executeBatch();
            statement.closeOnCompletion();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    synchronized void storeMe(String chatID, String messageID)
    {
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement statement = con.prepareStatement(TICKET_REPLACE_INFO))
        {
            ReportTicketHolder rth = _reportTickets.get(chatID).get(messageID);
            byte[] umu = getSerializeByteArrayFromMessage(rth.getRequestorMessage());
            statement.setBytes(1, umu);
            statement.setLong(2, rth.getID());
            statement.setBoolean(3, rth.getStatus());
            statement.execute();
            statement.closeOnCompletion();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void sendMessageReportToTech(String chatID, String messageID)
    {
        ReportTicketHolder holder = _reportTickets.get(chatID).get(messageID);
        SendMessage message = new SendMessage();
        message.setChatId(new IndexMain().YummyReChat);
        message.setReplyMarkup(getPresetWithGetMessage(chatID, String.valueOf(holder.getReportedMessage().getMessageId())));
        message.setText("ОТКРЫТО\n@MrKirill1232 @ReAltair\nНа сообщение https://t.me/c/" + holder.getChatID().replace("-100", "") + "/" + holder.getReportedMessage().getMessageId() + " поступила жалоба от пользователя " +
                "tg://user?id=" + holder.getReportedUserID() + " - " + UserInfo.getInstance().getUser(holder.getChatID(), holder.getReportedUserID()).getUserName() + ".");
        try
        {
            new IndexMain().execute(message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup getPresetWithGetMessage(String chatID, String messageID)
    {
        return new InlineKeyboardMarkup(List.of(
                List.of(generateButton("Закрыть", "reportticket;close_" + chatID + "_" + messageID)),
                List.of(generateButton("Репорт Сообщение", "reportticket;forwardReportMessage_" + chatID + "_" + messageID), generateButton("Реквест Сообщение", "reportticket;forwardRequestMessage_" + chatID + "_" + messageID)),
                List.of(generateButton("Репорт Юзер", "reportticket;forwardReportUser_" + chatID + "_" + messageID), generateButton("Реквест Юзер", "reportticket;forwardRequestUser_" + chatID + "_" + messageID)),
                List.of(generateButton("Обновить", "reportticket;update_" + chatID + "_" + messageID))
        ));
    }

    private InlineKeyboardMarkup getPresetForClose(String chatID, String messageID)
    {
        return new InlineKeyboardMarkup(List.of(
                List.of(generateButton("Репорт Сообщение", "reportticket;forwardReportMessage_" + chatID + "_" + messageID), generateButton("Реквест Сообщение", "reportticket;forwardRequestMessage_" + chatID + "_" + messageID)),
                List.of(generateButton("Репорт Юзер", "reportticket;forwardReportUser_" + chatID + "_" + messageID), generateButton("Реквест Юзер", "reportticket;forwardRequestUser_" + chatID + "_" + messageID)),
                List.of(generateButton("Обновить", "reportticket;update_" + chatID + "_" + messageID))
        ));
    }

    public void getUserFromRequest(Update update)
    {
        String dataMessage = update.getCallbackQuery().getData();
        String[] values = dataMessage.split("_");
        String chatID = values[1];
        String messageID = values[2];
        String updateChatID = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        ReportTicketHolder holder = _reportTickets.get(chatID).get(messageID);
        SendMessage message = new SendMessage();
        message.setChatId(updateChatID);
        message.enableHtml(true);
        message.setReplyToMessageId(update.getCallbackQuery().getMessage().getMessageId());
        if (values[0].contains("forwardReportUser"))
        {
            message.setText(UserInfo.getInstance().getUser(chatID, holder.getReportedUserID()).getAllInfo());
        }
        else if (values[0].contains("forwardRequestUser"))
        {
            message.setText(UserInfo.getInstance().getUser(chatID, holder.getRequestorUserID()).getAllInfo());
        }
        try
        {
            new IndexMain().execute(message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            new IndexMain().SendAnswer(String.valueOf(update.getCallbackQuery().getMessage().getChatId()), "", "Какая-то ошибка :)");
            return;
        }
    }
    public void closeRequest(Update update)
    {
        String dataMessage = update.getCallbackQuery().getData();
        String[] values = dataMessage.split("_");
        String chatID = values[1];
        String messageID = values[2];
        String updateChatID = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        ReportTicketHolder holder = _reportTickets.get(chatID).get(messageID);
        EditMessageText message = new EditMessageText();
        message.setChatId(updateChatID);
        message.setText("ЗАКРЫТО\nНа сообщение https://t.me/c/" + holder.getChatID().replace("-100", "") + "/" + holder.getReportedMessage().getMessageId() + " поступила жалоба от пользователя " +
                "tg://user?id=" + holder.getReportedUserID() + " - " + UserInfo.getInstance().getUser(holder.getChatID(), holder.getReportedUserID()).getUserName() + ".");
        message.setReplyMarkup(getPresetForClose(chatID, messageID));
        message.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        try
        {
            new IndexMain().execute(message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            new IndexMain().SendAnswer(String.valueOf(update.getCallbackQuery().getMessage().getChatId()), "", "Сообщение было удалено пользователем.");
            return;
        }
        Map<String, ReportTicketHolder> tickets = _reportTickets.get(holder.getChatID());
        holder.setStatus(true);
        tickets.replace(messageID, holder);
        _reportTickets.replace(holder.getChatID(), tickets);
        storeMe(chatID, messageID);
    }

    public void updateButtons(Update update)
    {
        String dataMessage = update.getCallbackQuery().getData();
        String[] values = dataMessage.split("_");
        String chatID = values[1];
        String messageID = values[2];
        String updateChatID = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        ReportTicketHolder holder = _reportTickets.get(chatID).get(messageID);
        EditMessageText message = new EditMessageText();
        message.setChatId(updateChatID);
        message.setText(update.getCallbackQuery().getMessage().getText());
        if (holder.getStatus())
        {
            message.setReplyMarkup(getPresetForClose(chatID, messageID));
        }
        else
        {
            message.setReplyMarkup(getPresetWithGetMessage(chatID, messageID));
        }
        message.enableHtml(true);
        message.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        try
        {
            new IndexMain().execute(message);
        }
        catch (Exception e)
        {
            new IndexMain().SendAnswer(String.valueOf(update.getCallbackQuery().getMessage().getChatId()), "", "Сообщение было удалено пользователем.");
        }
    }

    public void tryForwardMessage(Update update)
    {
        String dataMessage = update.getCallbackQuery().getData();
        String[] values = dataMessage.split("_");
        String chatID = values[1];
        String messageID = values[2];
        ReportTicketHolder holder = _reportTickets.get(chatID).get(messageID);
        ForwardMessage message = new ForwardMessage();
        if (values[0].contains("forwardReportMessage"))
        {
            message.setMessageId(holder.getReportedMessage().getMessageId());
        }
        else if (values[0].contains("forwardRequestMessage"))
        {
            message.setMessageId(holder.getRequestorMessage().getMessageId());
        }
        message.setFromChatId(chatID);
        message.setChatId(update.getCallbackQuery().getMessage().getChatId());
        try
        {
            new IndexMain().execute(message);
        }
        catch (Exception e)
        {
            new IndexMain().SendAnswer(String.valueOf(update.getCallbackQuery().getMessage().getChatId()), "", "Сообщение было удалено пользователем.");
            if (values[0].contains("forwardReportMessage"))
            {
                new Send(holder.getReportedMessage(), "Index", String.valueOf(update.getCallbackQuery().getMessage().getChatId()), update.getCallbackQuery().getMessage().getMessageId());
            }
            else if (values[0].contains("forwardRequestMessage"))
            {
                new Send(holder.getRequestorMessage(), "Index", String.valueOf(update.getCallbackQuery().getMessage().getChatId()), update.getCallbackQuery().getMessage().getMessageId());
            }
        }
    }

    private InlineKeyboardMarkup getInlineMarkup()
    {
        return new InlineKeyboardMarkup(List.of(
                List.of(generateButton("Закрыть", "reportticket;close")),
                List.of(generateButton("Обновить", "reportticket;update"))
        ));
    }

    private InlineKeyboardButton generateButton(String text, String data)
    {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(data);
        return button;
    }

    synchronized byte[] getSerializeByteArrayFromMessage(Message message)
    {
        try (
            ByteArrayOutputStream serializeByteArray = new ByteArrayOutputStream();
            ObjectOutputStream serializeMessage = new ObjectOutputStream(serializeByteArray)
        )
        {
            serializeMessage.writeObject(message);
            serializeMessage.flush();
            serializeMessage.close();
            return serializeByteArray.toByteArray();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    synchronized Message getMessageFromSerializeByteArray(byte[] byteArray)
    {
        if (byteArray != null)
        {
            try
            {
                ObjectInputStream byteArrayToSerialize = new ObjectInputStream(new ByteArrayInputStream(byteArray));
                Message message = (Message) byteArrayToSerialize.readObject();
                return message;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static ReportTicketsInfo getInstance() {
        return ReportTicketsInfo.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder
    {
        protected static final ReportTicketsInfo INSTANCE = new ReportTicketsInfo();
    }
}
