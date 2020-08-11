package com.chan.springboot;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Springboot03LoggingApplicationTests {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    void contextLoads() {
        logger.trace("这是trace...");
        logger.debug("这是debug...");
        logger.info("这是info...");
        logger.warn("这是warn...");
        logger.error("这是error...");
    }

}
