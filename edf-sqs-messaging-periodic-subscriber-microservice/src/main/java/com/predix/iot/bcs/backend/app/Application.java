package com.predix.iot.bcs.backend.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;



@EnableAutoConfiguration(exclude =
    {
        //Add any configuration loading call you want to exclude

    })
@PropertySource("classpath:application-default.properties")
@SpringBootApplication
public class Application
{
    private static final Logger log = LoggerFactory.getLogger(Application.class);


    /**
     * @param args -
     */
    @SuppressWarnings(
        {
            "nls", "resource"
        })
    public static void main(String[] args)
    {
        SpringApplication springApplication = new SpringApplication(Application.class);
        ApplicationContext ctx = springApplication.run(args);

    }
    

}