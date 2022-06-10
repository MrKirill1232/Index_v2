package com.index.data.sql;

import com.index.DataBaseConnection;
import com.index.IndexMain;
import com.index.enums.RestrictionMediaType;
import com.index.model.holders.RestrictionMedia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BannedFileInfo {

    private static final String SELECT_TYPE_QUERY = "SELECT type FROM ban_files";
    private static final String SELECT_ID_QUERY = "SELECT ids FROM ban_files WHERE chat_id=?";
    private final static String DELETE_ALL_INFO_QUERY = "DELETE FROM ban_files";
    private final static String INSERT_URL_QUERY = "INSERT INTO ban_files (type, id) VALUES (?,?)";

    public BannedFileInfo() {
        load();
        StringBuilder st = new StringBuilder(": Загружены запрещенные файлы:\n");
        RestrictionMedia rm = ChatInfo.getInstance().getChat(new IndexMain().YummyChannel_CHAT).getRestrictionMedia();
        st.append("- Фото - ").append(rm.getRestrictionMedia(RestrictionMediaType.PHOTO) != null ? rm.getRestrictionMedia(RestrictionMediaType.PHOTO).size() : "").append("\n");
        st.append("- GIF - ").append(rm.getRestrictionMedia(RestrictionMediaType.GIF) != null ? rm.getRestrictionMedia(RestrictionMediaType.GIF).size() : "").append("\n");
        st.append("- Sticker - ").append(rm.getRestrictionMedia(RestrictionMediaType.STICKER) != null ? rm.getRestrictionMedia(RestrictionMediaType.STICKER).size() : "").append("\n");
        st.append("- Video - ").append(rm.getRestrictionMedia(RestrictionMediaType.VIDEO) != null ? rm.getRestrictionMedia(RestrictionMediaType.VIDEO).size() : "").append("\n");
        st.append("- ViaBOT - ").append(rm.getRestrictionMedia(RestrictionMediaType.BOT) != null ? rm.getRestrictionMedia(RestrictionMediaType.BOT).size() : "").append("\n");
        new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + st);
        System.out.println(getClass().getSimpleName() + st);
    }

    private void load() {
        try (Connection con = DataBaseConnection.getConnection();
                 PreparedStatement st = con.prepareStatement(SELECT_ID_QUERY)) {
            for (String chatID : ChatInfo.getInstance().getChatsID()) {
                st.setString(1, chatID);
                Map<RestrictionMediaType, List<String>> files = new HashMap<>();
                try (ResultSet rset = st.executeQuery()) {
                    while (rset.next()) {
                        String[] listOfIDs = rset.getString("ids").split(";");
                        for (RestrictionMediaType type : RestrictionMediaType.values()) {
                            try {
                                for (String fileID : listOfIDs[type.ordinal()].split(",")) {
                                    if (fileID.isEmpty() || fileID.isBlank()) {
                                        continue;
                                    }
                                    files.putIfAbsent(type, new ArrayList<>());
                                    files.get(type).add(fileID.replace("\"", "").replace("\"", "").replace(" ", ""));
                                }
                            } catch (ArrayIndexOutOfBoundsException ignored) {
                                // Игнорируем ибо может быть случай когда enum-ов может быть большем чем файлов;
                            }
                        }
                    }
                }
                ChatInfo.getInstance().getChat(chatID).setRestrictionMedia(new RestrictionMedia(files));
            }
        }
        catch (Exception e)
        {
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName() + ": Ошибка при получении ID файлов с базы данных - " + e);
            System.out.println(getClass().getSimpleName() + ": Ошибка при получении ID файлов с базы данных - " + e);
        }
    }

    public void storeMe(String chatId){
        try (Connection con = DataBaseConnection.getConnection()) {
            // Clear previous entries.
            try (PreparedStatement st = con.prepareStatement(DELETE_ALL_INFO_QUERY)) {
                st.execute();
            }
            try (PreparedStatement st = con.prepareStatement(INSERT_URL_QUERY)) {
                RestrictionMedia rm = ChatInfo.getInstance().getChat(chatId).getRestrictionMedia();
                for (RestrictionMediaType restrictionMediaType : RestrictionMediaType.values())
                {
                    if (rm.getRestrictionMedia(restrictionMediaType) == null)
                    {
                        continue;
                    }
                    for (String id : rm.getRestrictionMedia(restrictionMediaType))
                    {
                        st.setString(1, restrictionMediaType.name());
                        st.setString(2, id);
                        st.execute();
                    }
                }
            }
            System.out.println(getClass().getSimpleName()  + ": Информация о всех запрещенных файлах сохранена в базе");
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName()  + ": Информация о всех запрещенных файлах сохранена в базе");
        }
        catch (SQLException e)
        {
            System.out.println(getClass().getSimpleName()  + ": Ошибка при сохранении информации о всех запрещенных файлах\n" + e);
            new IndexMain().SendAnswer(new IndexMain().YummyReChat, getClass().getSimpleName(), getClass().getSimpleName()  + ": Ошибка при сохранении информации о всех запрещенных файлах @MrKirill1232 \n" + e);
        }
    }
}
