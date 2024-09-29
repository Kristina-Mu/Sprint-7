package praktikum;


import io.qameta.allure.junit4.DisplayName;
import org.junit.*;
import io.restassured.response.Response;
import praktikum.client.Courier;
import praktikum.client.OrdersClient;
import praktikum.client.CourierCredentials;


public class LoginCourierTest {
    private OrdersClient courierClient;
    private Courier courier;

    @Before
    public void setUp() {
        courierClient = new OrdersClient();
        courier = Courier.getRandom();
        courierClient.postCreateToCourier(courier);
    }
    @Test
    @DisplayName("Проверка входа с валидными данными")
    public void checkCourierCanLoginWithValidCredentialsResponse200() {
        CourierCredentials courierCredentialsCorrect = CourierCredentials.from(courier);
        Response response = courierClient.postToCourierLogin(courierCredentialsCorrect);
        courierClient.compareLoginResponseAndBodyIdNotNull(response);
    }
    @Test
    @DisplayName("Проверка входа с пустыми логином и паролем")
    public void checkLoginCourierWithInvalidCredentialsResponse400() {
        CourierCredentials courierCredentialsIncorrect = new CourierCredentials("", "");
        Response response = courierClient.postToCourierLogin(courierCredentialsIncorrect);
        courierClient.compareLoginResponseCodeAndBody400Message(response);
    }
    @Test
    @DisplayName("Проверка входа с пустым логином")
    public void checkLoginCourierWitEmptyLoginResponse400() {
        CourierCredentials courierCredentialsIncorrect = new CourierCredentials("", courier.getPassword());
        Response response = courierClient.postToCourierLogin(courierCredentialsIncorrect);
        courierClient.compareLoginResponseCodeAndBody400Message(response);
    }
    @Test
    @DisplayName("Проверка входа с пустым паролем")
    public void checkLoginCourierWitEmptyPasswordResponse400() {
        CourierCredentials courierCredentialsIncorrect = new CourierCredentials(courier.getLogin(), "");
        Response response = courierClient.postToCourierLogin(courierCredentialsIncorrect);
        courierClient.compareLoginResponseCodeAndBody400Message(response);
    }
    @Test
    @DisplayName("Проверка входа с не валидным логином")
    public void checkLoginCourierIncorrectLoginNameResponse404() {
        CourierCredentials courierCredentialsIncorrect = new CourierCredentials("Kotani", courier.getPassword());
        Response response = courierClient.postToCourierLogin(courierCredentialsIncorrect);
        courierClient.compareLoginResponseCodeAndBody404Message(response);
    }
    @Test
    @DisplayName("Проверка входа с не валидным паролем")
    public void checkLoginCourierIncorrectPasswordResponse404() {
        CourierCredentials courierCredentialsIncorrect = new CourierCredentials(courier.getLogin(), "Azerty");
        Response response = courierClient.postToCourierLogin(courierCredentialsIncorrect);
        courierClient.compareLoginResponseCodeAndBody404Message(response);
    }
    @After
    public void tearDown() {
        CourierCredentials courierCredentialsCorrect = CourierCredentials.from(courier);
        int courierId = courierClient.postToCourierLogin(courierCredentialsCorrect)
                .then().extract().path("id");
        Response response = courierClient.deleteCourier(courierId);
        courierClient.compareDeleteResponseCodeAndBodyOk(response);
    }
}