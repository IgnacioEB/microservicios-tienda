package usuario.service.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegistroRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;

    public RegistroRequest(String nombre, String password){
        this.email=nombre;
        this.password=password;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
