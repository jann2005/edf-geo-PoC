package com.predix.iot.bcs.backend.app;

//import com.ge.predix.timeseries.client.Client;
//import com.ge.predix.timeseries.client.ClientFactory;
//import com.ge.predix.timeseries.client.TenantContext;
//import com.ge.predix.timeseries.client.TenantContextFactory;
//import com.ge.predix.timeseries.model.builder.IngestionRequestBuilder;
//import com.ge.predix.timeseries.model.builder.IngestionTag;
//import com.ge.predix.timeseries.model.datapoints.DataPoint;
//import com.ge.predix.timeseries.model.datapoints.Quality;
//import com.ge.predix.timeseries.model.response.IngestionResponse;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.UUID;
//
//import geo.client.library.GEOMessage;

//import com.ge.predix.timeseries.client.ClientFactory;
//import com.ge.predix.timeseries.client.TenantContext;
//import com.ge.predix.timeseries.client.TenantContextFactory;
//import com.ge.predix.timeseries.model.builder.IngestionRequestBuilder;
//import com.ge.predix.timeseries.model.builder.IngestionTag;
//import com.ge.predix.timeseries.model.datapoints.DataPoint;
//import com.ge.predix.timeseries.model.datapoints.Quality;
//import com.ge.predix.timeseries.model.response.IngestionResponse;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//import java.util.UUID;

/**
 * Created by 212337645 on 2/23/17.
 */
public class TSIngestor {

//    private static final Logger LOGGER = LoggerFactory.getLogger(TSIngestor.class);
//    private static final String DELIMETER = ",";
//    private static final String HEADER = "Date";
//    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyymmdd hh:mm", Locale.ENGLISH);
//
//    public static void main(String[] args) throws Exception {
//
//            System.out.println("TS ingetion");
//
//            BufferedReader br = null;
//            String line;
//
//            String token = getToken();
//            Client client = getClientFactory(token);
//
//            List<DataPoint> costDataPoints = new ArrayList<>();
//            List<DataPoint> consumptionDataPoints = new ArrayList<>();
//
//            String uuid = "38400000-8cf0-11bd-b23e-10b96e4ef00e";
//
//            BigDecimal runningCostTotal = new BigDecimal(0);
//            BigDecimal runningConsumptionTotal = new BigDecimal(0);
//            int linecount = 0;
//            try {
//                br = new BufferedReader(new FileReader("/Users/212337645/electricity.csv"));
//                while ((line = br.readLine()) != null) {
//                    linecount++;
//                    System.out.println("Processing line : " + line);
//                    if (!line.contains(HEADER)) {
//
//                        String[] values = line.split(DELIMETER);
//                        Date date = DATE_FORMAT.parse(values[0]);
//                        Double costPounds = Double.parseDouble(values[1]);
//                        Double consumptionKiloWatt = Double.parseDouble(values[3]);
//
//                        runningCostTotal = runningCostTotal.add(new BigDecimal(costPounds));
//                        runningConsumptionTotal = runningConsumptionTotal.add(new BigDecimal(consumptionKiloWatt));
//
//                        costDataPoints.add(new DataPoint(date.getTime(),
//                                                         runningCostTotal.multiply(new BigDecimal(100 * 1000)).longValue(),
//                                                         Quality.GOOD));
//                        consumptionDataPoints.add(new DataPoint(date.getTime(),
//                                                                runningConsumptionTotal.multiply(new BigDecimal(1000)).longValue(), Quality.GOOD));
//
//                        if( consumptionDataPoints.size() == 1 ) {
//                            String
//                                costTimeseriesPayload =
//                                getTimeSeriesPayload(UUID.randomUUID().toString(), "@" + uuid + ":ELECTRICITY:Total:Periodic",
//                                                     costDataPoints, new HashMap<>());
//                            IngestionResponse costResponse = client.ingest(costTimeseriesPayload);
//                            String responseStr = costResponse.getMessageId() + costResponse.getStatusCode();
//                            LOGGER.info("Time series response :" + responseStr);
//
//                            String
//                                consumptionTimeseriesPayload =
//                                getTimeSeriesPayload(UUID.randomUUID().toString(), "@" + uuid + ":ELECTRICITY:Cost:Periodic",
//                                                     consumptionDataPoints, new HashMap<>());
//                            IngestionResponse consumptionResponse = client.ingest(consumptionTimeseriesPayload);
//                            String consumptionResponseStr = consumptionResponse.getMessageId() + consumptionResponse.getStatusCode();
//                            LOGGER.info("Time series response :" + consumptionResponseStr);
//                            consumptionDataPoints.clear();
//                            costDataPoints.clear();
//                            Thread.sleep(100);
//                            LOGGER.info("line processed  : " + linecount);
//                        }
//
//
//                    }
//                }
//
//                LOGGER.info("Total line processed  : " + linecount);
//
//
//            } catch (Exception exc) {
//                exc.printStackTrace();
//            }
//
//    }
//
//    private static String getToken() {
//        return AuthorizationHandler.getInstance().getAuthToken("https://ed606436-0a8e-4a86-9f07-ee67f36d846a.predix-uaa.run.aws-usw02-pr.ice.predix.io/oauth/token", "ZWRmOmVkZg==");
//    }
//
//    private static Client getClientFactory(String token) throws Exception {
//        TenantContext
//            tenant =
//            TenantContextFactory
//                .createIngestionTenantContextFromProvidedProperties("wss://gateway-predix-data-services.run.aws-usw02-pr.ice.predix.io/v1/stream/messages", token, "predix-zone-id",
//                                                                    "017fa50c-5f1b-44c4-8e5c-df191f96bce4");
//        return ClientFactory.ingestionClientForTenant(tenant);
//    }
//
//    private static String getTimeSeriesPayload(String messageId, String tag, List<DataPoint> dataPointList,
//                                        Map<String, String> attributes) throws IOException {
//        IngestionTag.Builder
//            temp =
//            IngestionTag.Builder.createIngestionTag().withTagName(tag).addDataPoints(dataPointList);
//        for (String key : attributes.keySet()) {
//            temp.addAttribute(key, attributes.get(key));
//        }
//        IngestionRequestBuilder
//            ingestionRequestBuilder =
//            IngestionRequestBuilder.createIngestionRequest().withMessageId(messageId).addIngestionTag(temp.build());
//        return ingestionRequestBuilder.build().get(0);
//    }
//
//    private String getTag(GEOMessage geoMessage, String commodityType, String readingType, String freqType)
//        throws IOException {
//        StringBuffer
//            buf =
//            new StringBuffer().append("@").append(geoMessage.getId()).append(":").append(commodityType).append(":")
//                .append(readingType).append(":").append(freqType);
//        return buf.toString();
//    }
}
