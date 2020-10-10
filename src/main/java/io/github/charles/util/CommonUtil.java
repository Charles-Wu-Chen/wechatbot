package io.github.charles.util;


import io.github.wechaty.Wechaty;
import io.github.wechaty.schemas.RoomQueryFilter;
import io.github.wechaty.user.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

public class CommonUtil {

    private static Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    public static int generateRandomBetween(int min, int max) {
        //Generate random int value from 50 to 100
        logger.debug("Random value in int from " + min + " to " + max + ":");
        int randomInt = (int) (Math.random() * (max - min + 1) + min);
        logger.info("wait ms:" + randomInt);
        return randomInt;
    }


    public static String getDateTimeWithZone() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss Z");
        return ZonedDateTime.now().format(formatter);
    }

    public static Room getRoomByTopic(Wechaty wechaty, String topic) {

        RoomQueryFilter roomQueryFilter = new RoomQueryFilter();
        roomQueryFilter.setTopic(topic);

        return wechaty.getRoomManager().find(roomQueryFilter);
    }

    public static String getTopicByRoom(Room room) {
        try {
            return room.getTopic().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }
}
