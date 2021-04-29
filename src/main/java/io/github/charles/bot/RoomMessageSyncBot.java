package io.github.charles.bot;

import io.github.charles.model.MessageRoute;
import io.github.wechaty.Wechaty;
import io.github.wechaty.filebox.FileBox;
import io.github.wechaty.user.Contact;
import io.github.wechaty.user.Image;
import io.github.wechaty.user.Message;
import io.github.wechaty.user.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static io.github.charles.util.CommonUtil.getRoomByTopic;
import static io.github.charles.util.CommonUtil.getTopicByRoom;

public class RoomMessageSyncBot implements Bot {

    private static Logger logger = LoggerFactory.getLogger(RoomMessageSyncBot.class);

    @Override
    public String usage() {
        return "自动搬运所有群消息， 无需触发";
    }

    @Override
    public void handleTextMessage(Message message, Wechaty wechaty) {
        Contact from = message.from();
        Room room = message.room();
        String text = message.text();
        getMessageRoutes().stream()
                .filter(messageRoute -> messageRoute.getSourceName().equals(getTopicByRoom(room)))
                .forEach(messageRoute -> {
                    Room destRoom = getRoomByTopic(wechaty, messageRoute.getDestinationName());
                    destRoom.say(String.format("%s:%n%s", from.name(), text));
                });
    }

    @Override
    public void handleImageMessage(Message message, Wechaty wechaty) {
        Contact from = message.from();
        Room room = message.room();

        if (room == null) {
            Image image = message.toImage();
            logger.info("Image String" + image.toString());
            FileBox fileBox = image.artwork();
            logger.info("FileBox name:" + fileBox.getName());
            logger.info("FileBox box type:" + fileBox.getBoxType());
            logger.info("FileBox base64:" + fileBox.getBase64());
            //FileBox.fromJson(fileBox);
            Message m = from.say(fileBox);
            logger.info(String.valueOf(m));
        }

        getMessageRoutes().stream()
                .filter(messageRoute -> messageRoute.getSourceName().equals(getTopicByRoom(room)))
                .forEach(messageRoute -> {
                    Room destRoom = getRoomByTopic(wechaty, messageRoute.getDestinationName());
                    //destRoom.say(String.format("[%s in %s]:%n%s", from.name(), getTopicByRoom(room), "图片上传中..."));
                    destRoom.say(message.toImage().artwork());
                });

    }


    @Override
    public void handleContactMessage(Message message, Wechaty wechaty) {
        Contact from = message.from();
        Room room = message.room();
        //Contact c = message.toContact(); // Wechaty Puppet Unsupported API Error
        Contact c = new Contact(wechaty, "wxid_1194601945911");
        //        Future<String> contactId = wechaty.getPuppet().messageContact(message.getId());
        //
        //        while (!contactId.isDone()) {
        //            System.out.println("Calculating...");
        //            try {
        //                Thread.sleep(1000);
        //            } catch (InterruptedException e) {
        //                e.printStackTrace();
        //            }
        //        }
        //
        //        try {
        //            String result = contactId.get();
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        } catch (ExecutionException e) {
        //            e.printStackTrace();
        //        }

        //        if (room != null) {
        //            room.say(c);
        //        }
    }

    //TODO externalize this to properties and initialization
    private static List<MessageRoute> getMessageRoutes() {
        MessageRoute route1 = new MessageRoute("测试区危险", "测试区不危险");
        MessageRoute route2 = new MessageRoute("测试区不危险", "测试区危险");
        MessageRoute route3 = new MessageRoute("3133好邻居群三", "测试区危险");
        MessageRoute route4 = new MessageRoute("3133好邻居群三", "3133好邻居群二");
        MessageRoute route5 = new MessageRoute("3133好邻居群二", "3133好邻居群三");
        MessageRoute route6 = new MessageRoute("3133好邻居群三", "3133好邻居群一");
        MessageRoute route7 = new MessageRoute("3133好邻居群二", "3133好邻居群一");
        MessageRoute route8 = new MessageRoute("3133好邻居群一", "3133好邻居群三");
        MessageRoute route9 = new MessageRoute("3133好邻居群一", "3133好邻居群二");


        MessageRoute route10 = new MessageRoute("墨尔本苏州总会", "墨尔本苏州侨民会二群");
        MessageRoute route11 = new MessageRoute("墨尔本苏州总会", "墨尔本苏州侨民会三");
        MessageRoute route12 = new MessageRoute("墨尔本苏州侨民会二群", "墨尔本苏州侨民会三");
        MessageRoute route13 = new MessageRoute("墨尔本苏州侨民会二群", "墨尔本苏州总会");
        MessageRoute route14 = new MessageRoute("墨尔本苏州侨民会三", "墨尔本苏州总会");
        MessageRoute route15 = new MessageRoute("墨尔本苏州侨民会三", "墨尔本苏州侨民会二群");

        List<MessageRoute> routes = new ArrayList<>();
        routes.add(route1);
        routes.add(route2);
        routes.add(route3);
        routes.add(route4);
        routes.add(route5);
        routes.add(route6);
        routes.add(route7);
        routes.add(route8);
        routes.add(route9);
        routes.add(route10);
        routes.add(route11);
        routes.add(route12);
        routes.add(route13);
        routes.add(route14);
        routes.add(route15);
        return routes;
    }
}
