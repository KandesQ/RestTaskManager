package com.taskManager.RestTaskManager.service;

import com.taskManager.RestTaskManager.dto.TaskRequestDto;
import com.taskManager.RestTaskManager.dto.TaskResponseDto;
import com.taskManager.RestTaskManager.entity.TaskEntity;
import com.taskManager.RestTaskManager.entity.UserEntity;
import com.taskManager.RestTaskManager.exception.TaskNotFoundException;
import com.taskManager.RestTaskManager.exception.UserNotFoundException;
import com.taskManager.RestTaskManager.repository.TaskRepository;
import com.taskManager.RestTaskManager.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceUnitTests {
    @InjectMocks
    TaskService taskService;

    @Mock
    TaskRepository taskRepository;
    @Mock
    UserRepository userRepository;

    static List<TaskResponseDto> expectedTaskResponseDtos;
    static List<TaskEntity> expectedTaskEntities;
    static List<UserEntity> expectedUserEntities;

    @BeforeAll
    public static void staticInitialize() {
        expectedUserEntities = List.of(
                new UserEntity(1L, "user1", "password", LocalDateTime.of(2023, 3, 1, 0, 0), List.of()),
                new UserEntity(2L, "user2", "password", LocalDateTime.of(2023, 3, 1, 0, 0), List.of()),
                new UserEntity(3L, "user3", "password", LocalDateTime.of(2023, 3, 1, 0, 0), List.of())
        );
        expectedTaskEntities = List.of(
                new TaskEntity(1L, "user1task1", "desc", false, LocalDateTime.of(2023, 10, 8, 14, 30, 0, 0), expectedUserEntities.get(0)),
                new TaskEntity(2L, "user2task2", "desc", false, LocalDateTime.of(2023, 10, 8, 14, 30, 0, 0), expectedUserEntities.get(1)),
                new TaskEntity(3L, "user3task3", "desc", false, LocalDateTime.of(2023, 10, 8, 14, 30, 0, 0), expectedUserEntities.get(2)),
                new TaskEntity(4L, "user3task3", "desc1", false, LocalDateTime.of(2023, 10, 8, 14, 30, 0, 0), expectedUserEntities.get(2)),
                new TaskEntity(5L, "user3task4", "desc", false, LocalDateTime.of(2023, 10, 8, 14, 30, 0, 0), expectedUserEntities.get(2))
        );
        expectedTaskResponseDtos = List.of(
                TaskResponseDto.EntityToDto(expectedTaskEntities.get(0)),
                TaskResponseDto.EntityToDto(expectedTaskEntities.get(1)),
                TaskResponseDto.EntityToDto(expectedTaskEntities.get(2)),
                TaskResponseDto.EntityToDto(expectedTaskEntities.get(3)),
                TaskResponseDto.EntityToDto(expectedTaskEntities.get(4))
        );
    }

    @Test
    public void getAllTasksTest() throws TaskNotFoundException {
        Mockito.when(taskRepository.findAll()).thenReturn(expectedTaskEntities);

        List<TaskResponseDto> actualTaskResponseDtos = taskService.getAllTasks();

        Mockito.verify(taskRepository, Mockito.times(1)).findAll();

        assertEquals(expectedTaskResponseDtos, actualTaskResponseDtos);

    }

    @Test
    public void getAllTasksThrowsTaskNotFoundExceptionTest() {
        Mockito.when(taskRepository.findAll()).thenReturn(List.of());

        assertThrows(TaskNotFoundException.class, () -> taskService.getAllTasks());
        Mockito.verify(taskRepository, Mockito.times(1)).findAll();
    }

//    @Test
//    public void getUserTasksTest() {}
//
//    @Test
//    public void getUserTasksThrowsUserNotFoundExceptionTest() {}

    @Test
    public void getSimilarUserTasksTest() throws TaskNotFoundException {
        List<TaskResponseDto> expectedTaskResponseDtosList = List.of(
                expectedTaskResponseDtos.get(2),
                expectedTaskResponseDtos.get(3)
        );
        String expectedTaskTitle = expectedTaskResponseDtosList.get(0).getTitle();
        String expectedUserName = expectedTaskResponseDtosList.get(0).getOwnerName();

        Mockito.when(taskRepository.findAllByTitle(expectedTaskTitle)).thenReturn(List.of(
                expectedTaskEntities.get(2),
                expectedTaskEntities.get(3)
        ));

        ArgumentCaptor<String> findTaskByTitleCaptor = ArgumentCaptor.forClass(String.class);

        List<TaskResponseDto> actualTaskResponseDtos = taskService.getSimilarUserTasks(expectedUserName, expectedTaskTitle);

        Mockito.verify(taskRepository, Mockito.times(1)).findAllByTitle(findTaskByTitleCaptor.capture());

        String actualTaskTitle = findTaskByTitleCaptor.getValue();

        assertEquals(expectedTaskTitle, actualTaskTitle);
        assertNotEquals(expectedTaskResponseDtosList, List.of());
        assertEquals(expectedTaskResponseDtosList, actualTaskResponseDtos);
    }

    @Test
    public void getSimilarUserTasksThrowsTaskNotFoundExceptionTest() {
        Mockito.when(taskRepository.findAllByTitle(Mockito.anyString())).thenReturn(List.of());

        assertThrows(TaskNotFoundException.class, () -> taskService.getSimilarUserTasks("Some username", "some taskTitle"));
    }

    @Test
    public void createTaskTest() throws UserNotFoundException {
        TaskRequestDto expectedTaskRequestDto = new TaskRequestDto("task1", "desc", false);
        UserEntity supposedUserEntity = expectedUserEntities.get(0);

        TaskEntity expectedTaskEntity = TaskEntity.dtoToEntity(expectedTaskRequestDto);
        expectedTaskEntity.setUserEntity(supposedUserEntity);

        String supposedUserName = supposedUserEntity.getUsername();

        ArgumentCaptor<String> userNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<TaskEntity> taskEntityCaptor = ArgumentCaptor.forClass(TaskEntity.class);

        Mockito.when(userRepository.findByUsername(supposedUserName)).thenReturn(supposedUserEntity);

        taskService.createTask(supposedUserName, expectedTaskRequestDto);

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(userNameCaptor.capture());
        Mockito.verify(taskRepository, Mockito.times(1)).save(taskEntityCaptor.capture());

        String actualUserName = userNameCaptor.getValue();
        TaskEntity actualTaskEntity = taskEntityCaptor.getValue();

        // set a strict LocalDateTime that equals() and hashCode() can work correctly
        LocalDateTime strictLocalDateTime = LocalDateTime.now();
        expectedTaskEntity.setCreatedAt(strictLocalDateTime);
        actualTaskEntity.setCreatedAt(strictLocalDateTime);

        assertEquals(supposedUserName, actualUserName);
        assertEquals(expectedTaskEntity, actualTaskEntity);
    }

    @Test
    public void createTaskThrowsUserNotFoundExceptionTest() {
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> taskService.createTask("some user", new TaskRequestDto()));
    }

    @Test
    public void deleteAllTasksTest() throws TaskNotFoundException {
        Mockito.when(taskRepository.count()).thenReturn(1L);

        taskService.deleteAllTasks();

        Mockito.verify(taskRepository, Mockito.times(1)).deleteAll();
    }

    @Test
    public void deleteAllTasksThrowsTaskNotFoundExceptionTest() {
        assertThrows(TaskNotFoundException.class, () -> taskService.deleteAllTasks());
    }

//    @Test
//    public void deleteTaskTest() {}
//
//    @Test
//    public void deleteTaskThrowsUserNotFoundExceptionTest() {}
//
//    @Test
//    public void deleteTaskThrowsTaskNotFoundExceptionTest() {}
//
//    @Test
//    public void completeTaskTest() {}
//
//    @Test
//    public void completeTaskThrowsUserNotFoundExceptionTest() {}
//
//    @Test
//    public void completeTaskThrowsTaskNotFoundExceptionTest() {}
}