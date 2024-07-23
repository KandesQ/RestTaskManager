package com.taskManager.RestTaskManager.service;

import com.taskManager.RestTaskManager.dto.UserRequestDto;
import com.taskManager.RestTaskManager.entity.UserEntity;
import com.taskManager.RestTaskManager.exception.NoUniqueUsernameException;
import com.taskManager.RestTaskManager.exception.UserNotFoundException;
import com.taskManager.RestTaskManager.dto.UserResponseDto;
import com.taskManager.RestTaskManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserResponseDto> getAllUsers() throws UserNotFoundException {
        List<UserResponseDto> users = new ArrayList<>();
        userRepository.findAll().forEach(userEntity ->
            users.add(UserResponseDto.EntityToDto(userEntity)));
        if (users.isEmpty()) {
            throw new UserNotFoundException("There's no users in database");
        }
        return users;
    }

    public void deleteAllUsers() throws UserNotFoundException {
        if (userRepository.count() == 0) {
            throw new UserNotFoundException("There are no users in database");
        }
        userRepository.deleteAll();
    }

    public void createUser(UserRequestDto userRequestDto) throws NoUniqueUsernameException {
        if (userRepository.findByUsername(userRequestDto.getUsername()) != null) {
            throw new NoUniqueUsernameException("The username must be unique. User " + userRequestDto.getUsername() + " already exist");
        }
        userRepository.save(UserEntity.dtoToEntity(userRequestDto));
    }

    public void deleteUserByUsername(String username) throws UserNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            throw new UserNotFoundException("User " + username + " doesn't exist");
        }
        userRepository.delete(userEntity);
    }

    public UserResponseDto getUserByUsername(String username) throws UserNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username);
        // DRY повторяющийся код. Посмотреть про Optional и сделать с ним
        if (userEntity == null) {
            throw new UserNotFoundException("User " + username + " doesn't exist");
        }
        return UserResponseDto.EntityToDto(userEntity);
    }
}
