package com.predix.iot.bcs.backend.app;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthorizationHandler{

    static Logger log = Logger.getLogger(AuthorizationHandler.class.getName());

    String uaaURL;
    String encodedClientSecret="ZWRmOmVkZg==";
    Map<String,String> uaaToTokenMap;
    Map<String,Long> tokenMetaDataMap;
    private static long token_expiry_interval = 10*60*60*1000;   // 10  hours

    private static AuthorizationHandler instance;

    public static synchronized AuthorizationHandler getInstance(){
        if( instance == null){
            instance = new AuthorizationHandler();
        }
        return instance;
    }

    private AuthorizationHandler(){
        uaaToTokenMap = new ConcurrentHashMap<String, String>();
        tokenMetaDataMap = new ConcurrentHashMap<String, Long>();
    }

    public String getAuthToken( String uaaURL , String encodedClientSecret){
        String key = getUaaToTokenMap( uaaURL , encodedClientSecret);
        String token = uaaToTokenMap.get(key);
        if( token==null || expired(token)){
            if(token!=null && expired(token)){
                log.error(" token expired");
            }
            token = getToken( uaaURL , encodedClientSecret);
            log.info("Got new Token : " + token);
            log.info("Got token at  : " + System.currentTimeMillis());
            System.out.println("Token : " + token );
        }else{
            log.info("Reusing valid token from map");
        }
        return token;
    }
    protected boolean expired( String token){
        return System.currentTimeMillis() - tokenMetaDataMap.get(token) >  token_expiry_interval;
    }
    private String getToken(String uaaURL , String encodedClientSecret){
        log.info("Get Token");
        // HttpHost proxy = new HttpHost("proxy-src.research.ge.com", 8080 , "http");
        CloseableHttpClient httpClient = null;
        //if(PropertyManager.getInstance().get("proxy.host") != null) {

        httpClient = HttpClientBuilder.create().build();


        List<Header> headerList = new ArrayList<Header>();
        headerList.add(new BasicHeader("Content-Type","application/x-www-form-urlencoded"));
        String oauthHeader = "Basic " + " " + encodedClientSecret;
        headerList.add(new BasicHeader("Authorization", oauthHeader));
        headerList.add(new BasicHeader("Pragma", "no-cache"));
        String requestBody = "grant_type=client_credentials";
        String token = null;
        try {
            token = getTokenFromUAAInStance(uaaURL, httpClient, requestBody, headerList);
            uaaToTokenMap.put(getUaaToTokenMap( uaaURL , encodedClientSecret) , token);
            tokenMetaDataMap.put(token , System.currentTimeMillis());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info(token);
        return token;
    }
    private  String getTokenFromUAAInStance(String url, CloseableHttpClient httpClient,
                                            String queryParams, List<Header> headers)
        throws UnsupportedEncodingException, IOException,
               ClientProtocolException {
        String url2 = url;
        url2 += "?" + queryParams;
        HttpGet method = new HttpGet(url2);
        method.setHeaders(headers.toArray(new Header[headers.size()]));
        CloseableHttpResponse httpResponse = httpClient.execute(method);
        if (httpResponse.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("unable able to connect to the UAA url="
                                       + url2 + " response=" + httpResponse);
        }
        HttpEntity responseEntity = httpResponse.getEntity();

        String token = EntityUtils.toString(responseEntity);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = null;
//        token = getToken(uaaURL, httpClient, requestBody, headerList);
        node = (ObjectNode) mapper.readTree(token);
        httpResponse.close();
        return node.get("access_token").asText();
    }
    private String getUaaToTokenMap( String uaaURL , String clientSecret){
        StringBuffer buf = new StringBuffer(uaaURL).append("_").append(clientSecret);
        return buf.toString();
    }

}

