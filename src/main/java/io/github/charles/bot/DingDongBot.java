package io.github.charles.bot;

import io.github.charles.model.MessageRoute;
import io.github.wechaty.Wechaty;
import io.github.wechaty.user.Contact;
import io.github.wechaty.user.Message;
import io.github.wechaty.user.Room;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DingDongBot implements Bot {

    private static Logger logger = LoggerFactory.getLogger(DingDongBot.class);

    private final String TRIGGER_WORD = "ding";

    @Override
    public String usage() {
        return "触发词：" + TRIGGER_WORD;
    }

    @Override
    public void handleTextMessage(Message message, Wechaty wechaty) {
        Contact from = message.from();
        Room room = message.room();
        String text = message.text();

        if (StringUtils.equals(text, TRIGGER_WORD)) {
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
                from.say("dong");
            }
        }
    }

    @Override
    public void handleImageMessage(Message message, Wechaty wechaty) {

    }

    @Override
    public void handleContactMessage(Message message, Wechaty wechaty) {

    }

}
