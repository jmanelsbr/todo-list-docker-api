package com.example.demo.controller;

import com.example.demo.dto.TaskRequestDTO;
import com.example.demo.dto.TaskResponseDTO;
import com.example.demo.mapper.TaskMapper;
import com.example.demo.model.Task;
import com.example.demo.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.example.demo.repository.TaskRepository;

import java.util.List;
import java.util.stream.Stream;


@RestController
@RequestMapping("/tasks")
@Tag(name = "Tasks", description = "Endpoints para Gerenciamento de Tarefas")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Cria uma nova tarefa", description = "Cria uma tarefa associada ao usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida (ex: título em branco)"),
            @ApiResponse(responseCode = "403", description = "Usuário não autenticado")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDTO createTask(@RequestBody @Valid TaskRequestDTO dto) {
        return taskService.createTask(dto);
    }

    @Operation (summary = "Lista todas as tarefas do usuário", description = "Retorna uma lista de todas as tarefas pertencentes ao usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tarefas retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Usuário não autenticado")
    })
    @GetMapping
    public List<TaskResponseDTO> getAllTasks(){
        return taskService.getAllTasks();
    }

    @Operation(summary = "Busca uma tarefa por ID", description = "Retorna uma tarefa específica associada ao usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa encontrada"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada ou não pertence ao usuário"),
            @ApiResponse(responseCode = "403", description = "Usuário não autenticado")
    })
    @GetMapping("/{id}")
    public TaskResponseDTO getTaskById(@PathVariable Long id){
        return taskService.getTaskById(id);
    }

    @Operation(summary = "Atualiza uma tarefa por ID", description = "Atualiza uma tarefa existente associada ao usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida (ex: título em branco)"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada ou não pertence ao usuário"),
            @ApiResponse(responseCode = "403", description = "Usuário não autenticado")
    })
    @PutMapping("/{id}")
    public TaskResponseDTO updateTask(@RequestBody @Valid TaskRequestDTO dto, @PathVariable Long id){
        return taskService.updateTask(dto, id);
    }

    @Operation(summary = "Deleta uma tarefa por ID", description = "Deleta uma tarefa existente, desde que ela pertença ao usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefa deletada com sucesso (Sem conteúdo)"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada ou não pertence ao usuário"),
            @ApiResponse(responseCode = "403", description = "Usuário não autenticado")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
    }
}
