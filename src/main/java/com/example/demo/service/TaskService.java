package com.example.demo.service;

import com.example.demo.dto.TaskRequestDTO;
import com.example.demo.dto.TaskResponseDTO;
import com.example.demo.mapper.TaskMapper;
import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    public TaskResponseDTO createTask( TaskRequestDTO taskRequestDTO) {


        Task taskToSave = TaskMapper.toTask(taskRequestDTO);
        Task savedTask = taskRepository.save(taskToSave);

        return TaskMapper.toTaskResponseDTO(savedTask);

    }


    public List<TaskResponseDTO> getAllTasks(){

        return taskRepository.findAll()
                .stream()
                .map(TaskMapper::toTaskResponseDTO)
                .toList();


    }


    public TaskResponseDTO getTaskById( Long id){
        Task getTaskID = taskRepository.findById(id).orElseThrow();

        return TaskMapper.toTaskResponseDTO(getTaskID);
    }


    public TaskResponseDTO updateTask(TaskRequestDTO taskRequestDTO, Long id){
        Task existingTask = taskRepository.findById(id).orElseThrow();

        existingTask.setTitle(taskRequestDTO.title());
        existingTask.setDescription(taskRequestDTO.description());
        existingTask.setCompleted(taskRequestDTO.completed());
        Task taskUpdate = taskRepository.save(existingTask);
        return TaskMapper.toTaskResponseDTO(taskUpdate);


    }



    public void deleteTask(Long id){
        taskRepository.findById(id).orElseThrow();
        taskRepository.deleteById(id);
    }
}
