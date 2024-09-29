package praktikum.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CourierCredentials {
    @JsonProperty("login")
    private final String login;

    @JsonProperty("password")
    private final String password;

    public CourierCredentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public CourierCredentials(Courier courier) {
        this.login = courier.getLogin();
        this.password = courier.getPassword();
    }

    public static CourierCredentials from(Courier courier) {
        return new CourierCredentials(courier);
    }

    @Override
    public String toString() {
        return "{ login= \"" + login + "\", password= \"" + password + "\" }";
    }
}
