package io.github.charles.bot;


import io.github.wechaty.Wechaty;
import io.github.wechaty.user.Message;

public interface Bot {
    String usage();

    //todo refactor to one method to follow ISP THE INTERFACE SEGREGATION PRINCIPLE
    //i.e a fat interface,  unnecessary methods in the interface, and client have to inherit even it doesn’t need.
    //todo 2nd remove the dependency of wechaty if poosible.
    void handleTextMessage(Message message, Wechaty wechaty);

    void handleImageMessage(Message message, Wechaty wechaty);

    void handleContactMessage(Message message, Wechaty wechaty);
}
