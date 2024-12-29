package vttp.batchb.ssf.mini_project_1.models;

import java.io.StringReader;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class User {

    @NotEmpty(message = "Username cannot be empty")
    @NotNull(message = "Username cannot be null")
    private String username;

    @NotEmpty(message = "Password cannot be empty")
    @NotNull(message = "Password cannot be null")
    private String password;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public JsonObject toJson() {
        JsonObject obj = Json.createObjectBuilder()
            .add("username", this.username)
            .add("password", this.password)
            .build();
        return obj;
    }

    public static User toUser(String json){
        User user = new User();

        // convert the string to json
        JsonReader reader = Json.createReader(new StringReader(json));
        JsonObject obj = reader.readObject();

        user.setUsername(obj.getString("username"));
        user.setPassword(obj.getString("password"));
        return user;
    }

}
