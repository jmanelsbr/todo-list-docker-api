package com.example.demo.controller;


import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dto.TaskRequestDTO;
import com.example.demo.dto.TaskResponseDTO;
import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.checkerframework.checker.units.qual.A;
import org.h2.util.json.JSONString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.data.repository.init.ResourceReader.Type.JSON;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TaskService taskService;

    @BeforeEach
    public void setUpDatabase() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "teste")
    public void shouldCreateTaskWhenAuthenticated() throws Exception {
        User testUser = new User();
        testUser.setUsername("teste");
        userRepository.save(testUser);

        TaskRequestDTO testeRequestDTO = new TaskRequestDTO("testeTask", "teste", true);
        String jsonDto = objectMapper.writeValueAsString(testeRequestDTO);

        mockMvc.perform(
                post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDto)).andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.id").exists())
                .andExpect(
                        jsonPath("$.title").value(testeRequestDTO.title()))
                .andExpect(
                        jsonPath("$.description").value(testeRequestDTO.description()));

    }

    @Test
    @WithMockUser(username = "testeA")
    public void shouldReturnOnlyTasksOwned() throws Exception {
        User testUserA = new User();
        User testUserB = new User();

        testUserA.setUsername("testeA");
        testUserB.setUsername("testeB");


        userRepository.save(testUserA);
        userRepository.save(testUserB);

        Task taskA = new Task();
        taskA.setTitle("taskA");
        taskA.setDescription("taskA");
        taskA.setOwner(testUserA);
        taskRepository.save(taskA);

        Task taskB = new Task();
        taskB.setTitle("taskB");
        taskB.setDescription("taskB");
        taskB.setOwner(testUserA);
        taskRepository.save(taskB);

        Task taskUserB = new Task();
        taskUserB.setTitle("taskUserB");
        taskUserB.setDescription("taskUserB");
        taskUserB.setOwner(testUserB);
        taskRepository.save(taskUserB);

        var returnedTask = mockMvc.perform(get("/tasks")).andExpect(status().isOk());

        returnedTask
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value(taskA.getTitle()));

    }

    @Test
    @WithMockUser(username = "testeA")
    public void shouldReturnTasksByIdForOwner() throws Exception {
        User testUserA = new User();
        User testUserB = new User();

        testUserA.setUsername("testeA");
        testUserB.setUsername("testeB");


        userRepository.save(testUserA);
        userRepository.save(testUserB);

        Task taskA = new Task();
        taskA.setTitle("taskA");
        taskA.setDescription("taskA");
        taskA.setOwner(testUserA);
        Task savedTaskA = taskRepository.save(taskA);

        Task taskB = new Task();
        taskB.setTitle("taskB");
        taskB.setDescription("taskB");
        taskB.setOwner(testUserA);
        taskRepository.save(taskB);

        var returnedTask = mockMvc.perform(get("/tasks/" + savedTaskA.getId())).andExpect(status().isOk());
        returnedTask.andExpect(jsonPath("$.id").value(savedTaskA.getId()))
                .andExpect(jsonPath("$.title").value(savedTaskA.getTitle()))
                .andExpect(jsonPath("$.description").value(savedTaskA.getDescription()));

    }

    @Test
    @WithMockUser(username = "testeA")
    public void shouldReturnNotFoundWhenTaskIsOwnedByAnotherUser() throws Exception {
        User testUserA = new User();
        User testUserB = new User();

        testUserA.setUsername("testeA");
        testUserB.setUsername("testeB");


        userRepository.save(testUserA);
        userRepository.save(testUserB);



        Task taskB = new Task();
        taskB.setTitle("taskB");
        taskB.setDescription("taskB");
        taskB.setOwner(testUserB);
        Task savedTaskB = taskRepository.save(taskB);

        mockMvc.perform(get("/tasks/" + savedTaskB.getId())).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testeA")
    public void shouldUpdateTaskWhenOwned() throws Exception {
        User testeA = new User();
        testeA.setUsername("testeA");
        userRepository.save(testeA);
        Task testTaskA = new Task();

        testTaskA.setTitle("taskA");
        testTaskA.setDescription("taskA");
        testTaskA.setCompleted(false);
        testTaskA.setOwner(testeA);
        Task savedTaskA = taskRepository.save(testTaskA);
        var savedTaskAId = savedTaskA.getId();


        TaskRequestDTO updatedTask = new TaskRequestDTO("newTask", "newTask", true);
        String jsonDto = objectMapper.writeValueAsString(updatedTask);

        mockMvc.perform(put("/tasks/" + savedTaskAId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDto)).andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id").exists())
                .andExpect(
                        jsonPath("$.title").value(updatedTask.title()))
                .andExpect(
                        jsonPath("$.description").value(updatedTask.description()))
                .andExpect(
                        jsonPath("$.completed").value(updatedTask.completed()));

    }

    @Test
    @WithMockUser(username = "testeA")
    public void shouldReturnNotFoundWhenUpdatingTaskNotFound() throws Exception {
        User testeA = new User();
        testeA.setUsername("testeA");
        userRepository.save(testeA);
        Long nonExistentID = 99L;

        TaskRequestDTO updatedTask = new TaskRequestDTO("newTask", "newTask", true);
        String jsonDto = objectMapper.writeValueAsString(updatedTask);

        mockMvc.perform(put("/tasks/" + nonExistentID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDto)).andExpect(status().isNotFound());


    }

    @Test
    @WithMockUser(username = "testeA")
    public void shouldReturnNotFoundWhenUpdatingTaskOwnedByAnotherUser() throws Exception {
        User testUserA = new User();
        User testUserB = new User();

        testUserA.setUsername("testeA");
        testUserB.setUsername("testeB");


        userRepository.save(testUserA);
        userRepository.save(testUserB);



        Task taskB = new Task();
        taskB.setTitle("taskB");
        taskB.setDescription("taskB");
        taskB.setOwner(testUserB);
        Task savedTaskB = taskRepository.save(taskB);

        TaskRequestDTO updatedTaskB = new TaskRequestDTO("newTask", "newTask", true);
        String jsonDto = objectMapper.writeValueAsString(updatedTaskB);

        mockMvc.perform(put("/tasks/" + savedTaskB.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonDto)).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testeA")
    public void shouldDeleteTaskWhenOwned() throws Exception {
        User testeA = new User();
        testeA.setUsername("testeA");
        userRepository.save(testeA);
        Task testTaskA = new Task();

        testTaskA.setTitle("taskA");
        testTaskA.setDescription("taskA");
        testTaskA.setCompleted(false);
        testTaskA.setOwner(testeA);
        Task savedTaskA = taskRepository.save(testTaskA);
        var savedTaskAId = savedTaskA.getId();

        mockMvc.perform(delete("/tasks/" + savedTaskAId)).andExpect(status().isNoContent());
        assertThat(taskRepository.findById(savedTaskAId)).isEmpty();

    }

    @Test
    @WithMockUser(username = "testeA")
    public void shouldReturnNotFoundWhenDeletingTaskNotFound() throws Exception {
        User testeA = new User();
        testeA.setUsername("testeA");
        userRepository.save(testeA);
        Long nonExistentID = 99L;

        mockMvc.perform(delete("/tasks/" + nonExistentID))
                .andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser(username = "testeA")
    public void shouldReturnNotFoundWhenDeletingTaskOwnedByAnotherUser() throws Exception {
        User testUserA = new User();
        User testUserB = new User();

        testUserA.setUsername("testeA");
        testUserB.setUsername("testeB");

        userRepository.save(testUserA);
        userRepository.save(testUserB);

        Task taskB = new Task();
        taskB.setTitle("taskB");
        taskB.setDescription("taskB");
        taskB.setOwner(testUserB);
        Task savedTaskB = taskRepository.save(taskB);

        mockMvc.perform(delete("/tasks/" + savedTaskB.getId()))
                .andExpect(status().isNotFound());
    }



}



