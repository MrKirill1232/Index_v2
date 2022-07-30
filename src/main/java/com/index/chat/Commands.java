package com.index.chat;

import com.index.model.commands.AdminChatAdminAdd;
import com.index.model.commands.AdminChatAdminList;
import com.index.model.commands.AdminChatAdminRemove;
import com.index.model.commands.AdminChatInfo;
import com.index.model.commands.AdminChatModerAdd;
import com.index.model.commands.AdminChatModerList;
import com.index.model.commands.AdminChatModerRemove;
import com.index.model.commands.AdminChatRestrictionSave;
import com.index.model.commands.AdminChatSetAdmin;
import com.index.model.commands.AdminMassDelete;
import com.index.model.commands.AdminMute;
import com.index.model.commands.AdminOTIIUCKA;
import com.index.model.commands.AdminPing;
import com.index.model.commands.AdminChatRestrictionAdd;
import com.index.model.commands.AdminStickersUpdateStatus;
import com.index.model.commands.AdminUnMute;
import com.index.model.commands.AdminUserGet;
import com.index.model.commands.AdminUserSave;
import com.index.model.commands.UserChatHelp;
import com.index.model.commands.UserReport;
import com.index.model.stickers.addSticker;
import com.index.model.stickers.clearSticker;
import com.index.model.stickers.delSticker;
import com.index.model.stickers.listSticker;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum Commands
{
    ADMIN_PING("//ping", "возвращает время выполнение команды;", 2, AdminPing.class, true, true),
    ADMIN_CHAT_INFO("//chat_info", "возвращает информацию о чате;", 2, AdminChatInfo.class, true, true),
    ADMIN_CHAT_MODER_ADD("//chat_moder_add", "добавляется пользователя в список пользовательской модерации, который может использовать некоторые команды;", 2, AdminChatModerAdd.class, true, false),
    ADMIN_CHAT_MODER_REMOVE("//chat_moder_remove", "исключает пользователя со списка пользовательской модерации;", 2, AdminChatModerRemove.class, true, false),
    ADMIN_CHAT_MODER_LIST("//chat_moder_list", "возвращает список пользователей с правами пользовательской модерации;", 2, AdminChatModerList.class, true, false),
    ADMIN_CHAT_ADMIN_ADD("//chat_admin_add", "добавляет пользователя в список администрации, который может использовать все команды, которые не ограничены на уровне кода;", 2, AdminChatAdminAdd.class, true, false),
    ADMIN_CHAT_ADMIN_REMOVE("//chat_admin_remove", "удаляет пользователя со списка администрации;", 2, AdminChatAdminRemove.class, true, false),
    ADMIN_CHAT_ADMIN_LIST("//chat_admin_list", "возвращает список пользователей с правами администратора;", 2, AdminChatAdminList.class, true, false),
    ADMIN_CHAT_ADD_TO_RESTRICTION_FILES("//chat_add_to", "добавляет ИД в список запрещенных;", 2, AdminChatRestrictionAdd.class, true, false),
    ADMIN_CHAT_SAVE_RESTRICTION_FILES("//chat_save_res", "сохраняет список запрещенных ИД в бд;", 2, AdminChatRestrictionSave.class, true, false),
    ADMIN_USER_GET("//user_get", "возвращает пользователя, можно использовать как с отвеченным, так и с ИД;", 2, AdminUserGet.class, true, false),
    ADMIN_USER_SAVE("//user_save", "сохраняет в БД всех пользователей чата, в который отправлена команда;", 2, AdminUserSave.class, true, false),
    ADMIN_MUTE("//mute", "запрещает писать пользователю в течении некоторого времени;", 2, AdminMute.class, true, false),
    ADMIN_UNMUTE("//unmute", "снимает ограничения на отправку сообщений, как и на уровне бота, так и на уровне чата;", 2, AdminUnMute.class, true, false),
    ADMIN_STICKER_ADD("//s_add", "добавляет УРЛ стикер-пака в список исключений;", 2, addSticker.class, true, false),
    ADMIN_STICKER_DELETE_01("//s_delete", "удаляет УРЛ стикер-пака со списка исключений;", 2, delSticker.class, true, false),
    ADMIN_STICKER_DELETE_02("//s_remove", "удаляет УРЛ стикер-пака со списка исключений;", 2, delSticker.class, true, false),
    ADMIN_STICKER_CLEAR("//s_clear", "очищает список стикеров от дубликатов;", 2, clearSticker.class, true, false),
    ADMIN_STICKER_LIST("//s_list", "возвращает список стикеров для чата;", 2, listSticker.class, true, false),
    ADMIN_STICKER_CHECK_UPDATE("//s_update", "меняет статус проверки стикеров при обработке сообщения;", 2, AdminStickersUpdateStatus.class, true, false),
    ADMIN_OTIIUCKA("//отписка", "возвращает сообщение;", 1, AdminOTIIUCKA.class, true, true),
    ADMIN_MASSIVE_DELETE("//mass_delete", "создает поток и удаляет множество сообщений. Использование //mass_delete ИДс_с_которого удалять ИДс_по_которое_удалять", 2, AdminMassDelete.class, true, true),
    MODER_MUTE("/mute", "запрещает писать пользователю в течении некоторого времени;", 1, AdminMute.class, true, false),
    USER_REPORT("/report", "отправляет сообщение на рассмотрение модераторам;", 0, UserReport.class, true, false),
    USER_CHAT_SET_ADMIN("//chat_set_admin", "добавляет текущую администрацию группы в список администрации бота. Ограничена использованием на уровне кода;", 0, AdminChatSetAdmin.class, true, false),
    USER_CHAT_HELP("/help", "возвращает это сообщение;", 1, UserChatHelp.class, true, true),
    USER_REQUEST_KICK("//chat_kick", "запускает голосование об исключении пользователя;", 0, AdminPing.class, true, true);



    private final String _command;
    private final String _comment;
    /**
     * 0 - user
     * 1 - moder
     * 2 - chat moder
     * 3 - chat admin
     **/
    private final int _accessLevel;
    private final Constructor<?> _constructor;
    private final Class _clazz;
    private final boolean _canCallInPublicGroup;
    private final boolean _canCallInPrivateChat;

    Commands(String command, String comment, int accessLevel, Class<?> clazz, boolean canCallInPublicGroup, boolean canCallInPrivateChat)
    {
        try
        {
            _constructor = clazz.getConstructor(Update.class);
        }
        catch (NoSuchMethodException e)
        {
            throw new Error(e);
        }
        _command = command;
        _comment = comment;
        _accessLevel = accessLevel;
        _clazz = clazz;
        _canCallInPublicGroup = canCallInPublicGroup;
        _canCallInPrivateChat = canCallInPrivateChat;
    }

    public String getCommand()
    {
        return _command;
    }

    public int getAccessLevel()
    {
        return _accessLevel;
    }

    public static Commands getCommandByString(String updateCommand)
    {
        for (Commands search : values())
        {
            if (updateCommand.startsWith(search.getCommand()))
            {
                return search;
            }
        }
        return null;
    }

    public String getComment()
    {
        return _comment;
    }

    public Class getClazz()
    {
        return _clazz;
    }

    public boolean isCanCallInPublicGroup()
    {
        return _canCallInPublicGroup;
    }

    public boolean isCanCallInPrivateChat()
    {
        return _canCallInPrivateChat;
    }


    public void makeCommand(Update update) throws InvocationTargetException, InstantiationException, IllegalAccessException
    {
        _constructor.newInstance(update);
    }
}
