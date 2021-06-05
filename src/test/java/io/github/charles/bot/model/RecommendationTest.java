package io.github.charles.bot.model;

import io.github.charles.util.MessageType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RecommendationTest {

    @Test
    void shallCreateWithDefaultValue() {
        Recommendation recommendation = Recommendation.builder()
                .answer("test")
                .question("Keyword")
                .build();

        assertEquals(1, recommendation.getRank());
        assertEquals(MessageType.Text, recommendation.getType());
    }
}
