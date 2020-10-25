package io.github.charles.bot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RecomendationBotTest {
    @Test
    public void testMentionText() {
        String line ="@abc @def this is text";

        assertEquals("this is text", line.replaceAll("@\\w+", " ").trim() );


        String cline ="@机器人 推荐 物流";

        assertEquals("推荐 物流", cline.replaceAll("@[\\p{L}\\p{Digit}_]+", " ").trim() );

    }

}
