package com.taskManager.RestTaskManager.service;

import com.taskManager.RestTaskManager.dto.UserResponseDto;
import com.taskManager.RestTaskManager.entity.UserEntity;
import com.taskManager.RestTaskManager.exception.UserNotFoundException;
import com.taskManager.RestTaskManager.repository.UserRepository;
import com.zaxxer.hikari.metrics.micrometer.MicrometerMetricsTracker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTests {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserService userService;
    // testing data
    static List<UserResponseDto> userResponseDtos;
    static  List<UserEntity> userEntities;

    // testing only no-args methods

    @BeforeAll
    public static void staticInitialize() {
        userResponseDtos = List.of(
                new UserResponseDto(1L, "user1", List.of(), LocalDateTime.of(2023, 3, 1, 0, 0)),
                new UserResponseDto(2L, "user2", List.of(), LocalDateTime.of(2023, 3, 1, 0, 0)),
                new UserResponseDto(3L, "user3", List.of(), LocalDateTime.of(2023, 3, 1, 0, 0))
        );
        userEntities = List.of(
                new UserEntity(1L, "user1", "password", LocalDateTime.of(2023, 3, 1, 0, 0), List.of()),
                new UserEntity(2L, "user2", "password", LocalDateTime.of(2023, 3, 1, 0, 0), List.of()),
                new UserEntity(3L, "user3", "password", LocalDateTime.of(2023, 3, 1, 0, 0), List.of())
        );
    }

    @Test
    public void getAllUsersTest() throws UserNotFoundException {
        Mockito.when(userRepository.findAll()).thenReturn(userEntities);

        Assertions.assertIterableEquals(userResponseDtos, userService.getAllUsers());
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void getAllUsersThrowsUserNotFoundExceptionTest() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.getAllUsers());
    }

    @Test
    public void deleteAllUsersTest() {
        userRepository.deleteAll();

        Mockito.verify(userRepository, Mockito.times(1)).deleteAll();
    }

    @Test
    public void deleteAllUsersThrowsUserTest() {
        // mockito default return for primitives is 0 (for Objects - null)
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.deleteAllUsers());
    }
}