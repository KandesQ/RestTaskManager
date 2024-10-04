package com.taskManager.RestTaskManager.service;

import com.taskManager.RestTaskManager.dto.UserRequestDto;
import com.taskManager.RestTaskManager.dto.UserResponseDto;
import com.taskManager.RestTaskManager.entity.UserEntity;
import com.taskManager.RestTaskManager.exception.NoUniqueUsernameException;
import com.taskManager.RestTaskManager.exception.UserNotFoundException;
import com.taskManager.RestTaskManager.repository.UserRepository;
import org.apache.catalina.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.transform.sax.SAXResult;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTests {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserService userService;
    // testing data
    static List<UserResponseDto> expectedUserResponseDtos;
    static  List<UserEntity> expectedUserEntities;

    // testing only no-args methods

    @BeforeAll
    public static void staticInitialize() {
        expectedUserResponseDtos = List.of(
                new UserResponseDto(1L, "user1", List.of(), LocalDateTime.of(2023, 3, 1, 0, 0)),
                new UserResponseDto(2L, "user2", List.of(), LocalDateTime.of(2023, 3, 1, 0, 0)),
                new UserResponseDto(3L, "user3", List.of(), LocalDateTime.of(2023, 3, 1, 0, 0))
        );
        expectedUserEntities = List.of(
                new UserEntity(1L, "user1", "password", LocalDateTime.of(2023, 3, 1, 0, 0), List.of()),
                new UserEntity(2L, "user2", "password", LocalDateTime.of(2023, 3, 1, 0, 0), List.of()),
                new UserEntity(3L, "user3", "password", LocalDateTime.of(2023, 3, 1, 0, 0), List.of())
        );
    }

    @Test
    public void getAllUsersTest() throws UserNotFoundException {
        Mockito.when(userRepository.findAll()).thenReturn(expectedUserEntities);

        List<UserResponseDto> actualUserResponseDto = userService.getAllUsers();

        Assertions.assertIterableEquals(expectedUserResponseDtos, actualUserResponseDto);
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

    @Test
    public void createUserTest() throws NoUniqueUsernameException {
        // initialize method args
        UserRequestDto expectedUserRequestDto = new UserRequestDto("user1", "1234567890");

        Mockito.when(userRepository.findByUsername(expectedUserRequestDto.getUsername())).thenReturn(Optional.empty());

        String expectedUserRequestName = expectedUserRequestDto.getUsername();
        UserEntity expectedUserEntity = UserEntity.dtoToEntity(expectedUserRequestDto);

        userService.createUser(expectedUserRequestDto);

        ArgumentCaptor<UserEntity> userEntityArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
        ArgumentCaptor<String> userRequestUserNameArgumentCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(userRequestUserNameArgumentCaptor.capture());
        Mockito.verify(userRepository, Mockito.times(1)).save(userEntityArgumentCaptor.capture());

        String actualUserRequestName = userRequestUserNameArgumentCaptor.getValue();
        UserEntity actualUserEntity = userEntityArgumentCaptor.getValue();

//      make strictDate for correct comparison
//      actually we exclude the date from test comparison, but in the program we can use it
//      properly for equals() and hashCode(), it ensures the code from a lack of hashCode() values
        LocalDateTime strictDate = LocalDateTime.now();

        expectedUserEntity.setRegisteredAt(strictDate);
        actualUserEntity.setRegisteredAt(strictDate);

        Assertions.assertEquals(expectedUserRequestName, actualUserRequestName);
        Assertions.assertEquals(expectedUserEntity, actualUserEntity);
    }

    @Test
    public void createUserThrowsNoUniqueUserNameExceptionTest() {
        // initialize method args
        UserRequestDto userRequestDto = new UserRequestDto("user1", "1234567890");

        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(new UserEntity()));

        Assertions.assertThrows(NoUniqueUsernameException.class, () -> userService.createUser(userRequestDto));
    }

    @Test
    public void deleteUserByUsernameTest() throws UserNotFoundException {
        UserEntity expectedUserEntity = expectedUserEntities.get(0);
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(expectedUserEntity));

        ArgumentCaptor<String> userNameArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UserEntity> userEntityArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);

        String expectedUserName = expectedUserEntity.getUsername();

        userService.deleteUserByUsername(expectedUserName);

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(userNameArgumentCaptor.capture());
        Mockito.verify(userRepository, Mockito.times(1)).delete(userEntityArgumentCaptor.capture());

        String actualUserName = userNameArgumentCaptor.getValue();
        UserEntity actualUserEntity = userEntityArgumentCaptor.getValue();

        Assertions.assertEquals(expectedUserName, actualUserName);
        Assertions.assertEquals(expectedUserEntity, actualUserEntity);
    }

    @Test
    public void deleteUserByUsernameThrowsUserNotFoundExceptionTest() {
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.deleteUserByUsername("some string"));
    }

    @Test
    public void getUserByUsernameTest() throws UserNotFoundException {
        UserEntity userEntity = expectedUserEntities.get(0);
        UserResponseDto expectedResponseDto = UserResponseDto.EntityToDto(userEntity);

        Mockito.when(userRepository.findByUsername(userEntity.getUsername())).thenReturn(Optional.of(userEntity));

        String expectedUserName = "user1";

        UserResponseDto actualResponseDto = userService.getUserByUsername(expectedUserName);

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(stringArgumentCaptor.capture());

        String actualUserName = stringArgumentCaptor.getValue();

        Assertions.assertEquals(expectedUserName, actualUserName);
        Assertions.assertEquals(expectedResponseDto, actualResponseDto);
    }

    @Test
    public void getUserByUsernameThrowsUserNotFoundExceptionTest() {
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername("some string"));
    }
}