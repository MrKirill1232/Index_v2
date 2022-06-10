package com.index.data.sql;

import com.index.DataBaseConnection;
import com.index.IndexMain;
import com.index.model.holders.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.*;
import java.util.*;

public class ChatInfo {

    private static final String SELECT_QUERY = "SELECT * FROM chat_info";
    private static final String DELETE_QUERY_BY_CHAT = "DELETE FROM chat_info WHERE chat_id=?";
    private static final String INSERT_QUERY = "INSERT INTO chat_info " +
            "(chat_id,chat_name,chat_url,need_to_store,admins_list,user_moderation,can_send_stickers,can_send_gifs,max_sticker_count,max_gif_count,reset_time,aggredStickers)" +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

    private final Map<String, Chat> _template = new HashMap<>();
    public ChatInfo() {
        load();
        new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": Загружена информация о " + _template.size() + " чатах;");
    }

    protected void load () {
        _template.clear();
        try (Connection con = DataBaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rset = st.executeQuery(SELECT_QUERY))
        {
            while (rset.next())
            {
                String chatId = rset.getString("chat_id");
                if (_template.containsKey(chatId)) continue;
                String userAdminsString = rset.getString("admins_list");
                StringTokenizer admin_token = new StringTokenizer(userAdminsString == null ? "" : userAdminsString, ", ");
                List<String> admins_list = new ArrayList<>();
                while (admin_token.hasMoreTokens())
                {
                    admins_list.add(admin_token.nextToken());
                }
                String userModerationString = rset.getString("user_moderation");
                StringTokenizer user_token = new StringTokenizer(userModerationString == null ? "" : userModerationString, ", ");
                List<String> users_list = new ArrayList<>();
                while (user_token.hasMoreTokens())
                {
                    users_list.add(user_token.nextToken());
                }
                String agreedStickerString = rset.getString("aggredStickers");
                StringTokenizer agreedStickerToken = new StringTokenizer(agreedStickerString == null ? "" : agreedStickerString, ", ");
                List<String> agreedStickerList = new ArrayList<>();
                while (agreedStickerToken.hasMoreTokens())
                {
                    String stickerId = agreedStickerToken.nextToken();
                    if (agreedStickerList.contains(stickerId)) continue;
                    agreedStickerList.add(stickerId.replace("\"","").replace("\"", ""));
                }
                _template.put(chatId,
                        new Chat(
                                chatId,
                                rset.getString("chat_name"),
                                rset.getString("chat_url"),
                                rset.getBoolean("need_to_store"),
                                admins_list,
                                users_list,
                                rset.getBoolean("can_send_stickers"),
                                rset.getBoolean("can_send_gifs"),
                                rset.getInt("max_sticker_count"),
                                rset.getInt("max_gif_count"),
                                rset.getLong("reset_time"),
                                agreedStickerList));
            }
        }
        catch (Exception e)
        {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": Ошибка при загрузке информации о чатах с базы - " + e);
            System.out.println(getClass().getSimpleName() + ": Ошибка при загрузке информации о чатах с базы - " + e);
        }
    }


    public boolean storeMe (String chat_id)
    {
        try (Connection con = DataBaseConnection.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(DELETE_QUERY_BY_CHAT)) {
                st.setString(1, chat_id);
                st.execute();
            }
            Chat chat = _template.get(chat_id);
            try (PreparedStatement st = con.prepareStatement(INSERT_QUERY)) {
                StringBuilder admin_list = new StringBuilder();
                if (chat.getAdminsList() != null)
                {
                    for (String list : chat.getAdminsList())
                    {
                        admin_list.append(admin_list.isEmpty() ? "" : ", ").append(list);
                    }
                }
                StringBuilder moder_list = new StringBuilder();
                if (chat.getUserModeration() != null)
                {
                    for (String list : chat.getUserModeration())
                    {
                        moder_list.append(moder_list.isEmpty() ? "" : ", ").append(list);
                    }
                }
                StringBuilder agreedStickerList = new StringBuilder();
                if (chat.getAgreedStickerList() != null)
                {
                    for (String list : chat.getAgreedStickerList())
                    {
                        if (!agreedStickerList.isEmpty())
                        {
                            agreedStickerList.append(", ");
                        }
                        agreedStickerList.append("\"").append(list).append("\"");
                    }
                }
                st.setString(1, chat_id);
                st.setString(2, chat.getChatName());
                st.setString(3, chat.getChatUrl());
                st.setBoolean(4, chat.isNeedToStore());
                st.setString(5, admin_list.toString());
                st.setString(6, moder_list.toString());
                st.setBoolean(7, chat.isCanSendStickers());
                st.setBoolean(8, chat.isCanSendGifs());
                st.setInt(9, chat.getMaxStickerCount());
                st.setInt(10, chat.getMaxGifCount());
                st.setLong(11, chat.getResetTime());
                st.setString(12, agreedStickerList.toString());
                st.execute();
                System.out.println(getClass().getSimpleName() + ": Информация о чате " + chat.getChatName() + " сохранена в базе");
                new IndexMain().SendAnswer(chat_id, getClass().getSimpleName(), getClass().getSimpleName() + ": Информация о чате " + chat.getChatName() + " сохранена в базе");
                return true;
            }
        }
        catch (SQLException e)
        {
            System.out.println(getClass().getSimpleName()  + ": Ошибка при сохранении информации о чате " + _template.get(chat_id).getChatName() + " сохранена в базе\n" + e);
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName()  + ": Ошибка при сохранении информации о чате " + _template.get(chat_id).getChatName() + " сохранена в базе @MrKirill1232 \n" + e);
            return false;
        }
    }

    public static ChatInfo getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Chat getChat(String chatId)
    {
        return _template.get(chatId);
    }

    public Set<String> getChatsID()
    {
        return _template.keySet();
    }

    public boolean addChat(Update update)
    {
        Message temp = update.getMessage() != null ? update.getMessage() : update.getCallbackQuery() != null ? update.getCallbackQuery().getMessage() : null;
        if (temp == null)
        {
            return false;
        }
        _template.put(String.valueOf(temp.getChatId()), new Chat(String.valueOf(temp.getChatId()), temp.getChat().getTitle(), "", false, List.of(), List.of(), true, true, 999, 999, -1, List.of()));
        if (_template.containsKey(String.valueOf(temp.getChatId())))
        {
            storeMe(String.valueOf(temp.getChatId()));
            return true;
        }
        return false;
    }

    private static class SingletonHolder
    {
        protected static final ChatInfo INSTANCE = new ChatInfo();
    }
}
