package it.infn.mw.iam.scim;

import static com.jayway.restassured.RestAssured.given;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.mapper.factory.Jackson2ObjectMapperFactory;

public class TestUtils {

  private TestUtils() {}

  public static ObjectMapper createJacksonObjectMapper() {

    FilterProvider filters = new SimpleFilterProvider().setFailOnUnknownId(false);

    ObjectMapper mapper = new ObjectMapper();
    mapper.setFilterProvider(filters);
    return mapper;

  }

  public static Jackson2ObjectMapperFactory getJacksonObjectMapperFactory() {

    return new Jackson2ObjectMapperFactory() {

      @Override
      public ObjectMapper create(@SuppressWarnings("rawtypes") Class cls, String charset) {

        return createJacksonObjectMapper();
      }
    };
  }

  public static void initRestAssured() {

    RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
        new ObjectMapperConfig().jackson2ObjectMapperFactory(getJacksonObjectMapperFactory()));
    RestAssured.port = 8080;
  }

  public static String getAccessToken(String clientId, String clientSecret, String scopes) {

    String accessToken = given().port(8080)
      .param("grant_type", "client_credentials")
      .param("client_id", clientId)
      .param("client_secret", clientSecret)
      .param("scope", scopes)
      .when()
      .post("/token")
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .extract()
      .path("access_token");

    return accessToken;
  }

}
