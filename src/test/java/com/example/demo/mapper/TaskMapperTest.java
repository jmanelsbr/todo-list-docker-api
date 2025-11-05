package com.example.demo.mapper;

import com.example.demo.dto.TaskRequestDTO;
import com.example.demo.dto.TaskResponseDTO;
import com.example.demo.model.Task;
import com.example.demo.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskMapperTest {

  @Test
  public void shouldMapTaskRequestDtoToTask(){
      TaskRequestDTO taskRequestDTO = new TaskRequestDTO("test", "teste", true);
      Task teste = TaskMapper.toTask(taskRequestDTO);

      assertThat(teste).isNotNull();
      assertThat(teste.getTitle()).isEqualTo(taskRequestDTO.title());
      assertThat(teste.getDescription()).isEqualTo(taskRequestDTO.description());
      assertThat(teste.isCompleted()).isEqualTo(taskRequestDTO.completed());
      }


    @Test
    public void shouldMapTaskToTaskResponseDto(){
      Task teste = new  Task();
      teste.setId(1L);
      teste.setTitle("test");
      teste.setDescription("teste");
      teste.setCompleted(true);

      TaskResponseDTO taskResponseDTO = TaskMapper.toTaskResponseDTO(teste);
      assertThat(taskResponseDTO).isNotNull();
      assertThat(taskResponseDTO.id()).isEqualTo(teste.getId());
      assertThat(taskResponseDTO.title()).isEqualTo(teste.getTitle());
      assertThat(taskResponseDTO.description()).isEqualTo(teste.getDescription());
      assertThat(taskResponseDTO.completed()).isEqualTo(teste.isCompleted());
    }
  }



