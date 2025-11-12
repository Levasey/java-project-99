package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
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
import hexlet.code.dto.task.TaskParamsDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    private LabelRepository labelRepository;

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
    void testFindAllWithFilters() {
        // Создаем дополнительные тестовые данные
        User anotherUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(anotherUser);

        TaskStatus anotherStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(anotherStatus);

        Label testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);

        Task taskWithLabel = Instancio.of(modelGenerator.getTaskModel()).create();
        taskWithLabel.setName("Special task with label");
        taskWithLabel.setAssignee(anotherUser);
        taskWithLabel.setTaskStatus(anotherStatus);
        taskWithLabel.getLabels().add(testLabel);
        taskRepository.save(taskWithLabel);

        // Тест фильтрации по названию
        TaskParamsDTO titleParams = new TaskParamsDTO();
        titleParams.setTitleCont("Special");
        Pageable pageable = PageRequest.of(0, 10);

        var filteredByTitle = taskService.findAll(titleParams, pageable);
        assertThat(filteredByTitle.getContent()).hasSize(1);
        assertThat(filteredByTitle.getContent().get(0).getName()).isEqualTo("Special task with label");

        // Тест фильтрации по исполнителю
        TaskParamsDTO assigneeParams = new TaskParamsDTO();
        assigneeParams.setAssigneeId(anotherUser.getId());

        var filteredByAssignee = taskService.findAll(assigneeParams, pageable);
        assertThat(filteredByAssignee.getContent()).hasSize(1);
        assertThat(filteredByAssignee.getContent().get(0).getAssigneeId()).isEqualTo(anotherUser.getId());

        // Тест фильтрации по статусу
        TaskParamsDTO statusParams = new TaskParamsDTO();
        statusParams.setStatus(anotherStatus.getSlug());

        var filteredByStatus = taskService.findAll(statusParams, pageable);
        assertThat(filteredByStatus.getContent()).hasSize(1);
        assertThat(filteredByStatus.getContent().get(0).getTaskStatusId()).isEqualTo(anotherStatus.getId());

        // Тест фильтрации по метке
        TaskParamsDTO labelParams = new TaskParamsDTO();
        labelParams.setLabelId(testLabel.getId());

        var filteredByLabel = taskService.findAll(labelParams, pageable);
        assertThat(filteredByLabel.getContent()).hasSize(1);
        assertThat(filteredByLabel.getContent().get(0).getId()).isEqualTo(taskWithLabel.getId());

        // Тест комбинированной фильтрации
        TaskParamsDTO combinedParams = new TaskParamsDTO();
        combinedParams.setTitleCont("Special");
        combinedParams.setAssigneeId(anotherUser.getId());

        var filteredCombined = taskService.findAll(combinedParams, pageable);
        assertThat(filteredCombined.getContent()).hasSize(1);
    }

    @Test
    void testFindAllWithoutFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        TaskParamsDTO emptyParams = new TaskParamsDTO();

        var allTasks = taskService.findAll(emptyParams, pageable);
        assertThat(allTasks.getContent()).hasSize(1); // Только одна задача из setUp
    }

    @Test
    void testFindAllWithNonExistingFilters() {
        Pageable pageable = PageRequest.of(0, 10);

        TaskParamsDTO params = new TaskParamsDTO();
        params.setAssigneeId(9999L); // Несуществующий пользователь

        var result = taskService.findAll(params, pageable);
        assertThat(result.getContent()).isEmpty();
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