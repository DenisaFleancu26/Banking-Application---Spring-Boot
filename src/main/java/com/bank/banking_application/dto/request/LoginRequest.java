package com.bank.banking_application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @Schema(name = "Email")
    @NotBlank(message = "Email is required!")
    @Email(message = "Email must be valid!")
    private String email;

    @Schema(name = "Password")
    @NotBlank(message = "Password is required!")
    private String password;
}
