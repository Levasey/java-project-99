package hexlet.code.controller;

import hexlet.code.dto.taskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskStatus.TaskStatusDTO;
import hexlet.code.dto.taskStatus.TaskStatusUpdateDTO;
import hexlet.code.service.TaskStatusService;
import hexlet.code.utils.UserUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusesController {
    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private UserUtils userUtils;

    @GetMapping
    public ResponseEntity<List<TaskStatusDTO>> index() {
        return ResponseEntity.ok(taskStatusService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskStatusDTO> show(@PathVariable Long id) {
        TaskStatusDTO taskStatusDTO = taskStatusService.findById(id);
        return ResponseEntity.ok(taskStatusDTO);
    }

    @PostMapping
    public ResponseEntity<TaskStatusDTO> create(@Valid @RequestBody TaskStatusCreateDTO taskStatusCreateDTO) {
        TaskStatusDTO created = taskStatusService.create(taskStatusCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskStatusDTO> update(@PathVariable Long id,
                                                @Valid @RequestBody TaskStatusUpdateDTO taskStatusUpdateDTO) {
        TaskStatusDTO updated = taskStatusService.update(id, taskStatusUpdateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskStatusService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
