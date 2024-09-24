package com.taskManager.RestTaskManager.service;

import ch.qos.logback.core.testUtil.MockInitialContext;
import com.taskManager.RestTaskManager.dto.TaskRequestDto;
import com.taskManager.RestTaskManager.dto.TaskResponseDto;
import com.taskManager.RestTaskManager.entity.TaskEntity;
import com.taskManager.RestTaskManager.entity.UserEntity;
import com.taskManager.RestTaskManager.exception.TaskNotFoundException;
import com.taskManager.RestTaskManager.exception.UserNotFoundException;
import com.taskManager.RestTaskManager.repository.TaskRepository;
import com.taskManager.RestTaskManager.repository.UserRepository;
import org.apache.catalina.User;
import org.hibernate.sql.model.ast.builder.TableUpdateBuilderSkipped;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.config.Task;

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

    @Test
    public void getAllTasksTest() throws TaskNotFoundException {
        UserEntity expectedUserEntity = new UserEntity();
        List<TaskEntity> expectedTaskEntities = List.of(
                new TaskEntity(1L, "user1task1", "desc", false, LocalDateTime.of(2023, 10, 8, 14, 30, 0, 0), expectedUserEntity),
                new TaskEntity(2L, "user2task2", "desc", false, LocalDateTime.of(2023, 10, 8, 14, 30, 0, 0), expectedUserEntity),
                new TaskEntity(3L, "user3task3", "desc", false, LocalDateTime.of(2023, 10, 8, 14, 30, 0, 0), expectedUserEntity)
                );
        List<TaskResponseDto> expectedTaskResponseDtos = List.of(
                TaskResponseDto.EntityToDto(expectedTaskEntities.get(0)),
                TaskResponseDto.EntityToDto(expectedTaskEntities.get(1)),
                TaskResponseDto.EntityToDto(expectedTaskEntities.get(2))
        );

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

    @Test
    public void getSimilarUserTasksTest() throws TaskNotFoundException {
        UserEntity expectedUserEntity = new UserEntity();
        expectedUserEntity.setUsername("user");
        List<TaskEntity> expectedTaskEntities = List.of(
                new TaskEntity(3L, "user3task3", "desc", false, LocalDateTime.of(2023, 10, 8, 14, 30, 0, 0), expectedUserEntity),
                new TaskEntity(4L, "user3task3", "desc1", false, LocalDateTime.of(2023, 10, 8, 14, 30, 0, 0), expectedUserEntity)
                );

        List<TaskResponseDto> expectedTaskResponseDtosList = List.of(
                TaskResponseDto.EntityToDto(expectedTaskEntities.get(0)),
                TaskResponseDto.EntityToDto(expectedTaskEntities.get(1))
        );
        String expectedTaskTitle = expectedTaskEntities.get(0).getTitle();
        String expectedUserName = expectedUserEntity.getUsername();

        Mockito.when(taskRepository.findAllByTitle(expectedTaskTitle)).thenReturn(expectedTaskEntities);

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

        assertThrows(TaskNotFoundException.class, () -> taskService.getSimilarUserTasks("", ""));
    }

    @Test
    public void createTaskTest() throws UserNotFoundException {
        TaskRequestDto expectedTaskRequestDto = new TaskRequestDto("task1", "desc", false);
        UserEntity expectedUserEntity = new UserEntity(1L, "user1", "password", LocalDateTime.of(2023, 3, 1, 0, 0), List.of());

        TaskEntity expectedTaskEntity = TaskEntity.dtoToEntity(expectedTaskRequestDto);
        expectedTaskEntity.setUserEntity(expectedUserEntity);

        String expectedUserName = expectedUserEntity.getUsername();

        ArgumentCaptor<String> userNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<TaskEntity> taskEntityCaptor = ArgumentCaptor.forClass(TaskEntity.class);

        Mockito.when(userRepository.findByUsername(expectedUserName)).thenReturn(expectedUserEntity);

        taskService.createTask(expectedUserName, expectedTaskRequestDto);

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(userNameCaptor.capture());
        Mockito.verify(taskRepository, Mockito.times(1)).save(taskEntityCaptor.capture());

        String actualUserName = userNameCaptor.getValue();
        TaskEntity actualTaskEntity = taskEntityCaptor.getValue();

        // set a strict LocalDateTime that equals() and hashCode() can work correctly
        LocalDateTime strictLocalDateTime = LocalDateTime.now();
        expectedTaskEntity.setCreatedAt(strictLocalDateTime);
        actualTaskEntity.setCreatedAt(strictLocalDateTime);

        assertEquals(expectedUserName, actualUserName);
        assertEquals(expectedTaskEntity, actualTaskEntity);
    }

    @Test
    public void createTaskThrowsUserNotFoundExceptionTest() {
        assertThrows(UserNotFoundException.class, () -> taskService.createTask(Mockito.anyString(), null));
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

    @Test
    public void deleteTaskTest() throws UserNotFoundException, TaskNotFoundException {
        UserEntity expectedUserEntity = new UserEntity(3L, "user", "password", LocalDateTime.of(2023, 3, 1, 0, 0), List.of());
        expectedUserEntity.setTaskEntities(List.of(
                new TaskEntity(3L, "user3task3", "desc", false, LocalDateTime.of(2023, 10, 8, 14, 30, 0, 0), expectedUserEntity),
                new TaskEntity(4L, "user3task3", "desc1", false, LocalDateTime.of(2023, 10, 8, 14, 30, 0, 0), expectedUserEntity),
                new TaskEntity(5L, "user3task4", "desc", false, LocalDateTime.of(2023, 10, 8, 14, 30, 0, 0), expectedUserEntity)
        ));

        String expectedUserName = expectedUserEntity.getUsername();
        Long expectedDeletableTaskId = expectedUserEntity.getTaskEntities().get(0).getId();

        ArgumentCaptor<String> userNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> deletableTaskIdCaptor = ArgumentCaptor.forClass(Long.class);

        Mockito.when(userRepository.findByUsername(expectedUserName)).thenReturn(expectedUserEntity);

        taskService.deleteTask(expectedUserName, expectedDeletableTaskId);

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(userNameCaptor.capture());
        Mockito.verify(taskRepository, Mockito.times(1)).deleteById(deletableTaskIdCaptor.capture());

        String actualUserName = userNameCaptor.getValue();
        Long actualDeletableTaskId = deletableTaskIdCaptor.getValue();

        assertEquals(expectedUserName, actualUserName);
        assertEquals(expectedDeletableTaskId, actualDeletableTaskId);
    }

    @Test
    public void deleteTaskThrowsUserNotFoundExceptionTest() {
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> taskService.deleteTask(Mockito.anyString(), 0L));
    }

    @Test
    public void deleteTaskThrowsTaskNotFoundExceptionTest() {
        UserEntity emptyTaskListUser = new UserEntity();
        emptyTaskListUser.setTaskEntities(List.of());

        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(emptyTaskListUser);

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(Mockito.anyString(),0L));

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(Mockito.anyString());

    }

    @Test
    public void completeTaskTest() throws UserNotFoundException, TaskNotFoundException {
        UserEntity expectedUserEntity = new UserEntity(1L, "user1", "password", LocalDateTime.of(2023, 3, 1, 0, 0), List.of(
                new TaskEntity(1L, "user1task1", "desc", false, LocalDateTime.of(2023, 10, 8, 14, 30, 0, 0), new UserEntity())
        ));
        expectedUserEntity.getTaskEntities().get(0).setUserEntity(expectedUserEntity);

        TaskEntity expectedTaskEntity = expectedUserEntity.getTaskEntities().get(0);
        String expectedUserName = expectedUserEntity.getUsername();
        Boolean expectedTaskStatus = !expectedTaskEntity.getIsDone();

        Mockito.when(userRepository.findByUsername(expectedUserName)).thenReturn(expectedUserEntity);

        ArgumentCaptor<String> expectedUserNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<TaskEntity> completableTaskEntityCaptor = ArgumentCaptor.forClass(TaskEntity.class);

        taskService.completeTask(expectedUserName, expectedTaskEntity.getId());

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(expectedUserNameCaptor.capture());
        Mockito.verify(taskRepository, Mockito.times(1)).save(completableTaskEntityCaptor.capture());

        String actualUserName = expectedUserNameCaptor.getValue();
        TaskEntity actualTaskEntity = completableTaskEntityCaptor.getValue();
        Boolean actualTaskStatus = actualTaskEntity.getIsDone();

        assertEquals(expectedUserName, actualUserName);
        assertEquals(expectedTaskStatus, actualTaskStatus);
        assertEquals(expectedTaskEntity, actualTaskEntity);
    }

    @Test
    public void completeTaskThrowsUserNotFoundExceptionTest() {
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> taskService.completeTask(Mockito.anyString(), 0L));
    }

    @Test
    public void completeTaskThrowsTaskNotFoundExceptionTest() {
        UserEntity expectedUserEntity = new UserEntity();
        expectedUserEntity.setTaskEntities(List.of());

        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(expectedUserEntity);

        assertThrows(TaskNotFoundException.class, () -> taskService.completeTask(Mockito.anyString(), 0L));
    }
}