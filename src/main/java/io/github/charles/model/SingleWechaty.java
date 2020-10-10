package io.github.charles.model;

import io.github.charles.bot.RecomendationBot;
import io.github.charles.bot.RoomMessageSyncBot;
import io.github.wechaty.Wechaty;
import io.github.wechaty.filebox.FileBox;
import io.github.wechaty.io.github.wechaty.schemas.EventEnum;
import io.github.wechaty.schemas.ContactQueryFilter;
import io.github.wechaty.schemas.RoomQueryFilter;
import io.github.wechaty.user.*;
import io.github.wechaty.utils.QrcodeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static io.github.charles.util.CommonUtil.getDateTimeWithZone;
import static io.github.charles.util.CommonUtil.getTopicByRoom;

public final class SingleWechaty {
    private static Logger logger = LoggerFactory.getLogger(SingleWechaty.class);
    private static final String PUPPET_HOSTIE_TOKEN = System.getenv("WECHATY_PUPPET_HOSTIE_TOKEN");
    private static volatile SingleWechaty instance;
    private static Wechaty wechaty;
    public static String loginUserName = StringUtils.EMPTY;

    public String value;

    private SingleWechaty() throws Exception {

        if (StringUtils.isBlank(PUPPET_HOSTIE_TOKEN)) {

            logger.info("Error: WECHATY_PUPPET_HOSTIE_TOKEN is not found in the environment variables");
            logger.info("You need a TOKEN to run the Java Wechaty. Please goto our README for details");
            logger.info("https://github.com/wechaty/java-wechaty-getting-started/#wechaty_puppet_hostie_token");

            throw new Exception("need a token");
        }
        wechaty = Wechaty.instance(PUPPET_HOSTIE_TOKEN);

        wechaty.onScan((qrcode, statusScanStatus, data) -> {
            logger.info(QrcodeUtils.getQr(qrcode));
            logger.info("Online Image: https://wechaty.github.io/qrcode/" + qrcode);
        });

        wechaty.onMessage(message -> handleMessage(wechaty, message));

        wechaty.onLogin(contactSelf -> {
            logger.info(String.format("%s logged in at %s", contactSelf.name(), getDateTimeWithZone()));
            handleLogin(wechaty, contactSelf);
        });

        wechaty.on(EventEnum.LOGOUT, logOut -> {
            Arrays.stream(logOut)
                    .forEach(l -> logger.info(String.format("[%s] %s logged out at %s",
                            l.getClass(), l, getDateTimeWithZone())));
            wechaty.stop();
            try {
                Thread.sleep(60000);
                logger.info("after thread sleep at " + getDateTimeWithZone());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            wechaty.start(false);
        });

        wechaty = wechaty.start(false);
    }


    public void sendMessage(String message, String contactId) {
        ContactQueryFilter singleContactQueryFilter = new ContactQueryFilter();
        singleContactQueryFilter.setId(contactId); //wuchen
        Contact singleContact = wechaty.getContactManager().find(singleContactQueryFilter);

        if (singleContact == null) {
            List<Contact> contacts = wechaty.getContactManager().findAll(new ContactQueryFilter());
            singleContact = contacts.stream().filter(contact -> contact.getId().equals(contactId))
                    .findFirst().orElse(null);
        }

        if (singleContact != null) {
            singleContact.say(String.format("%s at %s", message, getDateTimeWithZone()));
        } else {
            logger.error(String.format("Contact not found %s", contactId));
        }
    }

    public static SingleWechaty getInstance() throws Exception {
        // The approach taken here is called double-checked locking (DCL). It
        // exists to prevent race condition between multiple threads that may
        // attempt to get singleton instance at the same time, creating separate
        // instances as a result.
        //
        // It may seem that having the `result` variable here is completely
        // pointless. There is, however, a very important caveat when
        // implementing double-checked locking in Java, which is solved by
        // introducing this local variable.
        //
        // You can read more info DCL issues in Java here:
        // https://refactoring.guru/java-dcl-issue
        SingleWechaty result = instance;
        if (result != null) {
            return result;
        }
        synchronized (SingleWechaty.class) {
            if (instance == null) {
                instance = new SingleWechaty();
            }
            return instance;
        }
    }


    private static void handleLogin(Wechaty bot, ContactSelf contactSelf) {
        loginUserName = contactSelf.name();

        ContactQueryFilter contactQueryFilter = new ContactQueryFilter();
        //contactQueryFilter.setId("wxid_1194601945911"); //wuchen
        List<Contact> contacts = bot.getContactManager().findAll(contactQueryFilter);
        contacts.stream()
                .forEach(contact -> {
            logger.info("is ready : " + contact.isReady());
            logger.info(String.format("contact ID:%s, contact name:%s", contact.getId(), contact.name()));
            if (!contact.isReady()) {
                contact.sync();
            }
            if (contact.getId().equals("wxid_1194601945911")) {
                contact.say(String.format("您的专属机器人 %s logged in at %s", contactSelf.name(), getDateTimeWithZone()));
            }
        });

        RoomQueryFilter roomQueryFilter = new RoomQueryFilter();
        List<Room> rooms = bot.getRoomManager().findAll(roomQueryFilter);
        rooms.stream().forEach(room -> logger.info("room topic:" + getTopicByRoom(room)));


    }

    private static void handleMessage(Wechaty bot, Message message) {
        Contact from = message.from();
        Room room = message.room();

        RoomMessageSyncBot roomMessageSyncBot = new RoomMessageSyncBot();
        RecomendationBot recomendationBot = new RecomendationBot();
        switch (message.type()) {
            case Text:


                String text = message.text();

                if (message.self()) { // skip message from self, also to avoid infinite loop
                    logger.info("message from self");
                    return;
                }

                if (StringUtils.equals(text, "ding")) {
                    if (room != null) {
                        room.say("dong");
                    } else {
                        from.say("dong");
                    }
                }

                roomMessageSyncBot.handleTextMessage(message, wechaty);
                recomendationBot.handleTextMessage(message, wechaty);
                return;
            case Image:
                roomMessageSyncBot.handleImageMessage(message, wechaty);
                return;
            default:
                logger.info("unhandled message with type:" + message.type());
        }
    }

    private static void handleImage(Message message) {
        Contact from = message.from();
        Room room = message.room();
        if (room != null) {
            logger.info("room image string:" + message.toString());
            //room.say(message.toImage().artwork());
        } else {
            logger.info("message string:" + message.toString());
                        Image image = message.toImage();
                        logger.info("Image String" + image.toString());
                        FileBox fileBox = image.artwork();
                        logger.info("FileBox name:" + fileBox.getName());
                        logger.info("FileBox url:" + fileBox.getRemoteUrl());
                        logger.info("FileBox json:" + fileBox.toJsonString());
            from.say(fileBox);
        }
    }







}