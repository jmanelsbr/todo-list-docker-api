package com.example.demo.repository;


import com.example.demo.model.Task;
import com.example.demo.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private TaskRepository taskRepository;


    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass");


    @DynamicPropertySource
    private static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);

        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    private User createAndPersistUser(String username) {
       User testUser = new User();
       testUser.setUsername(username);
       return testEntityManager.persistAndFlush(testUser);

    }

    private Task createAndPersistTask(String title, User owner) {
        Task testTask  = new Task();
        testTask.setTitle(title);
        testTask.setOwner(owner);
        return testEntityManager.persistAndFlush(testTask);

    }

    @Test
    public void shouldReturnTasksOnlyForGivenOwner() {
        User testUser = createAndPersistUser("testUser");
        User testUser2 = createAndPersistUser("testUser2");
        Task testTask = createAndPersistTask("testTask", testUser);
        Task testTask2 = createAndPersistTask("testTask2", testUser2);
        Task testTask3 = createAndPersistTask("testTask3", testUser);
        Task testTask4 = createAndPersistTask("testTask4", testUser2);

        List<Task> user1Tasks = taskRepository.findByOwner(testUser);
        assertThat(user1Tasks).hasSize(2);
        assertThat(user1Tasks).allMatch(task -> task.getOwner().getId().equals(testUser.getId()));

    }

    @Test
    public void shouldReturnTasksOnlyForGivenOwnerAndId() {
        User testUser = createAndPersistUser("testUser");
        User testUser2 = createAndPersistUser("testUser2");
        Task testTask = createAndPersistTask("testTask", testUser);
        Task testTask2 = createAndPersistTask("testTask2", testUser2);

        Optional<Task> user1Tasks = taskRepository.findByIdAndOwner(testTask.getId(), testUser);
        assertThat(user1Tasks).isPresent();
        assertThat(user1Tasks.get().getId()).isEqualTo(testTask.getId());
        assertThat(user1Tasks.get().getOwner()).isEqualTo(testTask.getOwner());
    }

    @Test
    public void shouldNotFindTaskByIdAndOwnerWhenOwnedByDifferentUser() {
        User testUser = createAndPersistUser("testUser");
        User testUser2 = createAndPersistUser("testUser2");
        Task testTask = createAndPersistTask("testTask", testUser);
        Task testTask2 = createAndPersistTask("testTask2", testUser2);
        Optional<Task> user1Tasks = taskRepository.findByIdAndOwner(testTask.getId(), testUser2);
        assertThat(user1Tasks).isEmpty();
    }
}
