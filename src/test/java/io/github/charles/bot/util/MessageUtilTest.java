package io.github.charles.bot.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageUtilTest {

    @Test
    void testNormalInput() {
        String input= "@机器人 请问水工";
        String question = MessageUtil.extractQuestion(input, "请问");
        assertEquals("水工", question);
    }
}