package io.github.charles.bot;


import io.github.wechaty.Wechaty;
import io.github.wechaty.user.Message;

public interface Bot {
    void handleTextMessage(Message message, Wechaty wechaty);

    void handleImageMessage(Message message, Wechaty wechaty);

    void handleContactMessage(Message message, Wechaty wechaty);
}
