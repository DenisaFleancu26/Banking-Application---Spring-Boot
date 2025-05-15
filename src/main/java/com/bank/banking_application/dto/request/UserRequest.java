package com.bank.banking_application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    @Schema(name = "First Name")
    @NotBlank(message = "First name is required!")
    private String firstName;

    @Schema(name = "Last Name")
    @NotBlank(message = "Last name is required!")
    private String lastName;

    @Schema(name = "Gender")
    @NotBlank(message = "Gender is required!")
    @Pattern(regexp = "Male|Female|Other", message = "Gender must be Male, Female, or Other")
    private String gender;

    @Schema(name = "Address")
    @NotBlank(message = "Address is required!")
    private String address;

    @Schema(name = "State of Origin")
    @NotBlank(message = "State of origin is required!")
    private String stateOfOrigin;

    @Schema(name = "Email")
    @NotBlank(message = "Email is required!")
    @Email(message = "Email must be valid!")
    private String email;

    @Schema(name = "Password")
    @NotBlank(message = "Password is required!")
    @Email(message = "Password must be valid!")
    private String password;

    @Schema(name = "Phone Number")
    @NotBlank(message = "Phone number is required!")
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 digits!")
    private String phoneNumber;

}
