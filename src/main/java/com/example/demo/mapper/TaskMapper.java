package com.example.demo.mapper;

import com.example.demo.dto.TaskRequestDTO;
import com.example.demo.dto.TaskResponseDTO;
import com.example.demo.model.Task;

public class TaskMapper {

    static public Task  toTask(TaskRequestDTO taskRequestDTO){
        Task task = new Task();
        task.setTitle(taskRequestDTO.title());
        task.setDescription(taskRequestDTO.description());
        task.setCompleted(taskRequestDTO.completed());
        return task;
    }

    static public TaskResponseDTO toTaskResponseDTO(Task task){
        return  new TaskResponseDTO(task.getId(), task.getTitle(), task.getDescription(), task.isCompleted());
    }
}
