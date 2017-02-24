package com.predix.iot.edf.sqs.messaging.subscriber.live;

import java.io.IOException;
import java.util.Collection;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import geo.client.library.ClientParser;
import geo.client.library.GEOMessage;
import geo.client.library.schema.AttributeFactory;
import geo.client.library.schema.StructureFactory.Power24;
import geo.client.library.schema.StructureFactory.Power24.CommodityType;

/**
 * Created by 212337645 on 2/16/17.
 */

@Component
public class SQSListener implements MessageListener {

	@Autowired
	private SimpMessagingTemplate objSimpleMessageTemplate;

	private static final Logger LOGGER = LoggerFactory.getLogger(SQSListener.class);
	// String uaaUrl =
	// "https://ed606436-0a8e-4a86-9f07-ee67f36d846a.predix-uaa.run.aws-usw02-pr.ice.predix.io/oauth/token";
	// String uaaClientSecret = "ZWRmOmVkZg==";
	// String ingestionUrl =
	// "wss://gateway-predix-data-services.run.aws-usw02-pr.ice.predix.io/v1/stream/messages";
	// String predixZoneIdHeaderName = "predix-zone-id";
	// String predixZoneIdHeaderValue = "017fa50c-5f1b-44c4-8e5c-df191f96bce4";

	public void onMessage(Message message) {

		TextMessage textMessage = (TextMessage) message;

		try {
			LOGGER.info("Received message " + textMessage.getText());
			ClientParser parser = new ClientParser();
			Collection<GEOMessage> messages = parser.parse(textMessage.getText());
			for (GEOMessage geoMessage : messages) {
				WebSocketMessage objWebSocketMessage = new WebSocketMessage();
				AttributeFactory.Power24sBlock powerBlock = geoMessage
						.getAttribute(AttributeFactory.Power24sBlock.class);
				Power24[] objPower24 = powerBlock.getValue();
				if (objPower24 != null && objPower24.length > 0) {

					// construct the message for Websocket
					for (int i = 0; i < objPower24.length; i++) {
						if (objPower24[i].getCommodityType() != null
								&& objPower24[i].getCommodityType() == CommodityType.ELECTRICITY) {
							objWebSocketMessage.setElectricValue(objPower24[i].getPowerW());
						}
						if (objPower24[i].getCommodityType() != null
								&& objPower24[i].getCommodityType() == CommodityType.GAS_ENERGY) {
							objWebSocketMessage.setGasValue(objPower24[i].getPowerW());
						}
						// if user logged in then and then push the data to the
						// webscoket topic
						objWebSocketMessage.setTimeStamp(geoMessage.getUtc() * 1000);
					}

					// Publish the message for the topic associated with the
					// meterId over the Websocket
					ObjectMapper objMapper = new ObjectMapper();
					String jsonStr = objMapper.writeValueAsString(objWebSocketMessage);
					LOGGER.info("JSON Payload for websocket " + jsonStr);
					objSimpleMessageTemplate.convertAndSend("/topic/" + geoMessage.getId(), jsonStr);

				}

			}

		} catch (JMSException e) {
			LOGGER.error("Error reading message ", e);
		} catch (IOException ioe) {
			LOGGER.error("failed to parse message  ", ioe);
		} catch (Exception e) {
			LOGGER.error("failed processing message  ", e);
		}
	}
	/*
	 * private String getToken() { return
	 * AuthorizationHandler.getInstance().getAuthToken(this.uaaUrl,
	 * uaaClientSecret); }
	 * 
	 * 
	 * private Client getClientFactory(String token) throws Exception {
	 * TenantContext tenant = TenantContextFactory
	 * .createIngestionTenantContextFromProvidedProperties(ingestionUrl, token,
	 * predixZoneIdHeaderName, predixZoneIdHeaderValue); return
	 * ClientFactory.ingestionClientForTenant(tenant); }
	 * 
	 * private String getTimeSeriesPayload(String messageId, String tag,
	 * List<DataPoint> dataPointList, Map<String, String> attributes) throws
	 * IOException { IngestionTag.Builder temp =
	 * IngestionTag.Builder.createIngestionTag().withTagName(tag).addDataPoints(
	 * dataPointList); for (String key : attributes.keySet()) {
	 * temp.addAttribute(key, attributes.get(key)); } IngestionRequestBuilder
	 * ingestionRequestBuilder =
	 * IngestionRequestBuilder.createIngestionRequest().withMessageId(messageId)
	 * .addIngestionTag(temp.build()); return
	 * ingestionRequestBuilder.build().get(0); }
	 * 
	 * private String getTag(GEOMessage geoMessage, String commodityType, String
	 * readingType, String freqType) throws IOException { StringBuffer buf = new
	 * StringBuffer().append("@").append(geoMessage.getId()).append(":").append(
	 * commodityType)
	 * .append(":").append(readingType).append(":").append(freqType); return
	 * buf.toString(); }
	 */
}
