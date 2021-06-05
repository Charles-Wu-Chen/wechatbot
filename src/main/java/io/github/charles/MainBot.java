package io.github.charles;


import io.github.charles.model.SingleWechaty;


public class MainBot {
    public static void main(String[] args) throws Exception {

        SingleWechaty.getInstance();
        TimerBot te1 = new TimerBot();
        te1.run();

    }
}
