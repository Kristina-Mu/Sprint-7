package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import praktikum.client.OrdersClient;
import praktikum.orders.Order;

import java.util.List;

    @RunWith(Parameterized.class)
    public class CreateOrderTest {

        private final List<String> colorScooter;
        int track;
        OrdersClient orderClient;

        public CreateOrderTest(List<String> colorScooter) {
            this.colorScooter = colorScooter;
        }

        @Parameterized.Parameters
        public static Object[] getOrderCreation() {
            return new Object[][]{
                    {List.of()},    //можно совсем не указывать цвет;
                    {List.of("BLACK", "GREY")},   //можно указать оба цвета;
                    {List.of("GREY")},  //можно указать один из цветов — BLACK или GREY;
                    {List.of("BLACK")},
            };
        }

        @Before
        public void setUp() {
            RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
            orderClient = new OrdersClient();
        }

        @Test
        @DisplayName("Создать заказ с разным цветом скутера")
        public void orderCreateByScooterColor() {
            Order order = Order.createOrderWithColor(colorScooter);
            Response response = orderClient.sendPostCreateToOrders(order);
            orderClient.compareResponseCodeAndBodyAboutOrderCreation(response);
            track = response.then().extract().path("track");   //тело ответа содержит track.
            Response responseGet = orderClient.sendGetToTrackOrder(track);
            orderClient.compareResponse200(responseGet);
        }
    }