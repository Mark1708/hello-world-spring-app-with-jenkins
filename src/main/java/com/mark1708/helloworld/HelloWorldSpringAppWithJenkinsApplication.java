package com.mark1708.helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.logging.Logger;

@SpringBootApplication
public class HelloWorldSpringAppWithJenkinsApplication {

    public static Logger logger = Logger.getLogger(HelloWorldSpringAppWithJenkinsApplication.class.getName());

    @PostConstruct
    public void init() {
        logger.info("Application started...");
    }

    public static void main(String[] args) {
        logger.info("Application executed...");
        SpringApplication.run(HelloWorldSpringAppWithJenkinsApplication.class, args);
    }

}
