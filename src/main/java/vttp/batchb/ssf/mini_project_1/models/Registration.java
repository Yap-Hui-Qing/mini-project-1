package vttp.batchb.ssf.mini_project_1.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Registration {

    @NotEmpty(message = "Username cannot be empty")
    @NotNull(message = "Username cannot be null")
    @Size(min = 3, message = "Please enter a username that is at least 3 characters long")
    private String username;

    @NotEmpty(message = "Password cannot be empty")
    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Please enter a password that is at least 8 characters long")
    private String password;

    @NotEmpty(message = "Please type your password again.")
    @NotNull(message = "Please type your password again.")
    private String passwordAgain;

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
    public String getPasswordAgain() {
        return passwordAgain;
    }
    public void setPasswordAgain(String passwordAgain) {
        this.passwordAgain = passwordAgain;
    }
    
}
