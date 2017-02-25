package com.predix.iot.edf.sqs.messaging.subscriber.live;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * Created by 212337645 on 2/16/17.
 */
@Configuration
public class EDFSQSConfig {


	@Autowired
	private Environment env;

	@Autowired
	private SQSListener sqsListener;

	@Bean
	public DefaultMessageListenerContainer jmsListenerContainer() {

		SQSConnectionFactory sqsConnectionFactory = SQSConnectionFactory.builder()
				.withAWSCredentialsProvider(new EnvironmentVariableCredentialsProvider())
				// .withAWSCredentialsProvider(awsCredentialsProvider)
				.withEndpoint(env.getProperty("SQS_END_POINT")).withAWSCredentialsProvider(awsCredentialsProvider)
				.withNumberOfMessagesToPrefetch(10).build();

		DefaultMessageListenerContainer dmlc = new DefaultMessageListenerContainer();
		dmlc.setConnectionFactory(sqsConnectionFactory);
		dmlc.setDestinationName(env.getProperty("SQS_QUEUE_NAME"));

		dmlc.setMessageListener(sqsListener);

		return dmlc;
	}

	@Bean
	public JmsTemplate createJMSTemplate() {

		SQSConnectionFactory sqsConnectionFactory = SQSConnectionFactory.builder()
				.withAWSCredentialsProvider(awsCredentialsProvider).withEndpoint(env.getProperty("SQS_END_POINT"))
				.withNumberOfMessagesToPrefetch(10).build();

		JmsTemplate jmsTemplate = new JmsTemplate(sqsConnectionFactory);
		jmsTemplate.setDefaultDestinationName(env.getProperty("SQS_QUEUE_NAME"));
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
