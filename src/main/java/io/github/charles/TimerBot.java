package io.github.charles;

import io.github.charles.model.SingleWechaty;
import io.github.charles.util.CommonUtil;

import java.util.Timer;
import java.util.TimerTask;

public class TimerBot extends TimerTask {

    static Timer timer = new Timer();
    @Override
    public void run() {
        try {
            SingleWechaty singleWechaty = SingleWechaty.getInstance();
            singleWechaty.sendMessage("timer bot is waking up", "wxid_1194601945911");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            int delay = CommonUtil.generateRandomBetween(2 * 60 * 1000, 5 * 60 * 1000);
            timer.schedule(new TimerBot(), delay);
        }
    }
}
