package praktikum;

import io.qameta.allure.junit4.DisplayName;
import org.junit.*;
import io.restassured.response.Response;
import praktikum.client.Courier;
import praktikum.client.CourierCredentials;
import praktikum.orders.CourierClient;

public class CourierLoginTest {
    private CourierClient courierClient;
    private Courier courier;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = Courier.getRandom();
        Response createResponse = courierClient.postCreateToCourier(courier);
        Assert.assertEquals(201, createResponse.getStatusCode()); // Убедитесь, что курьер создан успешно
    }

    @Test
    @DisplayName("Проверка входа с валидными данными")//курьер может авторизоваться;
    public void checkCourierCanLoginWithValidCredentialsResponse200() {
        CourierCredentials validCredentials = CourierCredentials.from(courier);
        Response response = courierClient.postToCourierLogin(validCredentials);
        courierClient.compareLoginResponseAndBodyIdNotNull(response); // Ожидается, что вернется id
    }

    @Test
    @DisplayName("Проверка входа с пустыми логином и паролем")
    public void checkLoginCourierWithInvalidCredentialsResponse400() {
        CourierCredentials invalidCredentials = new CourierCredentials("", "");
        Response response = courierClient.postToCourierLogin(invalidCredentials);
        courierClient.compareLoginResponseCodeAndBody400Message(response); // Ожидается ошибка 400
    }

    @Test//если какого-то поля нет, запрос возвращает ошибку;
    @DisplayName("Проверка входа с пустым логином")
    public void checkLoginCourierWithEmptyLoginResponse400() {
        CourierCredentials invalidCredentials = new CourierCredentials("", courier.getPassword());
        Response response = courierClient.postToCourierLogin(invalidCredentials);
        courierClient.compareLoginResponseCodeAndBody400Message(response); // Ожидается ошибка 400
    }

    @Test
    @DisplayName("Проверка входа с пустым паролем")
    public void checkLoginCourierWithEmptyPasswordResponse400() {
        CourierCredentials invalidCredentials = new CourierCredentials(courier.getLogin(), "");
        Response response = courierClient.postToCourierLogin(invalidCredentials);
        courierClient.compareLoginResponseCodeAndBody400Message(response); // Ожидается ошибка 400
    }

    @Test//система вернёт ошибку, если неправильно указать логин или пароль;
    @DisplayName("Проверка входа с не валидным логином")
    public void checkLoginCourierIncorrectLoginNameResponse404() {
        CourierCredentials invalidCredentials = new CourierCredentials("InvalidLogin", courier.getPassword());
        Response response = courierClient.postToCourierLogin(invalidCredentials);
        courierClient.compareLoginResponseCodeAndBody404Message(response); // Ожидается ошибка 404
    }

    @Test
    @DisplayName("Проверка входа с не валидным паролем")
    public void checkLoginCourierIncorrectPasswordResponse404() {
        CourierCredentials invalidCredentials = new CourierCredentials(courier.getLogin(), "InvalidPassword");
        Response response = courierClient.postToCourierLogin(invalidCredentials);
        courierClient.compareLoginResponseCodeAndBody404Message(response); // Ожидается ошибка 404
    }

    @Test//если авторизоваться под несуществующим пользователем, запрос возвращает ошибку;
    @DisplayName("Проверка авторизации несуществующего пользователя")
    public void checkLoginCourierWithNonExistingUserResponse404() {
        CourierCredentials invalidCredentials = new CourierCredentials("NonExistingUser", "SomePassword");
        Response response = courierClient.postToCourierLogin(invalidCredentials);
        courierClient.compareLoginResponseCodeAndBody404Message(response); // Ожидается ошибка 404
    }


    @After
    public void tearDown() {
        if (courier != null) {
            CourierCredentials validCredentials = CourierCredentials.from(courier);
            int courierId = courierClient.postToCourierLogin(validCredentials)
                    .then().extract().path("id");
            if (courierId != 0) { // Проверка, что id существует
                Response response = courierClient.deleteCourier(courierId);
                courierClient.compareDeleteResponseCodeAndBodyOk(response); // Удаление курьера, проверка статуса удаления
            }
        }
    }
}