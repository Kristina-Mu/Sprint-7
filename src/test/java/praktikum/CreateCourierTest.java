package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import praktikum.client.Courier;
import praktikum.client.CourierCredentials;
import praktikum.orders.CourierClient;

public class CreateCourierTest {

    private CourierClient courierClient;
    private Courier courier;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
    }

    @Test//курьера можно создать;
    @DisplayName("Создаём курьера, заполнив все обязательные поля")
    public void createCourierOnlyRequiredFieldsResponse201() {
        courier = Courier.getRandomRequiredField();
        Response response = courierClient.postCreateToCourier(courier);
        courierClient.compareResponseCodeAndBodyAboutCreation(response);

        CourierCredentials courierCredentials = CourierCredentials.from(courier);
        Response responseLogin = courierClient.postToCourierLogin(courierCredentials);
        courierClient.compareLoginResponseAndBodyIdNotNull(responseLogin);
    }

    @Test//нельзя создать двух одинаковых курьеров;
    @DisplayName("Создаём двух одинаковых курьеров")
    public void createTwoIdenticalCouriersResponse409() {
        courier = Courier.getRandom();
        Response response = courierClient.postCreateToCourier(courier);
        courierClient.compareResponseCodeAndBodyAboutCreation(response);

        Response responseDuplicate = courierClient.postCreateToCourier(courier);
        courierClient.compareResponseCodeAndMessageWithError409(responseDuplicate);
    }

    @Test
    @DisplayName("Создаём еще одного курьера с логином, который уже существует")
    public void createCourierWithLoginThatAlreadyExistsResponse409() {
        courier = Courier.getRandom();
        Response response = courierClient.postCreateToCourier(courier);
        courierClient.compareResponseCodeAndBodyAboutCreation(response);

        Courier duplicateCourier = new Courier(courier.getLogin(), "Mavis", "Taras");
        Response responseDuplicateLogin = courierClient.postCreateToCourier(duplicateCourier);
        courierClient.compareResponseCodeAndMessageWithError409(responseDuplicateLogin);
    }

    @Test
    @DisplayName("Создаем курьера с пустым логином")
    public void createCourierEmptyRequiredLoginResponse400() {
        courier = new Courier("", "Asassa", "Tarsa");

        Response response = courierClient.postCreateToCourier(courier);
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("Недостаточно данных для создания учетной записи", response.jsonPath().getString("message"));
    }

    @Test
    @DisplayName("Создаем курьера с пустым паролем")
    public void createCourierEmptyRequiredPasswordResponse400() {
        courier = new Courier("Kakadu", "", "Hahhyt");

        Response response = courierClient.postCreateToCourier(courier);
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("Недостаточно данных для создания учетной записи", response.jsonPath().getString("message"));
    }

    @After
    public void tearDown() {
        if (courier != null) {
            CourierCredentials courierCredentials = CourierCredentials.from(courier);
            Response response = courierClient.postToCourierLogin(courierCredentials);
            if (response.getStatusCode() == 200) {
                int courierId = response.then().extract().path("id");
                if (courierId != 0) {
                    courierClient.compareDeleteResponseCodeAndBodyOk(courierClient.deleteCourier(courierId));
                }
            }
        }
    }
}
