package core;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import static org.hamcrest.Matchers.*;

import listeners.TestListener;
import org.apache.commons.io.output.WriterOutputStream;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;

import java.io.PrintStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BaseTest implements Constantes{


    public static Faker faker = new Faker(new Locale("pt-BR"));
    public static String token;
    public  static StringWriter requestWriter;
    public static PrintStream requestCapture;
    public  static StringWriter responseWriter;
    public static PrintStream responseCapture;


    @BeforeClass
    public static void setUp() throws Exception {

        RestAssured.baseURI=APP_BASE_URL;
        RestAssured.basePath = APP_BASE_PATH;
        RestAssured.port = APP_PORT;

        RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
        reqBuilder.setContentType(APP_CONTENT_TYPE);
        RestAssured.requestSpecification = reqBuilder.build();

        ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
        resBuilder.expectResponseTime(lessThan(MAX_TIMEOUT));
        RestAssured.responseSpecification = resBuilder.build();

        Map<String,String> login = new HashMap<String, String>();
        login.put("email", AUTH_LOGIN);
        login.put("senha",AUTH_SENHA );
        token = RestAssured.given().body(login).when().post("/signin").then().statusCode(200).extract().path("token");
        RestAssured.requestSpecification.header("Authorization","JWT "+ token);

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }


}
