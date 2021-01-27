package io.github.charles.bot.builder;

import com.amazonaws.services.dynamodbv2.document.Item;
import io.github.charles.bot.model.Recommendation;
import io.github.charles.util.MessageType;
import io.github.wechaty.user.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RecommendationConverter {

    private static Logger logger = LoggerFactory.getLogger(RecommendationConverter.class);

    public static Recommendation from(Item item) {
        if (item == null) {
            return null;
        }

        Recommendation.RecommendationBuilder builder = Recommendation.builder();

        MessageType messageType = MessageType.Text;
        if (item.getString("AnswerType") != null) {
            messageType = MessageType.valueOf(item.getString("AnswerType"));
        }

        builder
                .answer(item.getString("Answer"))
                .creationTimestamp(item.getLong("CreationTimestamp"))
                .question(item.getString("Question"))
                .rank(item.getInt("AnswerRank"))
                .type(messageType)
                .createdBy(item.getString("createdBy"));

        return builder.build();
    }

    public static Recommendation fromAddString(String textInput, Contact createdBy) {
        logger.info(String.format("converting string %s to recommendation for creation  by %s", textInput, createdBy));
        Recommendation recommendation = null;
        String[] inputs = textInput.split(" ");

        if (inputs != null && inputs.length >= 2) {
            recommendation = Recommendation.builder()
                    .question(inputs[0])
                    .answer(textInput.substring(inputs[0].length()).trim())
                    .createdBy(createdBy.getId())
                    .build();

        }
        return recommendation;
    }


    public static Recommendation getCardTypeRecommendation(String textInput, Contact createdBy) {
        logger.info(String.format("converting string %s to recommendation for creation  by %s", textInput, createdBy));
        Recommendation recommendation = null;
        String[] inputs = textInput.split(" ");

        if (inputs != null) {
            recommendation = Recommendation.builder()
                    .question(inputs[0])
                    .answer(createdBy.getId())
                    .createdBy(createdBy.getId())
                    .type(MessageType.Contact)
                    .build();

        }
        return recommendation;
    }
}
