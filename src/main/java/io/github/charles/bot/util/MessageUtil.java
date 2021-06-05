package io.github.charles.bot.util;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageUtil {

    private static Logger logger = LoggerFactory.getLogger(MessageUtil.class);

    //temporarily replace wechaty-java mentionText function as it is not implemented in the library
    public static String extractQuestion(String fullText, String keyword) {
        String trimMentionString = StringUtils.strip(fullText.replaceAll("@[\\p{L}\\p{Digit}_]+", " ").trim());
        return StringUtils.substring(trimMentionString.strip(), keyword.length()).strip();
    }
}
