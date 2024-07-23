package com.taskManager.RestTaskManager.dto;

import com.taskManager.RestTaskManager.entity.UserEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private List<TaskResponseDto> taskResponseDtos;
    private LocalDateTime registeredAt;

    public static UserResponseDto EntityToDto(UserEntity userEntity) {
        UserResponseDto userResponseDto = new UserResponseDto();

        userResponseDto.id = userEntity.getId();
        userResponseDto.username = userEntity.getUsername();
        userResponseDto.taskResponseDtos = userEntity.getTaskEntities().stream()
                .map(TaskResponseDto::EntityToDto)
                .toList();
        userResponseDto.registeredAt = userEntity.getRegisteredAt();

        return userResponseDto;
    }
}
