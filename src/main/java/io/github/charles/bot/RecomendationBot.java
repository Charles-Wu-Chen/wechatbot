package io.github.charles.bot;


import io.github.charles.bot.builder.RecommendationConverter;
import io.github.charles.bot.dao.RecommendationDao;
import io.github.charles.bot.model.Recommendation;
import io.github.wechaty.Wechaty;
import io.github.wechaty.user.Contact;
import io.github.wechaty.user.Message;
import io.github.wechaty.user.Room;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static io.github.charles.util.CommonUtil.getDateTimeByTimestamp;
import static io.github.charles.util.CommonUtil.getDatetimeFromString;

public class RecomendationBot implements Bot {

    private static Logger logger = LoggerFactory.getLogger(RecomendationBot.class);
    private static final List<String> LIST_TRIGGERWORD = Arrays.asList("推荐", "添加推荐", "删除推荐");
    private static final String SELF_MENTIONED = "@机器人";
    private static final String WUCHEN_WECHAT_ID = "wxid_1194601945911";

    private RecommendationDao recommendationDao;

    public RecomendationBot() {
        recommendationDao = new RecommendationDao();

    }

    @Override
    public void handleTextMessage(Message message, Wechaty wechaty) {
        if (!checkTriggerFormat(message)) {
            return;
        } else {
            Contact from = message.from();
            Room room = message.room();
            String text = mentionText(message);
            if (text.startsWith("推荐")) {
                doRecommendation(from, room, text);
            } else if (text.startsWith("添加推荐")) {
                createRecommendation(from, room, text);
            } else if (text.startsWith("删除推荐")) {
                deleteRecommendation(from, room, text);
            }

        }
    }

    //temporarily replace wechaty-java mentionText function as it is not implemented in the library
    private String mentionText(Message message) {
        String text = message.text();
        return StringUtils.strip(text.replaceAll("@[\\p{L}\\p{Digit}_]+", " ").trim());
    }

    private void deleteRecommendation(Contact from, Room room, String text) {
        String textInput = StringUtils.strip(text.replace("删除推荐", "").trim());
        try {
            String[] inputs = textInput.split(" ");
            if (inputs != null && inputs.length >= 2) {
                long actualTimestamp = getDatetimeFromString(inputs[1]).getTime();
                Recommendation recommendation = recommendationDao.queryByPrimaryKey(inputs[0], actualTimestamp);
                if (isAllowToDelete(recommendation, from)) {
                    boolean result = recommendationDao.delete(recommendation);
                    if (result) {
                        wechatReply(from, room, "删除成功");
                    } else {
                        wechatReply(from, room, "删除失败");
                    }
                } else {
                    wechatReply(from, room, "Failed to delete. You cannot delete other's answer");
                }

            } else {
                wechatReply(from, room, "Failed to delete, correct format is @机器人 删除推荐 <问题> <答案>");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            wechatReply(from, room, "Failed to delete, correct format is @机器人 删除推荐 <问题> <答案>");
        }
    }

    private boolean isAllowToDelete(Recommendation recommendation, Contact from) {
        return from.getId().equals(recommendation.getCreatedBy()) || from.getId().equals(WUCHEN_WECHAT_ID);
    }

    private void createRecommendation(Contact from, Room room, String text) {
        String textInput = text.replace("添加推荐", "").trim();
        Recommendation recommendation = RecommendationConverter.fromAddString(textInput, from.getId());
        if (recommendation == null) {
            wechatReply(from, room, String.format("Failed to add %s, correct format is @机器人 添加推荐 <问题> <答案>",
                    textInput));
        } else {
            recommendationDao.create(recommendation);
            wechatReply(from, room, String.format("%s 添加成功 at %s",
                    recommendation.getQuestion(),
                    getDateTimeByTimestamp(recommendation.getCreationTimestamp())));
        }
    }

    private void wechatReply(Contact from, Room room, String textReply) {
        if (room != null) {
            room.say(textReply);
        } else {
            from.say(textReply);
        }
    }

    private void doRecommendation(Contact from, Room room, String text) {
        String questionText = text.replace("推荐", "").trim();
        logger.info("questionText is :" + questionText);

        List<Recommendation> recommendations = recommendationDao.queryByKeyword(questionText);

        if (recommendations.size() == 0) {
            wechatReply(from, room, String.format("暂时没有关于%s的推荐， 请添加。 格式：%s",
                    questionText, "@机器人 添加推荐 " + questionText + " <推荐内容>"));
        }

        recommendations.forEach(recommendation -> {
            wechatReply(from, room, String.format("%s by %s at %s",
                    recommendation.getAnswer(), recommendation.getCreatedBy(),
                    getDateTimeByTimestamp(recommendation.getCreationTimestamp())));
        });
    }

    private boolean checkTriggerFormat(Message message) {
        //Comment out as java-wechaty is not support mentionList yet
        //        message.mentionList().forEach(c -> System.out.println("mentioned contact:" + c));
        //        boolean isSelfMentioned = message.mentionList().stream().filter(contact -> contact.self()).count() > 0;
        //        boolean isStartWithTriggerWord =
        //            LIST_TRIGGERWORD.stream().anyMatch(s -> message.mentionText().startsWith(s));

        boolean isSelfMentioned = message.text().startsWith(SELF_MENTIONED);
        if (!isSelfMentioned) {
            return false;
        }


        String mentionText = StringUtils.strip(message.text().substring(SELF_MENTIONED.length()).trim());
        boolean isStartWithTriggerWord =
                    LIST_TRIGGERWORD.stream().anyMatch(s -> mentionText.startsWith(s));
        return isStartWithTriggerWord;
    }

    @Override
    public void handleImageMessage(Message message, Wechaty wechaty) {
        return;
    }

    public static void main(String[] args) {
        RecommendationDao recommendationDao = new RecommendationDao();

        List<Recommendation> recommendations = recommendationDao.queryAll();

        recommendations.forEach(recommendation -> {
            System.out.println(recommendation.getAnswer());
        });

        Recommendation newRecommendation = Recommendation.builder()
                .answer("google.com")
                .question("test")
                .createdBy("wechatid")
                .build();

        recommendationDao.create(newRecommendation);

        recommendations = recommendationDao.queryAll();

        recommendations.forEach(recommendation -> {
            System.out.println(recommendation.getAnswer());
        });

        //recommendationDao.delete(recommendations.get(0));

        recommendations = recommendationDao.queryAll();

        recommendations.forEach(recommendation -> {
            System.out.println(recommendation.getAnswer());
        });

        Recommendation recommendation = recommendationDao.queryByPrimaryKey("test", 1603446179082L);
        recommendationDao.delete(recommendation);

    }


    @Override
    public void handleContactMessage(Message message, Wechaty wechaty) {

    }
}
