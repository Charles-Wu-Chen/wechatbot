package io.github.charles.bot;


import io.github.charles.adaptor.out.chatapi.TianChatApiImpl;
import io.github.charles.application.out.ChatApiPort;
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
import java.util.stream.Collectors;

import static io.github.charles.bot.util.MessageUtil.extractQuestion;
import static io.github.charles.util.CommonUtil.getDateTimeByTimestamp;
import static io.github.charles.util.CommonUtil.getDatetimeFromString;

public class RecommendationBot implements Bot {

    private static Logger logger = LoggerFactory.getLogger(RecommendationBot.class);
    private static final String ASK_KEYWORD = "请问";
    private static final List<String> LIST_TRIGGERWORD = Arrays.asList(ASK_KEYWORD, "添加keyword", "删除keyword", "KEYWORD");
    private static final String SELF_MENTIONED = "@机器人";
    private static final String WUCHEN_WECHAT_ID = "wxid_1194601945911";

    private static ChatApiPort chatApi = new TianChatApiImpl();

    public RecommendationDao getRecommendationDao() {
        return recommendationDao;
    }

    public void setRecommendationDao(RecommendationDao recommendationDao) {
        this.recommendationDao = recommendationDao;
    }

    private RecommendationDao recommendationDao;

    public RecommendationBot() {
        recommendationDao = new RecommendationDao();
    }


    @Override
    public String usage() {
        return "触发词：" + SELF_MENTIONED + " " + ASK_KEYWORD;
    }

    @Override
    public void handleMessage(Message message, Wechaty wechaty) {
        switch (message.type()) {
            case Text:
                handleTextMessage(message, wechaty);
                return;
            case Image:
                handleImageMessage(message, wechaty);
                return;
            case Contact:
                handleContactMessage(message, wechaty);
            default:
                logger.info("unhandled message with type:" + message.type());
                logger.info("unhandled message:" + message);
        }
    }

    public void handleTextMessage(Message message, Wechaty wechaty) {
        if (!checkTriggerFormat(message)) {
            return;
        } else {
            Contact from = message.from();
            Room room = message.room();
            String question = extractQuestion(message.text(), ASK_KEYWORD);
            doRecommendation(from, room, question, wechaty);

            //if (text.toUpperCase().startsWith("添加keyword".toUpperCase())) {
            //    createRecommendation(from, room, text);
            //} else if (text.toUpperCase().startsWith("删除keyword".toUpperCase())) {
            //    deleteRecommendation(from, room, text);
            //} else if (text.toUpperCase().startsWith("KEYWORD")) {
            //    listAllKeywords(from, room);
            //} else if (text.toUpperCase().startsWith("请问")) {
            //    doRecommendation(from, room, text, wechaty);
            //}

        }
    }

    private void listAllKeywords(Contact from, Room room) {
        List<String> questions = recommendationDao.queryAll().stream()
                .map(r -> r.getQuestion()).distinct()
                .collect(Collectors.toList());

        if (questions.size() > 0) {
            wechatReply(from, room, questions.toString());
        }
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
        String textInput = text.toUpperCase().replace("添加keyword".toUpperCase(), "").trim();
        Recommendation recommendation;
        if (room == null && from != null) {
            recommendation = RecommendationConverter.getCardTypeRecommendation(textInput, from);
        } else {
            recommendation = RecommendationConverter.fromAddString(textInput, from);
        }
        if (recommendation == null) {
            wechatReply(from, room, String.format("Failed to add %s, correct format is @机器人 添加keyword <问题> <答案>",
                    textInput));
        } else {
            recommendationDao.create(recommendation);
            wechatReply(from, room, String.format("%s 添加成功 at %s",
                    recommendation.getQuestion(),
                    getDateTimeByTimestamp(recommendation.getCreationTimestamp())));
        }
    }

    private void wechatReply(Contact from, Room room, Object textReply) {
        if (room != null) {
            room.say(textReply);
        } else {
            from.say(textReply);
        }
    }

    private void doRecommendation(Contact from, Room room, String question, Wechaty wechaty) {
        logger.info("Sending tianapi with question:" + question);
        String output = chatApi.getResponse(question);
        logger.info("received tianapi with answer:" + output);
        wechatReply(from, room, output);
    }
    //
    //private void doRecommendation(Contact from, Room room, String text, Wechaty wechaty) {
    //    String questionText = text.trim();
    //    logger.info("questionText is :" + questionText);
    //
    //    List<Recommendation> recommendations = recommendationDao.queryAll();
    //
    //    recommendations = recommendations.stream()
    //            .filter(r -> text.toUpperCase().contains(r.getQuestion().toUpperCase()))
    //            .collect(Collectors.toList());
    //
    //    if (recommendations.size() == 0) {
    //        wechatReply(from, room, "试试 请问机器人 keyword ");
    //    }
    //
    //    recommendations.forEach(recommendation -> {
    //        if (MessageType.Text.equals(recommendation.getType())) {
    //            wechatReply(from, room, String.format("%s by %s at %s",
    //                    recommendation.getAnswer(), recommendation.getCreatedBy(),
    //                    getDateTimeByTimestamp(recommendation.getCreationTimestamp())));
    //        } else if (MessageType.Contact.equals(recommendation.getType())) {
    //            Contact c = new Contact(wechaty, recommendation.getAnswer());
    //            wechatReply(from, room, c);
    //        }
    //    });
    //}

    private boolean checkTriggerFormat(Message message) {
        //Comment out as java-wechaty is not support mentionList yet
        //        message.mentionList().forEach(c -> System.out.println("mentioned contact:" + c));
        //        boolean isSelfMentioned = message.mentionList().stream().filter(contact -> contact.self()).count() > 0;
        //        boolean isStartWithTriggerWord =
        //            LIST_TRIGGERWORD.stream().anyMatch(s -> message.mentionText().startsWith(s));


        String messageText = message.text();

        boolean isSelfMentioned = message.text().startsWith(SELF_MENTIONED);
        if (isSelfMentioned) {
            messageText = StringUtils.strip(message.text().substring(SELF_MENTIONED.length()).trim());
        }


        String finalMessageText = messageText;
        boolean isStartWithTriggerWord =
                    LIST_TRIGGERWORD.stream().anyMatch(s -> finalMessageText.toUpperCase().startsWith(s.toUpperCase()));
        return isStartWithTriggerWord;
    }


    public void handleImageMessage(Message message, Wechaty wechaty) {
        return;
    }

    @SuppressWarnings("checkstyle:MethodLength")
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

        List<String> questions = recommendations.stream()
                .map(r -> r.getQuestion()).distinct()
                .collect(Collectors.toList());

        System.out.println(questions);



        String testQuery = "adfas test tsafd";
        recommendations = recommendationDao.queryAll();

        recommendations = recommendations.stream()
                .filter(r -> testQuery.toUpperCase().contains(r.getQuestion().toUpperCase()))
                .collect(Collectors.toList());

        recommendations.forEach(System.out::println);

        //To clear all test keyword
        recommendations = recommendationDao.queryByKeyword("test");
        recommendations.forEach(r -> recommendationDao.delete(r));
    }


    public void handleContactMessage(Message message, Wechaty wechaty) {

    }
}
