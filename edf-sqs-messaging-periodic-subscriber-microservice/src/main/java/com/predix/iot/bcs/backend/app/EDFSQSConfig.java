package com.predix.iot.bcs.backend.app;

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
    private SQSListener sqsListener;

    @Autowired
    private Environment environment;


    @Bean
    public DefaultMessageListenerContainer jmsListenerContainer() {
        //endpoint = environment.getProperty("");
        SQSConnectionFactory sqsConnectionFactory = SQSConnectionFactory.builder()
            .withAWSCredentialsProvider(new EnvironmentVariableCredentialsProvider())
           // .withAWSCredentialsProvider(awsCredentialsProvider)
            .withEndpoint(environment.getProperty("SQS_END_POINT"))
            .withAWSCredentialsProvider(awsCredentialsProvider)
            .withNumberOfMessagesToPrefetch(10).build();

        DefaultMessageListenerContainer dmlc = new DefaultMessageListenerContainer();
        dmlc.setConnectionFactory(sqsConnectionFactory);
        dmlc.setDestinationName(environment.getProperty("SQS_QUEUE_NAME"));

        dmlc.setMessageListener(sqsListener);

        return dmlc;
    }

    @Bean
    public JmsTemplate createJMSTemplate() {

        SQSConnectionFactory sqsConnectionFactory = SQSConnectionFactory.builder()
            .withAWSCredentialsProvider(awsCredentialsProvider)
            .withEndpoint(environment.getProperty("SQS_END_POINT"))
            .withNumberOfMessagesToPrefetch(10).build();

        JmsTemplate jmsTemplate = new JmsTemplate(sqsConnectionFactory);
        jmsTemplate.setDefaultDestinationName(environment.getProperty("SQS_QUEUE_NAME"));
        jmsTemplate.setDeliveryPersistent(false);


        return jmsTemplate;
    }

    private final AWSCredentialsProvider awsCredentialsProvider = new AWSCredentialsProvider() {
        @Override
        public AWSCredentials getCredentials() {
            return new BasicAWSCredentials( environment.getProperty("AWS_ACCESS_KEY_ID"), environment.getProperty("AWS_SECRET_KEY"));
        }

        @Override
        public void refresh() {

        }
    };

}
