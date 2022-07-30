package com.index.model.holders;

import com.index.DataBaseConnection;
import com.index.Params;
import com.index.data.sql.ChatInfo;
import com.index.enums.RestrictionMediaType;
import com.index.sends.SendMessageMethod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestrictionMedia
{
    private final static String INSERT_URL_QUERY = "REPLACE INTO ban_files (chat_id, ids) VALUES (?,?)";
    private Map<RestrictionMediaType, List<String>> restrictionMedia;

    public RestrictionMedia(Map<RestrictionMediaType, List<String>> restrictionMedia)
    {
        this.restrictionMedia = restrictionMedia;
    }

    public List<String> getRestrictionMedia(RestrictionMediaType restrictionMediaType)
    {
        if (this.restrictionMedia == null || this.restrictionMedia.isEmpty() || this.restrictionMedia.get(restrictionMediaType) == null || this.restrictionMedia.get(restrictionMediaType).isEmpty())
        {
            return null;
        }
        return this.restrictionMedia.get(restrictionMediaType);
    }
    public boolean addToRestrictionMedia(RestrictionMediaType restrictionMediaType, String fileId)
    {
        if (this.restrictionMedia == null)
        {
            restrictionMedia = new HashMap<>();
        }
        try
        {
            List<String> restrictionIds = restrictionMedia.computeIfAbsent(restrictionMediaType, k -> new ArrayList<>());
            if (!restrictionIds.contains(fileId))
            {
                restrictionIds.add(fileId);
            }
            return true;
        }
        catch (Exception ex)
        {
            // Send Message
            ex.printStackTrace();
            return false;
        }
    }
    public boolean checkIsInRestrictionMedia(RestrictionMediaType restrictionMediaType, String fileId)
    {
        if (this.restrictionMedia == null || this.restrictionMedia.isEmpty() || this.restrictionMedia.get(restrictionMediaType) == null || this.restrictionMedia.get(restrictionMediaType).isEmpty())
        {
            return false;
        }
        List<String> filesIDs = this.restrictionMedia.get(restrictionMediaType);
        boolean is = filesIDs.contains(fileId);
        return is;
    }

    public void replaceID(RestrictionMediaType restrictionMediaType, String uniqueFileId, String fileID, String chatID)
    {
        List<String> filesIDs = this.restrictionMedia.get(restrictionMediaType);
        // int pos = filesIDs.lastIndexOf(uniqueFileId);
        filesIDs.remove(uniqueFileId);
        filesIDs.add(fileID);
        restrictionMedia.replace(restrictionMediaType, filesIDs);
        storeMe(chatID);
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
                    writeValues.append(";");
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
            new SendMessageMethod(0, Params.CHAT_YUMMY_CHAT_TECH, getClass().getSimpleName()  + ": Информация о всех запрещенных файлах сохранена в базе;");
        }
        catch (SQLException e)
        {
            new SendMessageMethod(0, Params.CHAT_YUMMY_CHAT_TECH, getClass().getSimpleName()  + ": Ошибка при сохранении информации о всех запрещенных файлах\n" + e);
        }
    }
}
