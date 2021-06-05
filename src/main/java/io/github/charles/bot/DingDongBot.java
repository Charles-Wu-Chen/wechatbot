package io.github.charles.bot;

import io.github.charles.adaptor.out.chatapi.TianChatApiImpl;
import io.github.charles.application.out.ChatApiPort;
import io.github.wechaty.Wechaty;
import io.github.wechaty.user.Contact;
import io.github.wechaty.user.Message;
import io.github.wechaty.user.Room;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class DingDongBot implements Bot {

    private static Logger logger = LoggerFactory.getLogger(DingDongBot.class);

    private static final String TRIGGER_WORD = "ding";

    private static ChatApiPort chatApi = new TianChatApiImpl();

    @Override
    public String usage() {
        return "触发词：" + TRIGGER_WORD;
    }

    public void handleMessage(Message message, Wechaty wechaty) {
        handleTextMessage(message, wechaty);
    }

    public void handleTextMessage(Message message, Wechaty wechaty) {
        Contact from = message.from();
        Room room = message.room();
        String text = message.text();

        if (StringUtils.startsWith(text, TRIGGER_WORD)) {
            if (room != null) {
                try {
                    logger.info("message from room, " + room.getTopic().get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Contact c = new Contact(wechaty, "wxid_1194601945911");
                room.say(c);
            } else {
                String input = StringUtils.substring(text, TRIGGER_WORD.length()).strip();
                String output = chatApi.getResponse(input);
                from.say(output);
            }
        }
    }

    public void handleImageMessage(Message message, Wechaty wechaty) {

    }

    public void handleContactMessage(Message message, Wechaty wechaty) {

    }

}
