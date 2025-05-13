package com.bank.banking_application.dto;

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

    @NotBlank(message = "First name is required!")
    private String firstName;

    @NotBlank(message = "Last name is required!")
    private String lastName;

    @NotBlank(message = "Gender is required!")
    @Pattern(regexp = "Male|Female|Other", message = "Gender must be Male, Female, or Other")
    private String gender;

    @NotBlank(message = "Address is required!")
    private String address;

    @NotBlank(message = "State of origin is required!")
    private String stateOfOrigin;

    @NotBlank(message = "Email is required!")
    @Email(message = "Email must be valid!")
    private String email;

    @NotBlank(message = "Phone number is required!")
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 digits!")
    private String phoneNumber;

}
