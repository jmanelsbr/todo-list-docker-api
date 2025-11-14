package com.example.demo.service;

import com.example.demo.config.SecurityConfig;
import com.example.demo.dto.TaskRequestDTO;
import com.example.demo.dto.TaskResponseDTO;
import com.example.demo.mapper.TaskMapper;
import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    private User getLoggedUser() {
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userRepository.findByUsername(loggedUser.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }



    public TaskResponseDTO createTask( TaskRequestDTO taskRequestDTO) {

        User taskOwner = getLoggedUser();

        Task taskToSave = TaskMapper.toTask(taskRequestDTO);
        taskToSave.setOwner(taskOwner);
        Task savedTask = taskRepository.save(taskToSave);

        logger.debug("Task {} created for user: {}", savedTask.getId(), taskOwner.getUsername());

        return TaskMapper.toTaskResponseDTO(savedTask);

    }


    public List<TaskResponseDTO> getAllTasks(){

        User taskOwner = getLoggedUser();

        return taskRepository.findByOwner(taskOwner)
                .stream()
                .map(TaskMapper::toTaskResponseDTO)
                .toList();


    }


    public TaskResponseDTO getTaskById( Long id){

        User taskOwner = getLoggedUser();
        Task getTaskID = taskRepository.findByIdAndOwner(id, taskOwner)
                .orElseThrow(() ->new NoSuchElementException("Elemento não encontrado"));

        return TaskMapper.toTaskResponseDTO(getTaskID);



    }


    public TaskResponseDTO updateTask(TaskRequestDTO taskRequestDTO, Long id){

        User taskOwner = getLoggedUser();
        Task existingTask = taskRepository.findByIdAndOwner(id, taskOwner)
                .orElseThrow(() ->new NoSuchElementException("Elemento não encontrado"));

            existingTask.setTitle(taskRequestDTO.title());
            existingTask.setDescription(taskRequestDTO.description());
            existingTask.setCompleted(taskRequestDTO.completed());
            Task taskUpdate = taskRepository.save(existingTask);

        return TaskMapper.toTaskResponseDTO(taskUpdate);


    }



    public void deleteTask(Long id){
        User taskOwner = getLoggedUser();
        taskRepository.findByIdAndOwner(id, taskOwner)
                .orElseThrow(() ->new NoSuchElementException("Elemento não encontrado"));
        taskRepository.deleteById(id);
    }
}
