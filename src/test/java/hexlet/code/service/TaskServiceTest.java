package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private Task testTask;
    private User testUser;
    private TaskStatus testTaskStatus;

    @BeforeEach
    void setUp() {
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);

        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(testTaskStatus);

        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(testTaskStatus);
        taskRepository.save(testTask);
    }

    @Test
    void testFindAll() {
        var tasks = taskService.findAll();
        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getName()).isEqualTo(testTask.getName());
    }

    @Test
    void testFindById() {
        var taskDTO = taskService.findById(testTask.getId());

        assertThat(taskDTO.getId()).isEqualTo(testTask.getId());
        assertThat(taskDTO.getName()).isEqualTo(testTask.getName());
        assertThat(taskDTO.getDescription()).isEqualTo(testTask.getDescription());
        assertThat(taskDTO.getTaskStatusId()).isEqualTo(testTaskStatus.getId());
        assertThat(taskDTO.getAssigneeId()).isEqualTo(testUser.getId());
    }

    @Test
    void testFindByIdNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.findById(9999L));
    }

    @Test
    void testCreate() {
        var createDTO = new TaskCreateDTO();
        createDTO.setName("New Service Task");
        createDTO.setDescription("Service test description");
        createDTO.setTaskStatusId(testTaskStatus.getId());
        createDTO.setAssigneeId(testUser.getId());
        createDTO.setIndex(7);

        var created = taskService.create(createDTO);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("New Service Task");
        assertThat(created.getDescription()).isEqualTo("Service test description");
        assertThat(created.getIndex()).isEqualTo(7);
        assertThat(created.getTaskStatusId()).isEqualTo(testTaskStatus.getId());
        assertThat(created.getAssigneeId()).isEqualTo(testUser.getId());

        // Проверяем сохранение в репозитории
        var savedTask = taskRepository.findById(created.getId());
        assertThat(savedTask).isPresent();
        assertThat(savedTask.get().getName()).isEqualTo("New Service Task");
    }

    @Test
    void testUpdate() {
        var updateDTO = new TaskUpdateDTO();
        updateDTO.setName(JsonNullable.of("Updated Service Name"));
        updateDTO.setDescription(JsonNullable.of("Updated service description"));
        updateDTO.setIndex(JsonNullable.of(15));

        var updated = taskService.update(testTask.getId(), updateDTO);

        assertThat(updated.getName()).isEqualTo("Updated Service Name");
        assertThat(updated.getDescription()).isEqualTo("Updated service description");
        assertThat(updated.getIndex()).isEqualTo(15);

        // Проверяем обновление в репозитории
        var updatedTask = taskRepository.findById(testTask.getId());
        assertThat(updatedTask).isPresent();
        assertThat(updatedTask.get().getName()).isEqualTo("Updated Service Name");
    }

    @Test
    void testPartialUpdate() {
        // Обновляем только одно поле
        var updateDTO = new TaskUpdateDTO();
        updateDTO.setName(JsonNullable.of("Partially Updated"));

        var updated = taskService.update(testTask.getId(), updateDTO);

        assertThat(updated.getName()).isEqualTo("Partially Updated");
        // Остальные поля остаются без изменений
        assertThat(updated.getDescription()).isEqualTo(testTask.getDescription());
        assertThat(updated.getIndex()).isEqualTo(testTask.getIndex());
    }

    @Test
    void testUpdateNotFound() {
        var updateDTO = new TaskUpdateDTO();
        updateDTO.setName(JsonNullable.of("New Name"));

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.update(9999L, updateDTO));
    }

    @Test
    void testDelete() {
        taskService.delete(testTask.getId());

        var deletedTask = taskRepository.findById(testTask.getId());
        assertThat(deletedTask).isEmpty();
    }

    @Test
    void testDeleteNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.delete(9999L));
    }
}