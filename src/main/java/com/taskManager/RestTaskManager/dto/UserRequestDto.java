package com.taskManager.RestTaskManager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserRequestDto {
    @NotEmpty(message = "username mustn't be empty or null")
    @NotBlank(message = "username mustn't be blank")
    private String username;

    @NotEmpty(message = "password mustn't be empty or null")
    @NotBlank(message = "password mustn't be blank")
    private String password;
}
