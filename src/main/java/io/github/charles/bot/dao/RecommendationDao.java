package io.github.charles.bot.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import io.github.charles.bot.builder.RecommendationConverter;
import io.github.charles.bot.model.Recommendation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class RecommendationDao {

    private static final String TABLE_NAME = "Recommendation";
    private AmazonDynamoDB client;
    private Table table;
    private static Logger logger = LoggerFactory.getLogger(RecommendationDao.class);

    public RecommendationDao() {
        client = AmazonDynamoDBClientBuilder.defaultClient();
        DynamoDB dynamoDB = new DynamoDB(client);
        table = dynamoDB.getTable(TABLE_NAME);
    }

    public List<Recommendation> queryByKeyword(String question) {

        List<Recommendation> recommendations = new ArrayList<>();

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("Question = :v_question")
                .withValueMap(new ValueMap()
                        .withString(":v_question", question));

        ItemCollection<QueryOutcome> items = table.query(spec);

        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            System.out.println(item.toJSONPretty());
            recommendations.add(RecommendationConverter.from(item));

        }
        return recommendations;
    }

    public Recommendation queryByPrimaryKey(String question, long creationTimestamp) {
        Item item = table.getItem(new PrimaryKey("Question", question,
                "CreationTimestamp", creationTimestamp));

        return RecommendationConverter.from(item);
    }

    public List<Recommendation> queryAll() {

        List<Recommendation> recommendations = new ArrayList<>();


        Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();

        expressionAttributeValues.put(":type", "Text");

        ItemCollection<ScanOutcome> items = table.scan("AnswerType = :type", // FilterExpression
                null, // ProjectionExpression
                null, // ExpressionAttributeNames - not used in this example
                expressionAttributeValues);

        System.out.println("Scan of " + TABLE_NAME + " for items with a type is Text");
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            System.out.println(item.toJSONPretty());
            recommendations.add(RecommendationConverter.from(item));
        }

        return recommendations;
    }


    public void create(Recommendation recommendation) {
        Item item = new Item()
                .withPrimaryKey(
                new PrimaryKey("Question", recommendation.getQuestion(),
                        "CreationTimestamp", recommendation.getCreationTimestamp()))
                .withString("Answer", recommendation.getAnswer())
                .withNumber("AnswerRank", recommendation.getRank())
                .withString("AnswerType", recommendation.getType())
                .withString("createdBy", recommendation.getCreatedBy());

        table.putItem(item);
    }

    public boolean delete(Recommendation recommendation) {
        if (recommendation == null) {
            return false;
        }
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(new PrimaryKey("Question", recommendation.getQuestion(),
                        "CreationTimestamp", recommendation.getCreationTimestamp()));

        try {
            logger.info("Attempting a delete at " + recommendation);
            table.deleteItem(deleteItemSpec);
            System.out.println("DeleteItem succeeded");
            return true;
        } catch (Exception e) {
            logger.error("Unable to delete item: " + recommendation);
            logger.error(e.getMessage());
            return false;
        }
    }

}
