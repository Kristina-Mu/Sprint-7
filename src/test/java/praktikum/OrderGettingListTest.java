package praktikum;


import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import praktikum.client.OrdersClient;
import praktikum.orders.CourierClient;


public class ListOrderTest {
    OrdersClient orderClient;

    @Before
    public void setUp() {
        orderClient = new CourierClient();
    }
    @Test
    @DisplayName("Проверка, что список заказов содержится в теле ответа")
    public void checkListOfOrdersContainedInResponse() {
        Response response = orderClient.sendGetToOrders();
        orderClient.compareResponse200(response);
        orderClient.isResponseBodyHaveOrdersList(response);
    }
}