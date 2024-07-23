package com.taskManager.RestTaskManager.entity;

import com.taskManager.RestTaskManager.dto.UserRequestDto;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Data
@NoArgsConstructor
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private LocalDateTime registeredAt;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userEntity")
    private List<TaskEntity> taskEntities;

    public static UserEntity dtoToEntity(UserRequestDto userRequestDto) {
        UserEntity userEntity = new UserEntity();

        userEntity.setUsername(userRequestDto.getUsername());
        userEntity.setPassword(userRequestDto.getPassword());
        userEntity.setRegisteredAt(LocalDateTime.now());

        return userEntity;
    }
}
