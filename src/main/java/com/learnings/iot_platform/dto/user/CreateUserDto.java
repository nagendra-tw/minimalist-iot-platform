package com.learnings.iot_platform.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDto {

    @Size(min = 2, max = 20, message = "Username length should be in between 2 and 20 characters only.")
    private String username;
    @Size(min = 8, max = 20, message = "Username length should be in between 2 and 20 characters only.")
    private String password;
    @Email(message = "valid Email address should be provided.")
    private String email;
}
