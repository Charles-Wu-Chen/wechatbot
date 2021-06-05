package io.github.charles.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CacheHelperTest {

    CacheHelper cacheHelper = new CacheHelper();
    @Test
    void shallReportDuplicateIfCheckMessageSecondTime() {
        cacheHelper.addMessageToCache("AMESSAGEADDED");
        assertTrue(cacheHelper.isDuplicateMessage("AMESSAGEADDED"));
    }

    @Test
    void shallBeFalseForANewMessage() {
        assertFalse(cacheHelper.isDuplicateMessage("ANEWMESSAGE"));
    }
}
