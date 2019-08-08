/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.test;

import com.jayway.restassured.RestAssured;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.DecoderConfig.decoderConfig;
import static com.jayway.restassured.config.EncoderConfig.encoderConfig;
import static com.jayway.restassured.config.RestAssuredConfig.config;
import static com.jayway.restassured.config.RestAssuredConfig.newConfig;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.path.json.config.JsonPathConfig;
import com.jayway.restassured.response.Response;
import dev.robertkopp.autocam.main.DevelopmentConfiguration;
import dev.robertkopp.autocam.main.CamApplication;
import dev.robertkopp.autocam.model.Camera;
import dev.robertkopp.autocam.model.ClientRaspi;
import dev.robertkopp.autocam.model.RaspiState;
import io.dropwizard.testing.junit.DropwizardAppRule;
import java.io.File;
import java.net.URISyntaxException;
import java.util.HashSet;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author robert kopp
 */
public class HeartbeatTest {

    public static String getPath() {
        try {
            return new File(HeartbeatTest.class.getResource("/development.yml").toURI()).getAbsolutePath();
        } catch (URISyntaxException ex) {
            return "";
        }
    }
    @ClassRule
    public static final DropwizardAppRule<DevelopmentConfiguration> RULE
            = new DropwizardAppRule<>(CamApplication.class, getPath());

    @BeforeClass
    public static void setUpClass() {
        RestAssured.config = newConfig().encoderConfig(encoderConfig().defaultContentCharset("UTF-8"));
        RestAssured.config = newConfig().decoderConfig(decoderConfig().defaultContentCharset("UTF-8"));
        JsonPath.config = new JsonPathConfig("UTF-8");
    }

    @AfterClass
    public static void tearDownClass() {

    }

    private ClientRaspi createDummy(String ip){
    ClientRaspi myself = new ClientRaspi();
        myself.setIpAdress(ip);
        myself.setHostId("mycoolraspi"+Math.random());
        myself.setRaspiState(RaspiState.Ok);
        myself.setCameras(new HashSet<Camera>());

        myself.getCameras().add(new Camera("test_cam"));
        return myself;
    }
    
    @Test
    public void testHasExpiredClients(){
        String posturl = String.format("http://%s:%s/api/clientraspi", "127.0.0.1", "8080");
        //String checkurl= String.format("http://%s:%s/api/clientraspi", "127.0.0.1", "8080");
        ClientRaspi activeclient= createDummy("192.168.0.1");
        activeclient.setLastPing(System.currentTimeMillis());
        Response resp = given().contentType("application/json").body(activeclient).when().post(posturl);
        ClientRaspi oldclient= createDummy("192.168.0.2");
        oldclient.setLastPing(System.currentTimeMillis()-20000);
         resp = given().contentType("application/json").body(oldclient).when().post(posturl);
        
         ClientRaspi[] result = given().contentType("application/json").when().get(posturl).as(ClientRaspi[].class);
         assertEquals(1,result.length);
        
    }
    
   
}
