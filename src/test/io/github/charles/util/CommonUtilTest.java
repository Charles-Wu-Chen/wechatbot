package io.github.charles.util;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static io.github.charles.util.CommonUtil.getDateTimeByTimestamp;
import static org.junit.jupiter.api.Assertions.*;

public class CommonUtilTest {

    @Test
    void getDateTimeByTimestampAndShallBeAbleConvertBack() throws ParseException {
        long timestamp = 1603514580790L;
        String dateString = getDateTimeByTimestamp(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat(CommonUtil.DATE_TIME_FORMAT);
        long actualTimeStamp = sdf.parse(dateString).getTime();
        assertEquals(timestamp, actualTimeStamp);
    }
}
