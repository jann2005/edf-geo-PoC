package com.predix.iot.bcs.backend.app;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * Created by 212337645 on 2/16/17.
 */
@Configuration
public class JMSSQSConfig {

   // @Value("${applications.queue.endpoint}")
    private String endpoint="https://sqs.eu-west-1.amazonaws.com/182236743652/geo_predix_sqs";

   // @Value("${applications.queue.name}")
    private String queueName="geo_predix_sqs";

    @Autowired
    private SQSListener sqsListener;

    @Bean
    public DefaultMessageListenerContainer jmsListenerContainer() {

        SQSConnectionFactory sqsConnectionFactory = SQSConnectionFactory.builder()
            .withAWSCredentialsProvider(new EnvironmentVariableCredentialsProvider())
           // .withAWSCredentialsProvider(awsCredentialsProvider)
            .withEndpoint(endpoint)
            .withAWSCredentialsProvider(awsCredentialsProvider)
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
            .withAWSCredentialsProvider(awsCredentialsProvider)
            .withEndpoint(endpoint)
            .withNumberOfMessagesToPrefetch(10).build();

        JmsTemplate jmsTemplate = new JmsTemplate(sqsConnectionFactory);
        jmsTemplate.setDefaultDestinationName(queueName);
        jmsTemplate.setDeliveryPersistent(false);


        return jmsTemplate;
    }

    private final AWSCredentialsProvider awsCredentialsProvider = new AWSCredentialsProvider() {
        @Override
        public AWSCredentials getCredentials() {
            return new BasicAWSCredentials("AKIAIG4P7CE7YCRTP55A", "Uz8O1r/1I4VtJYWjcI+k2WaRIEFreOjx775rSjc2");
        }

        @Override
        public void refresh() {

        }
    };

}