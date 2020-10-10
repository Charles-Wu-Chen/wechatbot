package io.github.charles.bot;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import io.github.wechaty.Wechaty;
import io.github.wechaty.user.Contact;
import io.github.wechaty.user.Message;
import io.github.wechaty.user.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

public class RecomendationBot implements Bot {

    private static Logger logger = LoggerFactory.getLogger(RecomendationBot.class);
    private Table table;
    private static final String TRIGGERWORD = "推荐";

    public RecomendationBot() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        DynamoDB dynamoDB = new DynamoDB(client);
        table = dynamoDB.getTable("Recommendation");
    }

    @Override
    public void handleTextMessage(Message message, Wechaty wechaty) {
        if (!checkTriggerFormat(message)) {
            return;
        } else {
            Contact from = message.from();
            Room room = message.room();
            String text = message.mentionText();
            String questionText = text.replace(TRIGGERWORD, "").trim();
            logger.info("mention self text is :" + text);

            QuerySpec spec = new QuerySpec()
                    .withKeyConditionExpression("Keyword = :v_keyword")
                    .withValueMap(new ValueMap()
                            .withString(":v_keyword", questionText));

            ItemCollection<QueryOutcome> items = table.query(spec);

            Iterator<Item> iterator = items.iterator();
            Item item = null;
            while (iterator.hasNext()) {
                item = iterator.next();
                System.out.println(item.toJSONPretty());

                item.attributes().forEach(e -> {
                    if (e.getKey().equals("Answer"))
                    if (room != null) {
                        room.say(e.getValue());
                    } else {
                        from.say(e.getValue());
                    }
                });

            }
        }
    }

    private boolean checkTriggerFormat(Message message) {
        message.mentionList().forEach(c -> System.out.println("mentioned contact:" + c));
        boolean isSelfMentioned = message.mentionList().stream().filter(contact -> contact.self()).count() > 0;
        boolean isStartWithTriggerWord = message.mentionText().startsWith(TRIGGERWORD);
        return isSelfMentioned && isStartWithTriggerWord;
    }

    @Override
    public void handleImageMessage(Message message, Wechaty wechaty) {
        return;
    }

    public static void main(String[] args) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        try {
            ScanRequest scanRequest = new ScanRequest()
                    .withTableName("Recommendation");

            ScanResult result = client.scan(scanRequest);
            for (Map<String, AttributeValue> item : result.getItems()) {
                System.out.println(item);
            }

            DynamoDB dynamoDB = new DynamoDB(client);

            Table table = dynamoDB.getTable("Recommendation");

            QuerySpec spec = new QuerySpec()
                    .withKeyConditionExpression("Keyword = :v_keyword")
                    .withValueMap(new ValueMap()
                            .withString(":v_keyword", "物流"));

            ItemCollection<QueryOutcome> items = table.query(spec);

            Iterator<Item> iterator = items.iterator();
            Item item = null;
            while (iterator.hasNext()) {
                item = iterator.next();
                System.out.println(item.toJSONPretty());

                item.attributes().forEach(e -> {
                    if (e.getKey().equals("Answer"))
                        System.out.println(e.getValue());
                });
            }
        } catch (Exception e) {
            System.err.println("Unable to scan the table:");
            System.err.println(e.getMessage());
        }

    }
}
