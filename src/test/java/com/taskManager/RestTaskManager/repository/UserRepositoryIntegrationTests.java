package com.taskManager.RestTaskManager.repository;


import com.taskManager.RestTaskManager.entity.UserEntity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test") // set application-test.properties as active profile of the integrations tests
public class UserRepositoryIntegrationTests {

    @Autowired
    private UserRepository userRepository;

    UserEntity expectedUser;

    // make tests: CREATE READ UPDATE (SAVE) DELETE

    @BeforeEach
    public void initialize() {
        expectedUser = new UserEntity();

        expectedUser.setUsername("John");
        expectedUser.setPassword("password");
    }

    @Test
    public void saveUserTest() {
        userRepository.save(expectedUser);

        Optional<UserEntity> actualFoundedByIdUser = userRepository.findById(expectedUser.getId());

        assertThat(userRepository.findAll()).isNotEmpty();

        // check that saved user is equal to foundedUser
        assertThat(actualFoundedByIdUser.isPresent()).isTrue();
        assertThat(expectedUser).isEqualTo(actualFoundedByIdUser.get());
    }

    @Test
    public void updateUserTest() {
        userRepository.save(expectedUser);

        expectedUser.setUsername("Joe");

        // update user
        userRepository.save(expectedUser);

        Optional<UserEntity> actualUpdatedUser = userRepository.findById(expectedUser.getId());

        assertThat(actualUpdatedUser.isPresent()).isTrue();

        assertThat(actualUpdatedUser.get()).isEqualTo(expectedUser);

        // check that JPA has not added second entity (maybe, it could have happened)
        Assertions.assertEquals(1, userRepository.count());

    }

    @Test
    public void findUserByIdTest() {
        UserEntity expectedUser1 = new UserEntity();
        UserEntity expectedUser2 = new UserEntity();

        expectedUser1.setUsername("John");
        expectedUser1.setPassword("password");
        expectedUser2.setUsername("Joe");
        expectedUser2.setPassword("somepass");

        userRepository.save(expectedUser1);
        userRepository.save(expectedUser2);

        Optional<UserEntity> actualUser1 = userRepository.findById(expectedUser1.getId());
        Optional<UserEntity> actualUser2 = userRepository.findById(expectedUser2.getId());

        assertThat(actualUser1.isPresent()).isTrue();
        assertThat(actualUser2.isPresent()).isTrue();

        assertThat(actualUser1.get()).isEqualTo(expectedUser1);
        assertThat(actualUser2.get()).isEqualTo(expectedUser2);

    }

    @Test
    public void deleteUserByIdTest() {
        userRepository.save(expectedUser);

        userRepository.deleteById(expectedUser.getId());

        Optional<UserEntity> emptyUser = userRepository.findById(expectedUser.getId());

        assertThat(userRepository.findAll()).isEmpty();
        assertThat(emptyUser.isPresent()).isFalse();

    }

    @Test
    public void findByUserNameTest() {
        userRepository.save(expectedUser);

        Optional<UserEntity> actualUser = userRepository.findByUsername(expectedUser.getUsername());

        assertThat(actualUser.isPresent()).isTrue();

        assertThat(actualUser.get()).isEqualTo(expectedUser);
    }

    // just for sure. Must return nullable optional
    @Test
    public void findNotExistingUserByUserNameTest() {
        Optional<UserEntity> actutalUser = userRepository.findByUsername(expectedUser.getUsername());

        assertThat(actutalUser.isPresent()).isFalse();
    }
}
