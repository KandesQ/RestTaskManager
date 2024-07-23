package com.taskManager.RestTaskManager.dto;

import com.taskManager.RestTaskManager.entity.TaskEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskResponseDto {
    private Long id;
    private String title;
    private String desc;
    private Boolean isDone;
    private String ownerName;
    private LocalDateTime createdAt;

    public static TaskResponseDto EntityToDto(TaskEntity taskEntity) {
        TaskResponseDto taskResponseDto = new TaskResponseDto();

        taskResponseDto.id = taskEntity.getId();
        taskResponseDto.title = taskEntity.getTitle();
        taskResponseDto.desc = taskEntity.getDescription();
        taskResponseDto.isDone = taskEntity.getIsDone();
        taskResponseDto.ownerName = taskEntity.getUserEntity().getUsername();
        taskResponseDto.createdAt = taskEntity.getCreatedAt();

        return taskResponseDto;
    }
}
