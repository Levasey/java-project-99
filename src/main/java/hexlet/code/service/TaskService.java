package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.dto.task.TaskUpdateDTO;

import java.util.List;

public interface TaskService {
    List<TaskDTO> findAll(TaskParamsDTO taskParamsDTO);

    TaskDTO findById(Long id);

    TaskDTO create(TaskCreateDTO taskCreateDTO);

    TaskDTO update(Long id, TaskUpdateDTO taskUpdateDTO);

    void delete(Long id);
}
