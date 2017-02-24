package com.ge.predix.iot.pae.edf.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;

/**
 * An example of creating a Rest api using Spring Annotations @RestController.
 * 
 * 
 * 
 * @author predix
 */
@RestController
public class LiveStreamController
{

//    private static final String USERNAME = "ge-user";
//    private static final String PASSWORD = "Gr33nD3v1c3_";
//    private static final String TRIGGER_URL = "http://solo3.energynote.eu/api/supportapi/system/trigger-fastupdate/";
//    private static final URI LOGIN_URI = URI.create("http://solo3.energynote.eu/api/userapi/account/login");

    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    Environment environment;
    /**
     * 
     */
    public LiveStreamController()
    {
        super();
    }



    @RequestMapping("/live/{systemId}")
    public ResponseEntity trigerLive (@PathVariable("systemId") String systemId) throws Exception {
        URI trigger_uri = URI.create(environment.getProperty("TRIGGER_URL") + systemId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + getLoginToken());

        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity<String> result = restTemplate.exchange(trigger_uri, HttpMethod.GET, entity, String.class);
        if(result.getStatusCode() != HttpStatus.OK){
            throw new Exception("Cannot trigger fast update for " + systemId);
        }
        return ResponseEntity.ok(null);
    }

    private String getLoginToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest(environment.getProperty("USERNAME"), environment.getProperty("PASSWORD"));
        ResponseEntity<Map> response = restTemplate.postForEntity(URI.create(environment.getProperty("LOGIN_URI")), loginRequest, Map.class);
        if(response.getStatusCode() != HttpStatus.OK){
            throw new Exception("Could not obtain login token");
        }
        return (String) response.getBody().get("token");
    }

    class LoginRequest implements Serializable {
        private final String name;
        private final String password;

        public LoginRequest(String name, String password){
            this.name = name;
            this.password = password;
        }

        public String getName () {
            return name;
        }

        public String getPassword () {
            return password;
        }
    }

}
