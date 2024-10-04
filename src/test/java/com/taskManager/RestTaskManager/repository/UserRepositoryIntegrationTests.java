package com.taskManager.RestTaskManager.repository;


import com.taskManager.RestTaskManager.entity.UserEntity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
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

    // make tests: CREATE READ UPDATE (SAVE) DELETE

    @Test
    public void saveUserTest() {
        UserEntity expectedUser = new UserEntity();

        expectedUser.setUsername("John");
        expectedUser.setPassword("password");

        userRepository.save(expectedUser);

        Optional<UserEntity> actualFoundedByIdUser = userRepository.findById(expectedUser.getId());

        assertThat(userRepository.findAll()).isNotEmpty();

        // check that saved user is equal to foundedUser
        assertThat(actualFoundedByIdUser.isPresent()).isTrue();
        assertThat(expectedUser).isEqualTo(actualFoundedByIdUser.get());
    }

    @Test
    public void updateUserTest() {
        UserEntity expectedUser = new UserEntity();

        expectedUser.setUsername("John");
        expectedUser.setPassword("password");

        userRepository.save(expectedUser);

        expectedUser.setUsername("Joe");

        // update user
        userRepository.save(expectedUser);

        Optional<UserEntity> actualUpdatedUser = userRepository.findById(expectedUser.getId());

        assertThat(actualUpdatedUser.isPresent()).isTrue();

        assertThat(actualUpdatedUser.get()).isEqualTo(expectedUser);

        // check that JPA has not added second entity (maybe, it could have happened :) )
        Assertions.assertEquals(1, userRepository.count());

    }

    @Test
    public void findByIdUserTest() {
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
    public void deleteByIdUserTest() {
        // maybe I can paste it in @BeforeEach method?
        UserEntity expectedDeletedUser = new UserEntity();

        expectedDeletedUser.setUsername("John");
        expectedDeletedUser.setPassword("password");

        userRepository.save(expectedDeletedUser);

        userRepository.deleteById(expectedDeletedUser.getId());

        Optional<UserEntity> emptyUser = userRepository.findById(expectedDeletedUser.getId());

        assertThat(userRepository.findAll()).isEmpty();
        assertThat(emptyUser.isPresent()).isFalse();

    }

    @Test
    public void findByUserNameTest() {
        UserEntity expectedUser = new UserEntity();

        expectedUser.setUsername("John");
        expectedUser.setPassword("password");

        userRepository.save(expectedUser);

        Optional<UserEntity> actualUser = userRepository.findByUsername(expectedUser.getUsername());

        assertThat(actualUser.isPresent()).isTrue();

        assertThat(actualUser.get()).isEqualTo(expectedUser);
    }
}
