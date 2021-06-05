package io.github.charles.model;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.SetSystemProperty;


public class SingleWechatyTest {

    @Test
    @SetSystemProperty(key = "WECHATY_PUPPET_HOSTIE_TOKEN", value = "newvalue")
    void shallHaveSameValueForDiffInstance() throws Exception {
        //SingleWechaty singleton = SingleWechaty.getInstance();
        //SingleWechaty anotherSingleton = SingleWechaty.getInstance();
        //assertEquals(singleton, anotherSingleton);
    }

}
