package com.index.model.holders;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ReportTicketHolder {
    private long _id;
    private String _chatID;
    private String _requestorUserID;
    private String _reportedUserID;
    private Update _update;
    private Message _requstorMessage;
    private Message _reportedMessage;
    private boolean _status;
    private boolean _hasChange;

    public ReportTicketHolder(long id, Update update, boolean hasChange)
    {
        _id = id;
        _update = update;
        UpdateVariables uv = new UpdateVariables(update);
        _chatID = uv.getUpdateChat().getChatID();
        _requestorUserID = uv.getUpdateUser().getUserID();
        _reportedUserID = uv.getReplyUser().getUserID();
        _requstorMessage = uv.getMessage();
        _reportedMessage = update.getMessage().getReplyToMessage();
        _status = false;
        _hasChange = hasChange;
    }

    public ReportTicketHolder(long id, Message requestorMessage, boolean hasChange, boolean status)
    {
        _id = id;
        _update = null;
        _chatID = String.valueOf(requestorMessage.getChatId());
        _requstorMessage = requestorMessage;
        _requestorUserID = String.valueOf(requestorMessage.getSenderChat() == null ? requestorMessage.getFrom().getId() : requestorMessage.getSenderChat().getId());
        _reportedMessage = requestorMessage.getReplyToMessage();
        _reportedUserID = String.valueOf(_reportedMessage.getSenderChat() == null ? _reportedMessage.getFrom().getId() : _reportedMessage.getSenderChat().getId());
        _hasChange = hasChange;
        _status = status;
    }

    public long getID()
    {
        return _id;
    }

    public String getChatID() {
        return _chatID;
    }

    public String getRequestorUserID() {
        return _requestorUserID;
    }

    public String getReportedUserID() {
        return _reportedUserID;
    }

    /*
    public Update getUpdate()
    {
        return _update;
    }
    */

    public Message getRequestorMessage() {
        return _requstorMessage;
    }

    public Message getReportedMessage() {
        return _reportedMessage;
    }

    public boolean getStatus() {
        return _status;
    }

    public void setStatus(boolean status) {
        _status = status;
    }

    public void setChange(boolean change)
    {
        _hasChange = change;
    }
    public boolean getChange()
    {
        return _hasChange;
    }
}
