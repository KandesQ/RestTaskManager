package com.taskManager.RestTaskManager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.BooleanFlag;
import lombok.Data;

@Data
public class TaskRequestDto {
    @NotEmpty(message = "title mustn't be empty")
    @NotBlank(message = "username mustn't be blank")
    private String title;

    @NotEmpty(message = "description mustn't be empty")
    @NotBlank(message = "description mustn't be blank")
    private String description;
    @BooleanFlag
    private Boolean isDone;
}
