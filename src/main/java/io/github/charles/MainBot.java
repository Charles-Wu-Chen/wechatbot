package io.github.charles;


import io.github.charles.model.SingleWechaty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MainBot {
    private static Logger logger = LoggerFactory.getLogger(MainBot.class);
    private static final String PUPPET_HOSTIE_TOKEN = System.getenv("WECHATY_PUPPET_HOSTIE_TOKEN");

    public static String loginUserName = StringUtils.EMPTY;

    public static void main(String[] args) throws Exception {

        SingleWechaty.getInstance();
        TimerBot te1 = new TimerBot();
        te1.run();

    }
}
