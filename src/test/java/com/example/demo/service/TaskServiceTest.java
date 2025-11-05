package com.example.demo.service;

import com.example.demo.dto.TaskRequestDTO;
import com.example.demo.dto.TaskResponseDTO;
import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TaskServiceTest {
    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private UserRepository userRepository;

    private User userTest;

    @Autowired
    private TaskService taskService;

    private Task createTask(Long id, String title, String description, boolean completed, User owner) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDescription(description);
        task.setCompleted(completed);
        task.setOwner(owner);
        return task;
    }


    private TaskResponseDTO createTaskResponseDTO(Long id, String title, String description, boolean completed) {

        return new TaskResponseDTO(id, title, description, completed);
    }

    @BeforeEach
    void setUpUserTaskService() {

        this.userTest = new User();
        this.userTest.setId(1L);
        this.userTest.setUsername("test");
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(this.userTest));
    }

    @WithMockUser(username = "test")
    @Test
    public void shouldCreateTaskSuccessfully(){

        TaskRequestDTO taskRequestDTO = new TaskRequestDTO("teste","testeCreateTask", true);

        Task taskTest = new Task();
            taskTest.setOwner(userTest);
            taskTest.setTitle(taskRequestDTO.title());
            taskTest.setDescription(taskRequestDTO.description());
            taskTest.setCompleted(taskRequestDTO.completed());

        Task taskTest2 = createTask(1L, taskRequestDTO.title(),  taskRequestDTO.description(), taskRequestDTO.completed(), userTest);


        TaskResponseDTO expectedResponseDTO = new TaskResponseDTO(
                taskTest2.getId(),
                taskTest2.getTitle(),
                taskTest2.getDescription(),
                taskTest2.isCompleted());



        when(taskRepository.save(any(Task.class))).thenReturn(taskTest2);

        TaskResponseDTO actualResponseDTO = taskService.createTask(taskRequestDTO);

        assertThat(actualResponseDTO).isEqualTo(expectedResponseDTO);
        Mockito.verify(userRepository, Mockito.times(1)).findByUsername("test");
        Mockito.verify(taskRepository, Mockito.times(1)).save(any(Task.class));
    }


    @WithMockUser(username = "test")
    @Test
    public void shouldReturnOnlyTasksOwnedByUser(){
       User owner = this.userTest;

        Task taskTest = createTask(1L, "test1", "teste 1", true, owner);
        Task taskTest2 = createTask(2L, "test2", "teste 2", true, owner);

        List<Task> taskList = List.of(taskTest, taskTest2);

        when(taskRepository.findByOwner(userTest)).thenReturn(taskList);

        List<TaskResponseDTO> actualList = taskService.getAllTasks();

        assertThat(actualList).hasSize(2);
        assertThat(actualList).extracting("id", "title", "description", "completed")
                .containsExactlyInAnyOrder(
                tuple(taskTest.getId(), taskTest.getTitle(), taskTest.getDescription(), taskTest.isCompleted()),
                tuple(taskTest2.getId(), taskTest2.getTitle(), taskTest2.getDescription(), taskTest2.isCompleted())

        );

        Mockito.verify(taskRepository, Mockito.times(1)).findByOwner(userTest);

    }

    @WithMockUser(username = "test")
    @Test
    public void shouldReturnTaskByIDWhenFoundAndOwnedByUser(){
        User owner = this.userTest;

        Task foundTask = createTask(1L, "teste", "foundTest", false, owner);

        when(taskRepository.findByIdAndOwner(1L, owner)).thenReturn(Optional.of(foundTask));

        TaskResponseDTO actualResponseDTO = taskService.getTaskById(1L);

        TaskResponseDTO expectedResponseDTO = createTaskResponseDTO(1L, foundTask.getTitle(), foundTask.getDescription(), foundTask.isCompleted());

        assertThat(actualResponseDTO).isEqualTo(expectedResponseDTO);

        Mockito.verify(taskRepository, Mockito.times(1)).findByIdAndOwner(1L, owner);

    }

    @WithMockUser(username = "test")
    @Test
    public void shouldThrowExceptionWhenTaskNotFound(){
        User owner = this.userTest;
        Long testId = 99L;
        when(taskRepository.findByIdAndOwner(testId, owner)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(testId)).isInstanceOf(NoSuchElementException.class);
    }

    @WithMockUser(username = "test")
    @Test
    public void shouldThrowExceptionWhenTaskBelongsToOtherUser(){
        User owner = this.userTest;
        Long testId = 2L;

        when(taskRepository.findByIdAndOwner(testId, owner)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(testId)).isInstanceOf(NoSuchElementException.class);

        verify(taskRepository, Mockito.times(1)).findByIdAndOwner(testId, owner);
    }

    @WithMockUser(username = "test")
    @Test
    public void shouldUpdateTaskSuccessfully(){

        User owner =this.userTest;
        Task existingTask = createTask(1L, "TEST", "testeUp", true, owner);

        TaskRequestDTO updateRequestDTO = new TaskRequestDTO("Teste", "testeAtualizado", true);

        Task updatedTask = createTask(1L, "Teste",  "testeAtualizado", true, owner);

        when(taskRepository.findByIdAndOwner(1L, owner)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        TaskResponseDTO expectedDTO =  createTaskResponseDTO(1L, updatedTask.getTitle(), updatedTask.getDescription(), updatedTask.isCompleted());

        TaskResponseDTO actualDTO = taskService.updateTask(updateRequestDTO, 1L);

        assertThat(actualDTO).isEqualTo(expectedDTO);

        verify(taskRepository, Mockito.times(1)).findByIdAndOwner(1L, owner);

    }

    @WithMockUser(username = "test")
    @Test
    public void shouldThrowExceptionWhenUpdatingTaskNotFound(){
        User owner =this.userTest;
        Long testId = 99L;
        TaskRequestDTO updateRequestDTO = new TaskRequestDTO("Teste", "testeAtualizado", true);
        when(taskRepository.findByIdAndOwner(99L, owner)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(updateRequestDTO, testId)).isInstanceOf(NoSuchElementException.class);
        verify(taskRepository, Mockito.times(1)).findByIdAndOwner(99L, owner);
        verify(taskRepository, Mockito.times(0)).save(any(Task.class));
    }

    @WithMockUser(username = "test")
    @Test
    public void shouldThrowExceptionWhenUpdatingTaskBelongsToOtherUser(){
        User owner =this.userTest;
        Long testId = 99L;
        TaskRequestDTO updateRequestDTO = new TaskRequestDTO("Teste", "testeAtualizado", true);
        when(taskRepository.findByIdAndOwner(99L, owner)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(updateRequestDTO, testId)).isInstanceOf(NoSuchElementException.class);
        verify(taskRepository, Mockito.times(1)).findByIdAndOwner(99L, owner);
        verify(taskRepository, Mockito.never()).save(any(Task.class));
    }

    @WithMockUser(username = "test")
    @Test
    public void shouldDeleteTaskSuccessfully(){
        User owner =this.userTest;
        Task taskToDelete = createTask(1L, "TEST", "testeDel", true, owner);

        when(taskRepository.findByIdAndOwner(1L, owner)).thenReturn(Optional.of(taskToDelete));
        doNothing().when(taskRepository).deleteById(taskToDelete.getId());

        taskService.deleteTask(taskToDelete.getId());
        verify(taskRepository, Mockito.times(1)).findByIdAndOwner(1L, owner);
        verify(taskRepository, Mockito.times(1)).deleteById(taskToDelete.getId());
    }


    @WithMockUser(username = "test")
    @Test
    public void shouldThrowExceptionWhenDeletingTaskNotFound(){
        User owner =this.userTest;
        Long testId = 99L;

        when(taskRepository.findByIdAndOwner(testId, owner)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask(testId)).isInstanceOf(NoSuchElementException.class);
        verify(taskRepository, Mockito.times(1)).findByIdAndOwner(testId, owner);
        verify(taskRepository, Mockito.never()).deleteById(anyLong());
    }

    @WithMockUser(username = "test")
    @Test
    public void shouldThrowExceptionWhenDeletingTaskOwnedByOtherUser(){
        User owner =this.userTest;
        Long testId = 99L;

        when(taskRepository.findByIdAndOwner(testId, owner)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask(testId)).isInstanceOf(NoSuchElementException.class);
        verify(taskRepository, Mockito.times(1)).findByIdAndOwner(testId, owner);
        verify(taskRepository, Mockito.never()).deleteById(anyLong());
    }
}
