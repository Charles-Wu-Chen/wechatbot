package io.github.charles.bot;


import io.github.wechaty.Wechaty;
import io.github.wechaty.user.Message;

public interface Bot {
    String usage();

    //todo 2nd remove the dependency of wechaty if possible.
    void handleMessage(Message message, Wechaty wechaty);
}
