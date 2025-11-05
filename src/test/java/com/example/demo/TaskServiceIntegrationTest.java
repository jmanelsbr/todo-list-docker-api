package com.example.demo;

import com.example.demo.dto.TaskRequestDTO;
import com.example.demo.dto.TaskResponseDTO;
import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class TaskServiceIntegrationTest {
    @Autowired
    TaskService taskService;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    UserRepository userRepository;
    @BeforeEach
    public void setUpDatabase() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "teste")
    public void shouldCreateAndRetrieveTaskById() {

        User testUser = new User();
        testUser.setUsername("teste");
        userRepository.save(testUser);

        TaskRequestDTO testeRequestDTO = new TaskRequestDTO("testeTask", "teste", true);
        TaskResponseDTO testeResponseDTO = taskService.createTask(testeRequestDTO);

        TaskResponseDTO retrievedResponseDto = taskService.getTaskById(testeResponseDTO.id());
        assertThat(retrievedResponseDto).isNotNull();
        assertThat(retrievedResponseDto.title()).isEqualTo(testeRequestDTO.title());
        assertThat(retrievedResponseDto.description()).isEqualTo(testeRequestDTO.description());
    }
}
