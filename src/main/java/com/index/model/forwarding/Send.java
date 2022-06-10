package com.index.model.forwarding;

import com.index.IndexMain;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Comparator;
import java.util.StringTokenizer;

public class Send {

    IndexMain im = new IndexMain();
    private long ReplyTo = 0;

    public Send(Update update, String Name)
    {
        if (String.valueOf(update.getMessage().getChatId()).equals(im.YummyReChat) && update.getMessage().getReplyToMessage() != null )
        {
            final StringTokenizer st = new StringTokenizer(update.getMessage().getReplyToMessage().getText());
            st.nextToken();
            st.nextToken();
            ReplyTo = Long.parseLong(st.nextToken());
        }
        if (       update.getMessage().hasContact()
                || update.getMessage().hasDice()
                || update.getMessage().hasLocation()
                || update.getMessage().hasPassportData()
                || update.getMessage().hasPoll()
                || update.getMessage().hasReplyMarkup()
                || update.getMessage().hasSuccessfulPayment()
                || update.getMessage().hasInvoice()
                || update.getMessage().hasVideoNote()
        )
        {
            im.SendAnswer(im.YummyReChat, Name, "Извиняюсь, не умею пересылать такое;");
            return;
        }
        //im.SendAnswer(im.YummyChannel_CHAT, "INDEX_BOT", Name+":");
        if ( update.getMessage().hasText() || update.getMessage().hasViaBot() )
        {
            im.SendAnswer(im.YummyChannel_CHAT, Name, update.getMessage().getText(), "NONE", (int) ReplyTo);
        }
        else if ( update.getMessage().hasVoice() )
        {
            InputFile file = new InputFile();
            file.setMedia(update.getMessage().getVoice().getFileId());
            SendVoice voice = SendVoice.builder()
                    .replyToMessageId((int) ReplyTo)
                    .voice(file)
                    .chatId(String.valueOf(im.YummyChannel_CHAT))
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
        else if ( update.getMessage().hasAudio() )
        {
            InputFile file = new InputFile();
            file.setMedia(update.getMessage().getAudio().getFileId());
            SendAudio audio = SendAudio.builder()
                    .replyToMessageId((int) ReplyTo)
                    .audio(file)
                    .chatId(String.valueOf(im.YummyChannel_CHAT))
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
        else if ( update.getMessage().hasSticker() ) {
            InputFile file = new InputFile();
            file.setMedia(update.getMessage().getSticker().getFileId());
            SendSticker sticker = SendSticker.builder()
                    .replyToMessageId((int) ReplyTo)
                    .sticker(file)
                    .chatId(String.valueOf(im.YummyChannel_CHAT))
                    .build();
            try {
                im.execute(sticker);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else if ( update.getMessage().hasAnimation() ) {
            InputFile file = new InputFile();
            file.setMedia(update.getMessage().getAnimation().getFileId());
            SendAnimation animation = SendAnimation.builder()
                    .replyToMessageId((int) ReplyTo)
                    .animation(file)
                    .chatId(String.valueOf(im.YummyChannel_CHAT))
                    .build();
            try {
                im.execute(animation);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else if ( update.getMessage().hasPhoto() ) {
            org.telegram.telegrambots.meta.api.objects.InputFile file = new org.telegram.telegrambots.meta.api.objects.InputFile();
            //java.io.File photo_f = new File("photo_2022-02-06_17-23-09.jpg");
            file.setMedia(update.getMessage().getPhoto().stream().max(Comparator.comparing(PhotoSize::getFileId)).orElse(null).getFileId());
            //file.setMedia(photo_f);
            //String file_id = "AgACAgIAAx0CUTEAAUYAAjZYYhMZRLHAHhL55WCA2ue4sgqndb0AAqu5MRvmpJhIcW22U_il1AgBAAMCAANzAAMjBA";
            //file.setMedia(file_id);
            //im.SendAnswer(im.YummyReChat, "", update.getMessage().getPhoto().stream().max(Comparator.comparing(PhotoSize::getFileId)).orElse(null).getFileId());
            SendPhoto photo = SendPhoto.builder()
                    .replyToMessageId((int) ReplyTo)
                    .photo(file)
                    .caption(update.getMessage().getCaption())
                    .chatId(String.valueOf(im.YummyChannel_CHAT))
                    .build();

            //im.SendAnswer(im.YummyReChat, "", String.valueOf(update.getMessage().getPhoto().stream()));
            try {
                im.execute(photo);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else if ( update.getMessage().hasVideo() ){
            InputFile file = new InputFile();
            file.setMedia(update.getMessage().getVideo().getFileId());
            SendVideo video = SendVideo.builder()
                    .replyToMessageId((int) ReplyTo)
                    .video(file)
                    .caption(update.getMessage().getCaption())
                    .chatId(String.valueOf(im.YummyChannel_CHAT))
                    .build();
            try {
                im.execute(video);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else if ( update.getMessage().hasDocument() ){
            InputFile file = new InputFile();
            file.setMedia(update.getMessage().getDocument().getFileId());
            SendDocument document = SendDocument.builder()
                    .replyToMessageId((int) ReplyTo)
                    .document(file)
                    .caption(update.getMessage().getCaption())
                    .chatId(String.valueOf(im.YummyChannel_CHAT))
                    .build();
            try {
                im.execute(document);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

}
