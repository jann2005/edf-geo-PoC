package com.predix.iot.edf.sqs.messaging.subscriber.live;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;

/**
 * Created by 212337645 on 2/16/17.
 */
@Configuration
public class JMSSQSConfig {

	// @Value("${applications.queue.endpoint}")

	// @Value("${END_POINT}")
	String endpoint = "https://sqs.eu-west-1.amazonaws.com/182236743652/geo_predix_sqs_live";
	//
	// // @Value("${applications.queue.name}")
	// @Value("${QUEUE_NAME}")
	String queueName = "geo_predix_sqs_live";
	//
	// @Value("${AWS_SECRET_KEY}")
	// private String awsSecretKey;
	//
	// @Value("${AWS_ACCESS_KEY_ID}")
	// private String awsSecretKeyId;

	@Autowired
	private Environment env;

	@Autowired
	private SQSListener sqsListener;

	@Bean
	public DefaultMessageListenerContainer jmsListenerContainer() {

		SQSConnectionFactory sqsConnectionFactory = SQSConnectionFactory.builder()
				.withAWSCredentialsProvider(new EnvironmentVariableCredentialsProvider())
				// .withAWSCredentialsProvider(awsCredentialsProvider)
				.withEndpoint(endpoint).withAWSCredentialsProvider(awsCredentialsProvider)
				.withNumberOfMessagesToPrefetch(10).build();

		DefaultMessageListenerContainer dmlc = new DefaultMessageListenerContainer();
		dmlc.setConnectionFactory(sqsConnectionFactory);
		dmlc.setDestinationName(queueName);

		dmlc.setMessageListener(sqsListener);

		return dmlc;
	}

	@Bean
	public JmsTemplate createJMSTemplate() {

		SQSConnectionFactory sqsConnectionFactory = SQSConnectionFactory.builder()
				.withAWSCredentialsProvider(awsCredentialsProvider).withEndpoint(endpoint)
				.withNumberOfMessagesToPrefetch(10).build();

		JmsTemplate jmsTemplate = new JmsTemplate(sqsConnectionFactory);
		jmsTemplate.setDefaultDestinationName(queueName);
		jmsTemplate.setDeliveryPersistent(false);

		return jmsTemplate;
	}

	private final AWSCredentialsProvider awsCredentialsProvider = new AWSCredentialsProvider() {
		@Override
		public AWSCredentials getCredentials() {
			return new BasicAWSCredentials(env.getProperty("AWS_ACCESS_KEY_ID"), env.getProperty("AWS_SECRET_KEY"));
		}

		@Override
		public void refresh() {

		}
	};

}
