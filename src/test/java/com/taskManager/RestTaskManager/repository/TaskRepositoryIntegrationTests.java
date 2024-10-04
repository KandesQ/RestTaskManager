package com.taskManager.RestTaskManager.repository;


import com.taskManager.RestTaskManager.entity.TaskEntity;
import com.taskManager.RestTaskManager.entity.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.scheduling.config.Task;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class TaskRepositoryIntegrationTests {

    @Autowired
    TaskRepository taskRepository;

    TaskEntity expectedTask;

    // make tests: CREATE READ UPDATE (SAVE) DELETE

    @BeforeEach
    public void initialize() {
        expectedTask = new TaskEntity();

        expectedTask.setTitle("task");
        expectedTask.setDescription("description");
        expectedTask.setIsDone(false);
    }

    @Test
    public void saveTaskTest() {
        // this method orders jpa (hibernate) to make a unique id for saving entity
        taskRepository.save(expectedTask);

        Optional<TaskEntity> actualTask = taskRepository.findById(expectedTask.getId());

        assertThat(actualTask.isPresent()).isTrue();
        assertThat(actualTask.get()).isEqualTo(expectedTask);
    }

    @Test
    public void updateTaskTest() {
        taskRepository.save(expectedTask);

        expectedTask.setDescription("other description");
        taskRepository.save(expectedTask);

        Optional<TaskEntity> actualTask = taskRepository.findById(expectedTask.getId());

        assertThat(actualTask.isPresent()).isTrue();

        assertThat(actualTask.get()).isEqualTo(expectedTask);
        Assertions.assertEquals(1, taskRepository.count());
    }

    @Test
    public void deleteTaskByIdTest() {
        taskRepository.save(expectedTask);

        taskRepository.deleteById(expectedTask.getId());

        Assertions.assertEquals(0, taskRepository.count());

        Optional<TaskEntity> emptyTask = taskRepository.findById(expectedTask.getId());

        assertThat(emptyTask.isEmpty()).isTrue();
    }

    @Test
    public void findTaskByIdTest() {
        taskRepository.save(expectedTask);

        Optional<TaskEntity> actualTask = taskRepository.findById(expectedTask.getId());

        assertThat(actualTask.isPresent()).isTrue();
        assertThat(actualTask.get()).isEqualTo(expectedTask);
    }

    @Test
    public void findAllTasksByTitleTest() {
        TaskEntity secondExpectedTask = new TaskEntity();

        secondExpectedTask.setTitle(expectedTask.getTitle());
        secondExpectedTask.setDescription(expectedTask.getDescription());

        taskRepository.save(expectedTask);
        taskRepository.save(secondExpectedTask);

        List<TaskEntity> expectedTaskList = List.of(expectedTask, secondExpectedTask);

        List<TaskEntity> actualTaskList = taskRepository.findAllByTitle(expectedTask.getTitle());

        Assertions.assertEquals(expectedTaskList.size(), actualTaskList.size()); // redundant but okay
        assertThat(actualTaskList).isEqualTo(expectedTaskList);
    }

    // Maybe make return Optional<List<?>> or remain List<?>?
    // Don't see situations where it can be null
    @Test
    public void findNotExistingTasksTest() {
        List<TaskEntity> actualTaskList = taskRepository.findAllByTitle(expectedTask.getTitle());
        assertThat(actualTaskList).isEmpty();
    }
}
