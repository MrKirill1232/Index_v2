package com.index.model.forwarding;

import com.index.IndexMain;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Comparator;
import java.util.Objects;
import java.util.StringTokenizer;

public class Send {

    IndexMain im = new IndexMain();

    public Send(Update update, String name)
    {
        Message updateMessage = update.getMessage();
        if (updateMessage == null)
        {
            return;
        }
        new Send(updateMessage, name, String.valueOf(im.YummyChannel_CHAT), 0);
    }
    public Send(Message updateMessage, String Name, String chatID, int replyTo)
    {
        if (String.valueOf(updateMessage.getChatId()).equals(im.YummyReChat) && updateMessage.getReplyToMessage() != null )
        {
            final StringTokenizer st = new StringTokenizer(updateMessage.getReplyToMessage().getText());
            st.nextToken();
            st.nextToken();
            replyTo = Integer.parseInt(st.nextToken());
        }
        if (       updateMessage.hasContact()
                || updateMessage.hasDice()
                || updateMessage.hasLocation()
                || updateMessage.hasPassportData()
                || updateMessage.hasPoll()
                || updateMessage.hasReplyMarkup()
                || updateMessage.hasSuccessfulPayment()
                || updateMessage.hasInvoice()
                || updateMessage.hasVideoNote()
        )
        {
            im.SendAnswer(im.YummyReChat, Name, "Извиняюсь, не умею пересылать такое;");
            return;
        }
        //im.SendAnswer(im.YummyChannel_CHAT, "INDEX_BOT", Name+":");
        if ( updateMessage.hasText() || updateMessage.hasViaBot() )
        {
            im.SendAnswer(chatID, Name, updateMessage.getText(), "NONE", replyTo);
        }
        else if ( updateMessage.hasVoice() )
        {
            InputFile file = new InputFile();
            file.setMedia(updateMessage.getVoice().getFileId());
            SendVoice voice = SendVoice.builder()
                    .replyToMessageId(replyTo)
                    .voice(file)
                    .chatId(chatID)
                    .build();
            try
            {
                im.execute(voice);
            }
            catch (TelegramApiException e)
            {
                e.printStackTrace();
            }
        }
        else if ( updateMessage.hasAudio() )
        {
            InputFile file = new InputFile();
            file.setMedia(updateMessage.getAudio().getFileId());
            SendAudio audio = SendAudio.builder()
                    .replyToMessageId(replyTo)
                    .audio(file)
                    .chatId(chatID)
                    .build();
            try
            {
                im.execute(audio);
            }
            catch (TelegramApiException e)
            {
                e.printStackTrace();
            }
        }
        else if ( updateMessage.hasSticker() ) {
            InputFile file = new InputFile();
            file.setMedia(updateMessage.getSticker().getFileId());
            SendSticker sticker = SendSticker.builder()
                    .replyToMessageId(replyTo)
                    .sticker(file)
                    .chatId(chatID)
                    .build();
            try {
                im.execute(sticker);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else if ( updateMessage.hasAnimation() ) {
            InputFile file = new InputFile();
            file.setMedia(updateMessage.getAnimation().getFileId());
            SendAnimation animation = SendAnimation.builder()
                    .replyToMessageId(replyTo)
                    .animation(file)
                    .chatId(chatID)
                    .build();
            try {
                im.execute(animation);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else if ( updateMessage.hasPhoto() ) {
            org.telegram.telegrambots.meta.api.objects.InputFile file = new org.telegram.telegrambots.meta.api.objects.InputFile();
            //java.io.File photo_f = new File("photo_2022-02-06_17-23-09.jpg");
            file.setMedia(Objects.requireNonNull(updateMessage.getPhoto().stream().max(Comparator.comparing(PhotoSize::getFileId)).orElse(null)).getFileId());
            //file.setMedia(photo_f);
            //String file_id = "AgACAgIAAx0CUTEAAUYAAjZYYhMZRLHAHhL55WCA2ue4sgqndb0AAqu5MRvmpJhIcW22U_il1AgBAAMCAANzAAMjBA";
            //file.setMedia(file_id);
            //im.SendAnswer(im.YummyReChat, "", update.getMessage().getPhoto().stream().max(Comparator.comparing(PhotoSize::getFileId)).orElse(null).getFileId());
            SendPhoto photo = SendPhoto.builder()
                    .replyToMessageId(replyTo)
                    .photo(file)
                    .caption(updateMessage.getCaption())
                    .chatId(chatID)
                    .build();

            //im.SendAnswer(im.YummyReChat, "", String.valueOf(update.getMessage().getPhoto().stream()));
            try {
                im.execute(photo);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else if ( updateMessage.hasVideo() ){
            InputFile file = new InputFile();
            file.setMedia(updateMessage.getVideo().getFileId());
            SendVideo video = SendVideo.builder()
                    .replyToMessageId(replyTo)
                    .video(file)
                    .caption(updateMessage.getCaption())
                    .chatId(chatID)
                    .build();
            try {
                im.execute(video);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else if ( updateMessage.hasDocument() ){
            InputFile file = new InputFile();
            file.setMedia(updateMessage.getDocument().getFileId());
            SendDocument document = SendDocument.builder()
                    .replyToMessageId(replyTo)
                    .document(file)
                    .caption(updateMessage.getCaption())
                    .chatId(chatID)
                    .build();
            try {
                im.execute(document);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

}
