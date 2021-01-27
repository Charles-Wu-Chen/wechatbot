package io.github.charles.bot;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import io.github.charles.model.SingleWechaty;
import io.github.wechaty.Wechaty;
import io.github.wechaty.user.Message;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.jupiter.api.Assertions.*;

public class RecommendationBotTest {

    RecommendationBot testInstance;

    @Mock
    Table dynamoTable;

    @BeforeEach
    void setUpEach() {
        MockitoAnnotations.initMocks(this);
        testInstance = new RecommendationBot();
    }


    @Test
    public void testMentionText() {
        String line ="@abc @def this is text";

        assertEquals("this is text", line.replaceAll("@\\w+", " ").trim() );


        String cline ="@机器人 推荐 物流";
        //to handle unicode replacement
        assertEquals("推荐 物流", cline.replaceAll("@[\\p{L}\\p{Digit}_]+", " ").trim() );

    }

    @Test
    public void testAddKeywordWithTextAnswer() {
        //use case: in the group chat (aka room), say @机器人 添加keyword 问题 答案
        //expected: entry in dynamodb. mock method to be called once.
        testInstance.getRecommendationDao().setTable(dynamoTable);

        // assert dynamoTable putItem() called once
    }

}
