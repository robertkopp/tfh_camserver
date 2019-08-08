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
public class RegistrationTest {

    public static String getPath() {
        try {
            return new File(RegistrationTest.class.getResource("/development.yml").toURI()).getAbsolutePath();
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

    @Test
    public void testClientRegistration() {

        String url = String.format("http://%s:%s/api/clientraspi", "127.0.0.1", "8080");

        ClientRaspi myself = new ClientRaspi();
        myself.setIpAdress("127.0.0.1");
        myself.setHostId("mycoolraspi");
        myself.setRaspiState(RaspiState.Ok);
        myself.setCameras(new HashSet<Camera>());

        myself.getCameras().add(new Camera("test_cam"));

        Response resp = given().contentType("application/json").body(myself).when().post(url);
        Assert.assertEquals(201, resp.getStatusCode());

        resp = given().contentType("application/json").when().get(url);
        ClientRaspi[] raspis = resp.as(ClientRaspi[].class);
        assertNotNull(raspis);
        assertNotNull(raspis[0]);
        assertEquals("mycoolraspi", raspis[0].getHostId());

        myself.setHostId("dhcp_refresh_simulation");
        resp = given().contentType("application/json").body(myself).when().post(url);
        Assert.assertEquals(201, resp.getStatusCode());

        resp = given().contentType("application/json").when().get(url);
        raspis = resp.as(ClientRaspi[].class);
        assertNotNull(raspis);
        assertNotNull(raspis[0]);
        assertEquals("dhcp_refresh_simulation", raspis[0].getHostId());

    }

}
