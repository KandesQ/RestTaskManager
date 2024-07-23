package com.taskManager.RestTaskManager.entity;

import com.taskManager.RestTaskManager.dto.TaskRequestDto;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private Boolean isDone;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    public static TaskEntity dtoToEntity(TaskRequestDto taskRequestDto) {
        TaskEntity taskEntity = new TaskEntity();

        taskEntity.setTitle(taskRequestDto.getTitle());
        taskEntity.setDescription(taskRequestDto.getDescription());
        taskEntity.setIsDone(taskRequestDto.getIsDone());
        taskEntity.setCreatedAt(LocalDateTime.now());

        return taskEntity;
    }
}
