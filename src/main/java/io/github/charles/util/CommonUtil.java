package io.github.charles.util;


import io.github.wechaty.Wechaty;
import io.github.wechaty.schemas.RoomQueryFilter;
import io.github.wechaty.user.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CommonUtil {

    private static Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    //"2020-10-07T21:42:35Z"
    //"yyyy-MM-dd'T'HH:mm:ss'Z'"	2001-07-04T12:08:56Z
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);

    public static int generateRandomBetween(int min, int max) {
        //Generate random int value from 50 to 100
        logger.debug("Random value in int from " + min + " to " + max + ":");
        int randomInt = (int) (Math.random() * (max - min + 1) + min);
        logger.info("wait ms:" + randomInt);
        return randomInt;
    }

    public static Date getDatetimeFromString(String dateString) throws ParseException {
        return simpleDateFormat.parse(dateString);
    }

    public static String getCurrentDateTimeString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        return ZonedDateTime.now().format(formatter);
    }

    public static String getDateTimeByTimestamp(long timestamp) {
        return simpleDateFormat.format(new Date(timestamp));
    }

    public static String getDateTimeStringByDate(Date date) {

        return simpleDateFormat.format(date);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
