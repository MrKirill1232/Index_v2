package com.index.data.sql;

import com.index.DataBaseConnection;
import com.index.IndexMain;
import com.index.enums.RestrictionType;
import com.index.model.holders.User;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.*;
import java.util.*;

public class UserInfo {

    private static final String SELECT_CHAT_QUERY = "SELECT chat_id FROM user_params";
    private static final String SELECT_INFO_QUERY = "SELECT * FROM user_params WHERE chat_id=?";
    private static final String INSERT_INFO_QUERY = "INSERT INTO user_params (chat_id, user_name, user_id, sticker_count, gif_count, next_message_reset, restriction_type, restriction_time, know_as, ignore_check) VALUES (?,?,?,?,?,?,?,?,?,?)";
    private final static String DELETE_INFO_QUERY = "DELETE FROM user_params WHERE chat_id=? AND user_id=?";
    private final static String DELETE_CHAT_INFO_QUERY = "DELETE FROM user_params WHERE chat_id=?";
    private final static String DELETE_ALL_INFO_QUERY = "DELETE FROM user_params";

    private Map<String, Map<String, User>> _template = new HashMap<>();

    protected UserInfo(){
        load();
        new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), "Загружено информация пользователях для " + _template.size() + " чатов;");
    }

    private void load(){
        _template.clear();
        List<String> chatIdList = new ArrayList<>();
        try (Connection con = DataBaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rset = st.executeQuery(SELECT_CHAT_QUERY))
        {
            while (rset.next()){
                final String chatId = String.valueOf(rset.getLong("chat_id"));
                if ( !chatIdList.isEmpty() && chatIdList.contains(chatId) ){
                    continue;
                }
                chatIdList.add(chatId);
            }
        }
        catch (Exception e)
        {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": Ошибка при получении ID чатов с базы данных - " + e);
            System.out.println(getClass().getSimpleName() + ": Ошибка при получении ID чатов с базы данных - " + e);
        }

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(SELECT_INFO_QUERY)) {
            for (String chatId : chatIdList) {
                st.setString(1, chatId);
                try (ResultSet rset = st.executeQuery()){
                    Map<String, User> temporary = new HashMap<>();
                    while (rset.next()){
                        List<String> know_as = new ArrayList<>();
                        /*
                        StringTokenizer know_as_tokens = new StringTokenizer(rset.getString("know_as"));
                        while ( know_as_tokens.hasMoreTokens() ){
                            know_as.add(know_as_tokens.nextToken());
                        }
                        */
                        know_as = null;
                        String userID = rset.getString("user_id");
                        temporary.put(userID,
                                new User(userID,
                                        rset.getString("user_name"),
                                        rset.getInt("sticker_count"),
                                        rset.getInt("gif_count"),
                                        rset.getString("next_message_reset"),
                                        RestrictionType.getRestrictionType(rset.getInt("restriction_type")),
                                        rset.getString("restriction_time"),
                                        know_as,
                                        rset.getBoolean("ignore_check")));
                    }
                    _template.put(chatId, temporary);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void storeChat(String chat_id){
        try (Connection con = DataBaseConnection.getConnection())
        {
            // Clear previous entries.
            try (PreparedStatement st = con.prepareStatement(DELETE_CHAT_INFO_QUERY))
            {
                st.setString(1, chat_id);
                st.execute();
            }
            // Insert all info back.
            try (PreparedStatement st = con.prepareStatement(INSERT_INFO_QUERY)) {
                st.setString(1, chat_id);
                for ( String user_id : _template.get(chat_id).keySet() ) {
                    User user = _template.get(chat_id).get(user_id);
                    st.setString(2, user.getUserName() == null ? "" : user.getUserName());
                    st.setString(3, user_id);
                    st.setInt(4, user.getStickerCount());
                    st.setInt(5, user.getGifCount());
                    st.setString(6, String.valueOf(user.getNextMessageReset().getTimeInMillis()));
                    st.setInt(7, RestrictionType.getRestrictionType(user.getRestrictionType()));
                    st.setString(8, String.valueOf(user.getRestrictionTime().getTimeInMillis()));
                    st.setString(9, "");
                    st.setBoolean(10, user.getIgnoreStickerCheck());
                    st.addBatch();
                }
                st.executeBatch();
            }
            System.out.println(getClass().getSimpleName()  + ": Информация о всех пользователя для чата " + chat_id + " была сохранены в базе");
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName()  + ": Информация о всех пользователя для чата " + chat_id + " была сохранены в базе");

        }
        catch (SQLException e)
        {
            System.out.println(getClass().getSimpleName()  + ": Ошибка при сохранении информации о пользователях для чата " + chat_id + "\n" + e);
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName()  + ": Ошибка при сохранении информации о пользователях для чата " + chat_id + " @MrKirill1232 \n" + e);
        }
    }

    public void storeAll(){
        try (Connection con = DataBaseConnection.getConnection())
        {
            // Clear previous entries.
            try (PreparedStatement st = con.prepareStatement(DELETE_ALL_INFO_QUERY))
            {
                st.execute();
            }
            // Insert all info back.
            for ( String chat_id : _template.keySet() ) {
                if ( Long.parseLong(chat_id) > 0) {
                    continue;
                }
                try (PreparedStatement st = con.prepareStatement(INSERT_INFO_QUERY)) {
                    st.setString(1, chat_id);
                    for (String user_id : _template.get(chat_id).keySet()) {
                        User user = _template.get(chat_id).get(user_id);
                        st.setString(2, user.getUserName() == null ? "" : user.getUserName());
                        st.setString(3, user_id);
                        st.setInt(4, user.getStickerCount());
                        st.setInt(5, user.getGifCount());
                        st.setString(6, String.valueOf(user.getNextMessageReset().getTimeInMillis()));
                        st.setInt(7, RestrictionType.getRestrictionType(user.getRestrictionType()));
                        st.setString(8, String.valueOf(user.getRestrictionTime().getTimeInMillis()));
                        st.setString(9, "");
                        st.setBoolean(10, user.getIgnoreStickerCheck());
                        st.addBatch();
                    }
                    st.executeBatch();
                }
                System.out.println(getClass().getSimpleName()  + ": Информация о всех пользователя для чата " + chat_id + " была сохранены в базе");
                new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName()  + ": Информация о всех пользователя для чата " + chat_id + " была сохранены в базе");
            }
        }
        catch (SQLException e)
        {
            System.out.println(getClass().getSimpleName()  + ": Ошибка при сохранении информации о пользователях для всех чатов \n" + e);
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName()  + ": Ошибка при сохранении информации о пользователях для всех чатов @MrKirill1232 \n" + e);
        }
    }

    public boolean storeMe(String chat_id, String user_id)
    {
        try (Connection con = DataBaseConnection.getConnection())
        {
            // Clear previous entries.
            try (PreparedStatement st = con.prepareStatement(DELETE_INFO_QUERY))
            {
                st.setString(1, chat_id);
                st.setString(2, user_id);
                st.execute();
            }
            User user = getUser(chat_id, user_id);
            // Insert all url back.
            try (PreparedStatement st = con.prepareStatement(INSERT_INFO_QUERY))
            {
                st.setString(1, chat_id);
                st.setString(2, user.getUserName());
                st.setString(3, user_id);
                st.setInt(4, user.getStickerCount());
                st.setInt(5, user.getGifCount());
                st.setString(6, String.valueOf(user.getNextMessageReset().getTimeInMillis()));
                st.setInt(7, RestrictionType.getRestrictionType(user.getRestrictionType()));
                st.setString(8, String.valueOf(user.getRestrictionTime().getTimeInMillis()));
                st.setString(9, "");
                st.setBoolean(10, user.getIgnoreStickerCheck());
                st.execute();
            }
            System.out.println(getClass().getSimpleName()  + ": Информация о пользователе " + user_id + " " + getUser(chat_id, user_id).getUserName() + " для чата " + chat_id + " сохранена в базе");
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName()  + ": Информация о пользователе " + user_id + " " + getUser(chat_id, user_id).getUserName() + " для чата " + chat_id + " сохранена в базе");
            return true;
        }
        catch (SQLException e)
        {
            System.out.println(getClass().getSimpleName()  + ": Ошибка при сохранении информации о пользователе " + user_id + " " + getUser(chat_id, user_id).getUserName() + " для чата " + chat_id + "\n" + e);
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName()  + ": Ошибка при сохранении информации о пользователе " + user_id + " " + getUser(chat_id, user_id).getUserName() + " для чата " + chat_id + " @MrKirill1232 \n" + e);
            return false;
        }
    }

    public User getUser(String chatId, String userId)
    {
        if (_template.get(chatId) == null || _template.get(chatId).get(userId) == null)
        {
            return null;
        }
        return _template.get(chatId).get(userId);
    }

    public User addUser(Update update)
    {
        Message temp = update.getMessage() != null ? update.getMessage() : update.getCallbackQuery() != null ? update.getCallbackQuery().getMessage() : null;
        if (temp == null) return null;
        String chat_id = String.valueOf(update.getMessage().getChatId());
        String userId = temp.getSenderChat() == null ? String.valueOf(temp.getFrom().getId()) : String.valueOf(temp.getSenderChat().getId());
        _template.computeIfAbsent(chat_id, k -> new HashMap<>());
        if (_template.get(chat_id) != null && _template.get(chat_id).get(userId) != null)
        {
            if (temp.getReplyToMessage() != null)
            {
                userId = temp.getReplyToMessage().getSenderChat() == null ? String.valueOf(temp.getReplyToMessage().getFrom().getId()) : String.valueOf(temp.getReplyToMessage().getSenderChat().getId());
            }
            if (_template.get(chat_id).get(userId) == null)
            {
                String userName = temp.getReplyToMessage() == null ? null : temp.getReplyToMessage().getSenderChat() == null ? temp.getReplyToMessage().getFrom().getFirstName() : temp.getReplyToMessage().getSenderChat().getTitle();
                try
                {
                    _template.get(chat_id).put(userId, new User(userId, userName, 0, 0, "0", RestrictionType.NULL, "0", List.of(), false));
                    return _template.get(chat_id).get(userId);
                } catch (Exception e)
                {
                    new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": Ошибка при сохранении пользователя " + userName + " " + userId + " с ответа для чата " + chat_id + " @MrKirill1232 \n");
                    return null;
                }
            }
            User user = _template.get(chat_id).get(userId);
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName()  + ": Странное создание нового пользователя " + user.getUserID() + " " + user.getUserName() + " для чата " + chat_id + " @MrKirill1232 \n");
            return _template.get(chat_id).get(userId);
        }
        String userName = temp.getSenderChat() == null ? temp.getFrom().getFirstName() : temp.getSenderChat().getTitle();
        try
        {
            _template.get(chat_id).put(userId, new User(userId, userName, 0, 0, "0", RestrictionType.NULL, "0", List.of(), false));
            return _template.get(chat_id).get(userId);
        }
        catch (Exception e)
        {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName()  + ": Ошибка при сохранении пользователя " + userName + " " + userId + " для чата " + chat_id + " @MrKirill1232 \n");
            return null;
        }
    }

    public String getAllTemplate(){
        StringBuilder umu = new StringBuilder();
        for ( String chat : _template.keySet() ){
            umu.append("CHAT: ").append(chat).append("\n").append("---").append("\n");
            for ( String user : _template.get(chat).keySet() ){
                umu.append("USER - ").append(user).append("\n");
                umu.append(_template.get(chat).get(user).getAllInfo());
                umu.append("\n").append("---").append("\n");
            }
        }
        return umu.toString();
    }

    public static UserInfo getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder
    {
        protected static final UserInfo INSTANCE = new UserInfo();
    }
}
