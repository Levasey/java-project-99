package hexlet.code.service;

import hexlet.code.dto.taskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskStatus.TaskStatusDTO;
import hexlet.code.dto.taskStatus.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskStatusService {
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    @Transactional(readOnly = true)
    public List<TaskStatusDTO> findAll() {
        return taskStatusRepository.findAll()
                .stream()
                .map(taskStatusMapper::map)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskStatusDTO findById(Long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found with id: " + id));
        return taskStatusMapper.map(taskStatus);
    }

    public TaskStatusDTO create(TaskStatusCreateDTO taskStatusCreateDTO) {
        TaskStatus taskStatus = taskStatusMapper.map(taskStatusCreateDTO);
        TaskStatus saved = taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(saved);
    }

    public TaskStatusDTO update(Long id, TaskStatusUpdateDTO taskStatusUpdateDTO) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found with id: " + id));
        taskStatusMapper.update(taskStatusUpdateDTO, taskStatus);
        TaskStatus updated = taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(updated);
    }

    public void delete(Long id) {
        if (!taskStatusRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task status not found with id: " + id);
        }
        taskStatusRepository.deleteById(id);
    }
}
