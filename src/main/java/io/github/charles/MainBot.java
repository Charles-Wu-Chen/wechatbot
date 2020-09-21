package io.github.charles;


import io.github.charles.model.MessageRoute;
import io.github.wechaty.Wechaty;
import io.github.wechaty.io.github.wechaty.schemas.EventEnum;
import io.github.wechaty.schemas.ContactQueryFilter;
import io.github.wechaty.schemas.RoomQueryFilter;
import io.github.wechaty.user.Contact;
import io.github.wechaty.user.ContactSelf;
import io.github.wechaty.user.Message;
import io.github.wechaty.user.Room;
import io.github.wechaty.utils.QrcodeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainBot {
    private static Logger logger = LoggerFactory.getLogger(MainBot.class);
    private static final String TEST_ROOM_TOPIC = "测试区危险";
    private static final String PUPPET_HOSTIE_TOKEN = System.getenv("WECHATY_PUPPET_HOSTIE_TOKEN");

    public static String loginUserName = StringUtils.EMPTY;

    public static void main(String[] args) throws Exception {

        if (StringUtils.isBlank(PUPPET_HOSTIE_TOKEN)) {

            logger.info("Error: WECHATY_PUPPET_HOSTIE_TOKEN is not found in the environment variables");
            logger.info("You need a TOKEN to run the Java Wechaty. Please goto our README for details");
            logger.info("https://github.com/wechaty/java-wechaty-getting-started/#wechaty_puppet_hostie_token");

            throw new Exception("need a token");
        }
        Wechaty bot = Wechaty.instance(PUPPET_HOSTIE_TOKEN);

        bot.onScan((qrcode, statusScanStatus, data) -> {
            logger.info(QrcodeUtils.getQr(qrcode));
            logger.info("Online Image: https://wechaty.github.io/qrcode/" + qrcode);
        });

        //bot.onMessage(message -> handleMessage(bot, message));

        bot.onLogin(contactSelf -> {
            logger.info(String.format("%s logged in at %s", contactSelf.name(), getDateTimeWithZone()));
            handleLogin(bot, contactSelf);
        });

        bot.on(EventEnum.LOGOUT, logOut -> {
            Arrays.stream(logOut)
                    .forEach(l -> logger.info(String.format("[%s] %s logged out at %s",
                            l.getClass(), l, getDateTimeWithZone())));
            bot.stop();
            try {
                Thread.sleep(60000);
                logger.info("after thread sleep at " + getDateTimeWithZone());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bot.start(true);
        });

        bot.start(true);
    }

    private static void handleLogin(Wechaty bot, ContactSelf contactSelf) {
        loginUserName = contactSelf.name();

        ContactQueryFilter contactQueryFilter = new ContactQueryFilter();
        //contactQueryFilter.setId("wxid_1194601945911"); //wuchen
        List<Contact> contacts = bot.getContactManager().findAll(contactQueryFilter);
        Contact mainContact = contacts.stream().filter(contact -> contact.getId().equals("wxid_1194601945911"))
                .findFirst().get();

        logger.info("is ready : " + mainContact.isReady());
        logger.info(String.format("contact ID:%s, contact name:%s", mainContact.getId(), mainContact.name()));

        if (mainContact.getId().equals("wxid_1194601945911")) {
            if (!mainContact.isReady()) {
                mainContact.sync();
            }
            mainContact.say(String.format("您的专属机器人 %s logged in at %s", contactSelf.name(), getDateTimeWithZone()));
        }

        if (mainContact != null) {
            while (true) {
                try {
                    Thread.sleep(generateRandomBetween(60000, 300000));
                    mainContact.say(String.format("at %s : wake up ", getDateTimeWithZone()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        RoomQueryFilter roomQueryFilter = new RoomQueryFilter();
        List<Room> rooms = bot.getRoomManager().findAll(roomQueryFilter);
        rooms.stream().forEach(room -> logger.info("room topic:" + getRoomTopic(room)));


    }

    private static void handleMessage(Wechaty bot, Message message) {
        Contact from = message.from();
        Room room = message.room();


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

        getMessageRoutes().stream()
                .filter(messageRoute -> messageRoute.getSourceName().equals(getRoomTopic(room)))
                .forEach(messageRoute -> {
                    Room destRoom = getRoomByTopic(bot, messageRoute.getDestinationName());
                    destRoom.say(String.format("[%s in %s]:%s", from.name(), getRoomTopic(room), text));
                });
    }

    private static String getRoomTopic(Room room) {
        try {
            return room.getTopic().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static Room getRoomByTopic(Wechaty bot, String topic) {

        RoomQueryFilter roomQueryFilter = new RoomQueryFilter();
        roomQueryFilter.setTopic(topic);

        return bot.getRoomManager().find(roomQueryFilter);
    }

    private static String getDateTimeWithZone() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss Z");
        return ZonedDateTime.now().format(formatter);
    }

    private static List<MessageRoute> getMessageRoutes() {
        MessageRoute route1 = new MessageRoute("测试区危险", "测试区不危险");
        MessageRoute route2 = new MessageRoute("测试区不危险", "测试区危险");
        MessageRoute route3 = new MessageRoute("3133 好邻居 （三）", "测试区危险");
        List<MessageRoute> routes = new ArrayList<>();
        routes.add(route1);
        routes.add(route2);
        routes.add(route3);
        return routes;
    }

    private static int generateRandomBetween(int min, int max) {
        //Generate random int value from 50 to 100
        logger.debug("Random value in int from " + min + " to " + max + ":");
        int randomInt = (int) (Math.random() * (max - min + 1) + min);
        logger.info("wait ms:" + randomInt);
        return randomInt;
    }
}
