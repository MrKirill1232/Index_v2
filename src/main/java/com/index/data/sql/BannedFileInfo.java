package com.index.data.sql;

import com.index.DataBaseConnection;
import com.index.IndexMain;
import com.index.Params;
import com.index.enums.RestrictionMediaType;
import com.index.model.forwarding.Send;
import com.index.model.holders.RestrictionMedia;
import com.index.sends.SendMessageMethod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class BannedFileInfo {

    private static final String SELECT_ID_QUERY = "SELECT ids FROM ban_files WHERE chat_id=?";
    private final static String INSERT_URL_QUERY = "REPLACE INTO ban_files (chat_id, ids) VALUES (?,?)";

    public BannedFileInfo() {
        load();
        StringBuilder st = new StringBuilder(": Загружены запрещенные файлы:\n");
        RestrictionMedia rm = ChatInfo.getInstance().getChat(Params.CHAT_YUMMY_CHAT).getRestrictionMedia();
        st.append("- Фото - ").append(rm.getRestrictionMedia(RestrictionMediaType.PHOTO) != null ? rm.getRestrictionMedia(RestrictionMediaType.PHOTO).size() : "").append("\n");
        st.append("- GIF - ").append(rm.getRestrictionMedia(RestrictionMediaType.GIF) != null ? rm.getRestrictionMedia(RestrictionMediaType.GIF).size() : "").append("\n");
        st.append("- Sticker - ").append(rm.getRestrictionMedia(RestrictionMediaType.STICKER) != null ? rm.getRestrictionMedia(RestrictionMediaType.STICKER).size() : "").append("\n");
        st.append("- Video - ").append(rm.getRestrictionMedia(RestrictionMediaType.VIDEO) != null ? rm.getRestrictionMedia(RestrictionMediaType.VIDEO).size() : "").append("\n");
        st.append("- ViaBOT - ").append(rm.getRestrictionMedia(RestrictionMediaType.BOT) != null ? rm.getRestrictionMedia(RestrictionMediaType.BOT).size() : "").append("\n");
        new SendMessageMethod(1, Params.CHAT_YUMMY_CHAT_TECH, getClass().getSimpleName() + st);
        new SendMessageMethod(1, Params.CHAT_YUMMY_CHAT_TECH, "------------------------------");
        st = new StringBuilder(": Загружены запрещенные файлы:\n");
        rm = ChatInfo.getInstance().getChat(Params.CHAT_YUMMY_CHAT_TECH).getRestrictionMedia();
        st.append("- Фото - ").append(rm.getRestrictionMedia(RestrictionMediaType.PHOTO) != null ? rm.getRestrictionMedia(RestrictionMediaType.PHOTO).size() : "").append("\n");
        st.append("- GIF - ").append(rm.getRestrictionMedia(RestrictionMediaType.GIF) != null ? rm.getRestrictionMedia(RestrictionMediaType.GIF).size() : "").append("\n");
        st.append("- Sticker - ").append(rm.getRestrictionMedia(RestrictionMediaType.STICKER) != null ? rm.getRestrictionMedia(RestrictionMediaType.STICKER).size() : "").append("\n");
        st.append("- Video - ").append(rm.getRestrictionMedia(RestrictionMediaType.VIDEO) != null ? rm.getRestrictionMedia(RestrictionMediaType.VIDEO).size() : "").append("\n");
        st.append("- ViaBOT - ").append(rm.getRestrictionMedia(RestrictionMediaType.BOT) != null ? rm.getRestrictionMedia(RestrictionMediaType.BOT).size() : "").append("\n");
        new SendMessageMethod(1, Params.CHAT_YUMMY_CHAT_TECH, getClass().getSimpleName() + st);
    }

    private void load()
    {
        try (Connection con = DataBaseConnection.getConnection();
                 PreparedStatement st = con.prepareStatement(SELECT_ID_QUERY))
        {
            for (String chatID : ChatInfo.getInstance().getChatsID())
            {
                st.setString(1, chatID);
                Map<RestrictionMediaType, List<String>> files = new HashMap<>();
                try (ResultSet rset = st.executeQuery())
                {
                    while (rset.next())
                    {
                        String[] listOfIDs = rset.getString("ids").split(";");
                        for (String ID : listOfIDs)
                        {
                            String[] splitIDs = ID.split(":");
                            String umu = splitIDs[0].replace(" ", "").replace(" ", "");
                            RestrictionMediaType rmt = RestrictionMediaType.valueOf(umu);
                            ArrayList<String> temp_file_ids = new ArrayList<>();
                            String[] ids = splitIDs[1].split(",");
                            for (String id : ids)
                            {
                                temp_file_ids.add(id.replace("\"", "").replace("\"", "").replace(" ", ""));
                            }
                            files.put(rmt, temp_file_ids);
                        }
                    }
                }
                ChatInfo.getInstance().getChat(chatID).setRestrictionMedia(new RestrictionMedia(files));
            }
        }
        catch (Exception e)
        {
            new SendMessageMethod(1, Params.CHAT_YUMMY_CHAT_TECH, getClass().getSimpleName(), getClass().getSimpleName() + ": Ошибка при получении ID файлов с базы данных - " + e);
            // System.out.println(getClass().getSimpleName() + ": Ошибка при получении ID файлов с базы данных - " + e);
        }
    }

    public void storeMe(String chatId)
    {
        RestrictionMedia rm = ChatInfo.getInstance().getChat(chatId).getRestrictionMedia();
        StringBuilder writeValues = new StringBuilder();
        for (RestrictionMediaType rmt : RestrictionMediaType.values())
        {
            if (rm.getRestrictionMedia(rmt) == null || rm.getRestrictionMedia(rmt).isEmpty())
            {
                continue;
            }
            writeValues.append(rmt.name()).append(": ");
            List<String> IDs = rm.getRestrictionMedia(rmt);
            for (String id : IDs)
            {
                writeValues.append("\"").append(id).append("\"");
                if (IDs.lastIndexOf(id) != IDs.size() - 1)
                {
                    writeValues.append(",");
                }
                else
                {
                    writeValues.append("; ");
                }
            }
        }
        try (Connection con = DataBaseConnection.getConnection())
        {
            try (PreparedStatement st = con.prepareStatement(INSERT_URL_QUERY))
            {
                st.setString(1, chatId);
                st.setString(2, writeValues.toString());
                st.execute();
                st.closeOnCompletion();
            }
            new SendMessageMethod(1, Params.CHAT_YUMMY_CHAT_TECH, getClass().getSimpleName()  + ": Информация о всех запрещенных файлах сохранена в базе;");
        }
        catch (SQLException e)
        {
            new SendMessageMethod(1, Params.CHAT_YUMMY_CHAT_TECH, getClass().getSimpleName()  + ": Ошибка при сохранении информации о всех запрещенных файлах\n" + e);
        }
    }
}
