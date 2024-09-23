package com.taskManager.RestTaskManager.service;

import com.taskManager.RestTaskManager.dto.TaskRequestDto;
import com.taskManager.RestTaskManager.entity.TaskEntity;
import com.taskManager.RestTaskManager.entity.UserEntity;
import com.taskManager.RestTaskManager.exception.TaskNotFoundException;
import com.taskManager.RestTaskManager.exception.UserNotFoundException;
import com.taskManager.RestTaskManager.dto.TaskResponseDto;
import com.taskManager.RestTaskManager.repository.TaskRepository;
import com.taskManager.RestTaskManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;


    public List<TaskResponseDto> getAllTasks() throws TaskNotFoundException {
        List<TaskResponseDto> taskResponseDtos = new ArrayList<>();
        List<TaskEntity> taskEntities = taskRepository.findAll();

        if (taskEntities.isEmpty()) {
            throw new TaskNotFoundException("There's no tasks in database");
        }

        taskEntities.forEach(taskEntity -> {
            TaskResponseDto taskResponseDto = TaskResponseDto.EntityToDto(taskEntity);
            taskResponseDtos.add(taskResponseDto);
        });
        return taskResponseDtos;
    }

    // public List<TaskResponseDto> getUserTasks(String username) {}

    // returns all username tasks with similar task title
    public List<TaskResponseDto> getSimilarUserTasks(String username, String taskTitle) throws TaskNotFoundException {
        List<TaskEntity> taskEntities = taskRepository.findAllByTitle(taskTitle);

        if (taskEntities.isEmpty()) {
            throw new TaskNotFoundException("User " + username + " doesn't have tasks with title " + taskTitle);
        }

        List<TaskResponseDto> taskResponseDtos = taskEntities.stream()
                .filter(taskEntity -> taskEntity.getUserEntity().getUsername().equals(username))
                .map(TaskResponseDto::EntityToDto)
                .toList();

        return taskResponseDtos;
    }

    public void createTask(String username, TaskRequestDto taskRequestDto) throws UserNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            throw new UserNotFoundException("User " + username + " doesn't exist");
        }
        TaskEntity taskEntity = TaskEntity.dtoToEntity(taskRequestDto);
        taskEntity.setUserEntity(userEntity);
        taskRepository.save(taskEntity);
    }

    public void deleteAllTasks() throws TaskNotFoundException {
        if (taskRepository.count() == 0) {
            throw new TaskNotFoundException("There are no tasks in database");
        }
        taskRepository.deleteAll();
    }

    public void deleteTask(String username, Long taskId) throws TaskNotFoundException, UserNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username);
        TaskEntity deletableTask;
        if (userEntity == null) {
            throw new UserNotFoundException(username + " doesn't exist");
        }
        try {
            deletableTask = userEntity.getTaskEntities().stream()
                    .filter(taskEntity -> taskEntity.getId().equals(taskId))
                    .findFirst().get();
        } catch (NoSuchElementException e) {
            throw new TaskNotFoundException(username + " doesn't have task with id=" + taskId);
        }
        taskRepository.deleteById(deletableTask.getId());
    }

    public void completeTask(String username, Long taskId) throws TaskNotFoundException, UserNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username);
        TaskEntity completableTask;

        if (userEntity == null) {
            throw new UserNotFoundException(username + " doesn't exist");
        }

        try {
            completableTask = userEntity.getTaskEntities().stream()
                    .filter(taskEntity -> taskEntity.getId().equals(taskId))
                    .findFirst().get();
        } catch (NoSuchElementException e) {
            throw new TaskNotFoundException(username + " doesn't have task with id=" + taskId);
        }
        completableTask.setIsDone(!completableTask.getIsDone());
        taskRepository.save(completableTask);
}
}
