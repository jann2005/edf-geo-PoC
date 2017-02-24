package com.predix.iot.edf.sqs.messaging.subscriber.live;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

//@EnableAutoConfiguration(exclude = {
//		// Add any configuration loading call you want to exclude
//
//})
//@PropertySource("classpath:application-default.properties")
//@SpringBootApplication
//public class PredixEdfMessagingSubscriberLiveApplication {
//
//	private static final Logger log = LoggerFactory.getLogger(PredixEdfMessagingSubscriberLiveApplication.class);
//
//	@SuppressWarnings({ "nls", "resource" })
//	public static void main(String[] args) {
//		SpringApplication springApplication = new SpringApplication(PredixEdfMessagingSubscriberLiveApplication.class);
//		ApplicationContext ctx = springApplication.run(args);
//	}
//}
//

@EnableAutoConfiguration(exclude =
	{
		//Add any configuration loading call you want to exclude

	})
@PropertySource("classpath:application-default.properties")
@ComponentScan(basePackages="com.predix.iot")
@SpringBootApplication
public class Application
{
	private static final Logger log = LoggerFactory.getLogger(Application.class);



	public static void main(String[] args)
	{
		SpringApplication springApplication = new SpringApplication(Application.class);
		ApplicationContext ctx = springApplication.run(args);

	}

	@Bean
	public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {
		return new TomcatEmbeddedServletContainerFactory();
	}
}