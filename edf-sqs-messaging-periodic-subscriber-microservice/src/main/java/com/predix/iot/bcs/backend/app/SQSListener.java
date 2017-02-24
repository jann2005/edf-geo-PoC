package com.predix.iot.bcs.backend.app;

/**
 * Created by 212337645 on 2/16/17.
 */


import com.ge.predix.timeseries.client.Client;
import com.ge.predix.timeseries.client.ClientFactory;
import com.ge.predix.timeseries.client.TenantContext;
import com.ge.predix.timeseries.client.TenantContextFactory;
import com.ge.predix.timeseries.model.builder.IngestionRequestBuilder;
import com.ge.predix.timeseries.model.builder.IngestionTag;
import com.ge.predix.timeseries.model.datapoints.DataPoint;
import com.ge.predix.timeseries.model.response.IngestionResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import geo.client.library.ClientParser;
import geo.client.library.GEOMessage;
import geo.client.library.schema.AttributeFactory;
import geo.client.library.schema.StructureFactory;

/**
 * Created by 212337645 on 2/16/17.
 */

@Component
public class SQSListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQSListener.class);
    String
        uaaUrl =
        "https://ed606436-0a8e-4a86-9f07-ee67f36d846a.predix-uaa.run.aws-usw02-pr.ice.predix.io/oauth/token";
    String uaaClientSecret = "ZWRmOmVkZg==";
    String ingestionUrl = "wss://gateway-predix-data-services.run.aws-usw02-pr.ice.predix.io/v1/stream/messages";
    String predixZoneIdHeaderName = "predix-zone-id";
    String predixZoneIdHeaderValue = "017fa50c-5f1b-44c4-8e5c-df191f96bce4";

    public void onMessage(Message message) {

        TextMessage textMessage = (TextMessage) message;

        try {
            LOGGER.info("Received message " + textMessage.getText());
            ClientParser parser = new ClientParser();
            Collection<GEOMessage> messages = parser.parse(textMessage.getText());
            for (GEOMessage geoMessage : messages) {
                LOGGER.info(" UTC " + geoMessage.getUtc() + " resourceId " + geoMessage.getResourceId());
                // Consumption attributes
                AttributeFactory.Attribute k0033Attribute = geoMessage.getAttribute("k0033");
                StructureFactory.MeterReadingSmets2[]
                    meterReadingSmets2s =
                    ((AttributeFactory.MeterReadingSmets2Block) k0033Attribute).getValue();
                LOGGER.info("meter reading " + meterReadingSmets2s[0]);

                String token = getToken();
                Client client = getClientFactory(token);
                Long gasReadingTimestamp = null;
                Long electricReadingTimestamp = null;
                for (StructureFactory.MeterReadingSmets2 temp : meterReadingSmets2s) {
                    List<DataPoint> dataPoints = new ArrayList<>();
                    DataPoint dataPoint = new DataPoint(temp.getReadingTime()*1000, temp.getTotalConsumption());
                    dataPoints.add(dataPoint);
                    String
                        timeseriesPayload =
                        getTimeSeriesPayload(UUID.randomUUID().toString(),
                                             getTag(geoMessage, temp.getCommodityType().toString(), "Total",
                                                    "Periodic"), dataPoints, new HashMap<>());
                    IngestionResponse response = client.ingest(timeseriesPayload);
                    String responseStr = response.getMessageId() + response.getStatusCode();
                    LOGGER.info("Time series response :" + responseStr);
                    if (temp.getCommodityType().toString().equalsIgnoreCase("ELECTRICITY")) {
                        electricReadingTimestamp = temp.getReadingTime()*1000;
                    }

                    if (temp.getCommodityType().toString().equalsIgnoreCase("GAS_ENERGY")) {
                        gasReadingTimestamp = temp.getReadingTime()*1000;
                    }


                }

                // Cost attribute processing for Electric

                AttributeFactory.CurrentCostsSmets2ElecBlock
                    k0035Attribute =
                    geoMessage.getAttribute(AttributeFactory.CurrentCostsSmets2ElecBlock.class);
                StructureFactory.PeriodCostsSmets2[] costElectricMeterReadingSmets2s = k0035Attribute.getValue();
                LOGGER.info("meter reading " + meterReadingSmets2s[0]);

                for (StructureFactory.PeriodCostsSmets2 temp : costElectricMeterReadingSmets2s) {
                    List<DataPoint> costDataPoints = new ArrayList<>();
                    if (temp.getDuration() == StructureFactory.PeriodCostsSmets2.Duration.DAY) {
                        DataPoint costdataPoint = new DataPoint(electricReadingTimestamp, temp.getCostAmount());

                        costDataPoints.add(costdataPoint);
                        String
                            costtimeseriesPayload =
                            getTimeSeriesPayload(UUID.randomUUID().toString(),
                                                 getTag(geoMessage, temp.getCommodityType().toString(), "Cost",
                                                        "Periodic"), costDataPoints, new HashMap<>());
                        IngestionResponse response = client.ingest(costtimeseriesPayload);
                        String responseStr = response.getMessageId() + response.getStatusCode();
                        LOGGER.info("Time series response for Electric Cost:" + responseStr);
                    }
                }

                // Cost attribute processing for Gas

                AttributeFactory.CurrentCostsSmets2GasBlock
                    k0036Attribute =
                    geoMessage.getAttribute(AttributeFactory.CurrentCostsSmets2GasBlock.class);
                StructureFactory.PeriodCostsSmets2[] costGasMeterReadingSmets2s = k0036Attribute.getValue();

                for (StructureFactory.PeriodCostsSmets2 temp : costGasMeterReadingSmets2s) {
                    List<DataPoint> costDataPoints = new ArrayList<>();
                    if (temp.getDuration() == StructureFactory.PeriodCostsSmets2.Duration.DAY) {
                        DataPoint costdataPoint = new DataPoint(gasReadingTimestamp, temp.getCostAmount());

                        costDataPoints.add(costdataPoint);
                        String
                            costtimeseriesPayload =
                            getTimeSeriesPayload(UUID.randomUUID().toString(),
                                                 getTag(geoMessage, temp.getCommodityType().toString(), "Cost",
                                                        "Periodic"), costDataPoints, new HashMap<>());
                        IngestionResponse response = client.ingest(costtimeseriesPayload);
                        String responseStr = response.getMessageId() + response.getStatusCode();
                        LOGGER.info("Time series response for Gas Cost :" + responseStr);
                    }
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

    private String getToken() {
        return AuthorizationHandler.getInstance().getAuthToken(this.uaaUrl, uaaClientSecret);
    }

    private Client getClientFactory(String token) throws Exception {
        TenantContext
            tenant =
            TenantContextFactory
                .createIngestionTenantContextFromProvidedProperties(ingestionUrl, token, predixZoneIdHeaderName,
                                                                    predixZoneIdHeaderValue);
        return ClientFactory.ingestionClientForTenant(tenant);
    }

    private String getTimeSeriesPayload(String messageId, String tag, List<DataPoint> dataPointList,
                                        Map<String, String> attributes) throws IOException {
        IngestionTag.Builder
            temp =
            IngestionTag.Builder.createIngestionTag().withTagName(tag).addDataPoints(dataPointList);
        for (String key : attributes.keySet()) {
            temp.addAttribute(key, attributes.get(key));
        }
        IngestionRequestBuilder
            ingestionRequestBuilder =
            IngestionRequestBuilder.createIngestionRequest().withMessageId(messageId).addIngestionTag(temp.build());
        return ingestionRequestBuilder.build().get(0);
    }

    private String getTag(GEOMessage geoMessage, String commodityType, String readingType, String freqType)
        throws IOException {
        StringBuffer
            buf =
            new StringBuffer().append("@").append(geoMessage.getId()).append(":").append(commodityType).append(":")
                .append(readingType).append(":").append(freqType);
        return buf.toString();
    }
}

