package com.taskManager.RestTaskManager.controller;

import com.taskManager.RestTaskManager.dto.UserRequestDto;
import com.taskManager.RestTaskManager.entity.UserEntity;
import com.taskManager.RestTaskManager.exception.NoUniqueUsernameException;
import com.taskManager.RestTaskManager.exception.UserNotFoundException;
import com.taskManager.RestTaskManager.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getUsers() throws UserNotFoundException {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(
            @PathVariable @NotBlank(message = "username must be not blank") String username
    ) throws UserNotFoundException {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequestDto userRequestDto) throws NoUniqueUsernameException {
        userService.createUser(userRequestDto);
        return ResponseEntity.ok("User " + userRequestDto.getUsername() + " was successfully registered!");
    }

    // as the names are identical it processes users by name
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(
            @RequestParam @NotBlank(message = "must be not blank") String username
    ) throws UserNotFoundException {
        userService.deleteUserByUsername(username);
        return ResponseEntity.ok("User " + username + " was deleted");
    }

    @DeleteMapping("/delete/all")
    public ResponseEntity<?> deleteAllUsers() throws UserNotFoundException {
        userService.deleteAllUsers();
        return ResponseEntity.ok("All users was successfully deleted");
    }
}
