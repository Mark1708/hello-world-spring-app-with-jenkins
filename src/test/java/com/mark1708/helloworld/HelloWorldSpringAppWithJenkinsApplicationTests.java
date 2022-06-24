package com.mark1708.helloworld;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.logging.Logger;

@SpringBootTest
class HelloWorldSpringAppWithJenkinsApplicationTests {

    public static Logger logger = Logger.getLogger(HelloWorldSpringAppWithJenkinsApplicationTests.class.getName());

    @Test
   public void contextLoads() {
        logger.info("Test case executing ...");
        Assertions.assertEquals(true, true);
    }

}
