package own.star.scatter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebApplication {
    private static final Logger logger = LoggerFactory.getLogger(WebApplication.class);
    public static void main(String[] args) {
        try {
            SpringApplication.run(WebApplication.class, args);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            throw e;
        }
    }

}
