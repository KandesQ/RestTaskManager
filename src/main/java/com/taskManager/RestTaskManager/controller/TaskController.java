package com.taskManager.RestTaskManager.controller;

import com.taskManager.RestTaskManager.dto.TaskRequestDto;
import com.taskManager.RestTaskManager.entity.TaskEntity;
import com.taskManager.RestTaskManager.exception.TaskNotFoundException;
import com.taskManager.RestTaskManager.exception.UserNotFoundException;
import com.taskManager.RestTaskManager.repository.TaskRepository;
import com.taskManager.RestTaskManager.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<?> getAllTasks() throws TaskNotFoundException {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getSimilarUserTasks(
            @PathVariable @NotEmpty String username,
            @RequestParam @NotNull @NotBlank String taskTitle
    ) throws TaskNotFoundException {
        return ResponseEntity.ok(taskService.getSimilarUserTasks(username, taskTitle));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTask(
            @Valid @RequestBody TaskRequestDto taskRequestDto,
            @RequestParam @NotEmpty String username) throws UserNotFoundException {
        taskService.createTask(username, taskRequestDto);
        return ResponseEntity.ok("Task " + taskRequestDto.getTitle() + " was created for " + username);
    }

    @DeleteMapping("/delete/all")
    public ResponseEntity<?> deleteAllUsers() throws UserNotFoundException {
        taskService.deleteAllTasks();
        return ResponseEntity.ok("All tasks was successfully deleted");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteTaskFromUser(
            @NotEmpty @RequestParam String username,
            @NotEmpty @RequestParam Long taskId
    ) throws UserNotFoundException, TaskNotFoundException {
        taskService.deleteTask(username, taskId);
        return ResponseEntity.ok().body("Task " + taskId + " was deleted from " + username);
    }

    @PostMapping("/complete")
    public ResponseEntity<?> completeTask(
            @NotEmpty @RequestParam String username,
            @NotEmpty @RequestParam Long taskId
    ) throws UserNotFoundException, TaskNotFoundException {
        taskService.completeTask(username, taskId);
        return ResponseEntity.ok().body("Task " + taskId + " completed");
    }
}
