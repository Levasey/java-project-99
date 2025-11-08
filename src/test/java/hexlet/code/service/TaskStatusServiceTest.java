package hexlet.code.service;

import hexlet.code.dto.taskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskStatus.TaskStatusDTO;
import hexlet.code.dto.taskStatus.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class TaskStatusServiceTest {

    @Mock
    private TaskStatusRepository taskStatusRepository;

    @Mock
    private TaskStatusMapper taskStatusMapper;

    @InjectMocks
    private TaskStatusService taskStatusService;

    @Test
    void findAll_ShouldReturnAllTaskStatuses() {
        // Arrange
        TaskStatus taskStatus1 = createTaskStatus(1L, "To Do", "to-do");
        TaskStatus taskStatus2 = createTaskStatus(2L, "In Progress", "in-progress");
        List<TaskStatus> taskStatuses = List.of(taskStatus1, taskStatus2);

        TaskStatusDTO dto1 = createTaskStatusDTO(1L, "To Do", "to-do");
        TaskStatusDTO dto2 = createTaskStatusDTO(2L, "In Progress", "in-progress");

        when(taskStatusRepository.findAll()).thenReturn(taskStatuses);
        when(taskStatusMapper.map(taskStatus1)).thenReturn(dto1);
        when(taskStatusMapper.map(taskStatus2)).thenReturn(dto2);

        // Act
        List<TaskStatusDTO> result = taskStatusService.findAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("To Do");
        assertThat(result.get(1).getName()).isEqualTo("In Progress");
        verify(taskStatusRepository).findAll();
    }

    @Test
    void findById_WhenTaskStatusExists_ShouldReturnTaskStatus() {
        // Arrange
        Long taskStatusId = 1L;
        TaskStatus taskStatus = createTaskStatus(taskStatusId, "To Do", "to-do");
        TaskStatusDTO expectedDTO = createTaskStatusDTO(taskStatusId, "To Do", "to-do");

        when(taskStatusRepository.findById(taskStatusId)).thenReturn(Optional.of(taskStatus));
        when(taskStatusMapper.map(taskStatus)).thenReturn(expectedDTO);

        // Act
        TaskStatusDTO result = taskStatusService.findById(taskStatusId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(taskStatusId);
        assertThat(result.getName()).isEqualTo("To Do");
        assertThat(result.getSlug()).isEqualTo("to-do");
        verify(taskStatusRepository).findById(taskStatusId);
    }

    @Test
    void findById_WhenTaskStatusNotExists_ShouldThrowException() {
        // Arrange
        Long taskStatusId = 999L;
        when(taskStatusRepository.findById(taskStatusId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskStatusService.findById(taskStatusId)
        );

        assertThat(exception.getMessage()).isEqualTo("Task status not found with id: " + taskStatusId);
        verify(taskStatusRepository).findById(taskStatusId);
        verify(taskStatusMapper, never()).map(any(TaskStatus.class));
    }

    @Test
    void create_ShouldSaveAndReturnTaskStatus() {
        // Arrange
        TaskStatusCreateDTO createDTO = new TaskStatusCreateDTO();
        createDTO.setName("Done");
        createDTO.setSlug("done");

        TaskStatus taskStatus = createTaskStatus(null, "Done", "done");
        TaskStatus savedTaskStatus = createTaskStatus(1L, "Done", "done");
        TaskStatusDTO expectedDTO = createTaskStatusDTO(1L, "Done", "done");

        when(taskStatusMapper.map(createDTO)).thenReturn(taskStatus);
        when(taskStatusRepository.save(taskStatus)).thenReturn(savedTaskStatus);
        when(taskStatusMapper.map(savedTaskStatus)).thenReturn(expectedDTO);

        // Act
        TaskStatusDTO result = taskStatusService.create(createDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Done");
        verify(taskStatusRepository).save(taskStatus);
    }

    @Test
    void update_WhenTaskStatusExists_ShouldUpdateAndReturnTaskStatus() {
        // Arrange
        Long taskStatusId = 1L;
        TaskStatusUpdateDTO updateDTO = new TaskStatusUpdateDTO();
        updateDTO.setName(JsonNullable.of("In Review"));
        updateDTO.setSlug(JsonNullable.of("in-review"));

        TaskStatus existingTaskStatus = createTaskStatus(taskStatusId, "In Progress", "in-progress");
        TaskStatus updatedTaskStatus = createTaskStatus(taskStatusId, "In Review", "in-review");
        TaskStatusDTO expectedDTO = createTaskStatusDTO(taskStatusId, "In Review", "in-review");

        when(taskStatusRepository.findById(taskStatusId)).thenReturn(Optional.of(existingTaskStatus));
        when(taskStatusRepository.save(existingTaskStatus)).thenReturn(updatedTaskStatus);
        when(taskStatusMapper.map(updatedTaskStatus)).thenReturn(expectedDTO);

        // Act
        TaskStatusDTO result = taskStatusService.update(taskStatusId, updateDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("In Review");
        assertThat(result.getSlug()).isEqualTo("in-review");
        verify(taskStatusMapper).update(updateDTO, existingTaskStatus);
        verify(taskStatusRepository).save(existingTaskStatus);
    }

    @Test
    void update_WhenTaskStatusNotExists_ShouldThrowException() {
        // Arrange
        Long taskStatusId = 999L;
        TaskStatusUpdateDTO updateDTO = new TaskStatusUpdateDTO();
        updateDTO.setName(JsonNullable.of("In Review"));

        when(taskStatusRepository.findById(taskStatusId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskStatusService.update(taskStatusId, updateDTO)
        );

        assertThat(exception.getMessage()).isEqualTo("Task status not found with id: " + taskStatusId);
        verify(taskStatusRepository, never()).save(any(TaskStatus.class));
        verify(taskStatusMapper, never()).update(any(), any());
    }

    @Test
    void delete_WhenTaskStatusExists_ShouldDeleteTaskStatus() {
        // Arrange
        Long taskStatusId = 1L;
        when(taskStatusRepository.existsById(taskStatusId)).thenReturn(true);

        // Act
        taskStatusService.delete(taskStatusId);

        // Assert
        verify(taskStatusRepository).existsById(taskStatusId);
        verify(taskStatusRepository).deleteById(taskStatusId);
    }

    @Test
    void delete_WhenTaskStatusNotExists_ShouldThrowException() {
        // Arrange
        Long taskStatusId = 999L;
        when(taskStatusRepository.existsById(taskStatusId)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskStatusService.delete(taskStatusId)
        );

        assertThat(exception.getMessage()).isEqualTo("Task status not found with id: " + taskStatusId);
        verify(taskStatusRepository, never()).deleteById(taskStatusId);
    }

    private TaskStatus createTaskStatus(Long id, String name, String slug) {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setId(id);
        taskStatus.setName(name);
        taskStatus.setSlug(slug);
        taskStatus.setCreatedAt(LocalDateTime.now());
        return taskStatus;
    }

    private TaskStatusDTO createTaskStatusDTO(Long id, String name, String slug) {
        TaskStatusDTO dto = new TaskStatusDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setSlug(slug);
        return dto;
    }
}