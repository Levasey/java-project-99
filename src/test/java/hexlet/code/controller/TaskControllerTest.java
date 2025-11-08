package hexlet.code.controller;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.TaskDTO;
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
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    private Task testTask;

    private User testUser;

    private TaskStatus testTaskStatus;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();

        objectMapper.registerModule(new JsonNullableModule());

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
    void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        List<TaskDTO> tasks = objectMapper.readValue(body, new TypeReference<>() {});

        assertThat(tasks).hasSize(1);
        assertThatJson(body).isNotNull();
        assertThatJson(body).isArray();
    }

    @Test
    void testShow() throws Exception {
        var result = mockMvc.perform(get("/api/tasks/" + testTask.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        TaskDTO taskDTO = objectMapper.readValue(body, TaskDTO.class);

        assertThat(taskDTO.getId()).isEqualTo(testTask.getId());
        assertThat(taskDTO.getName()).isEqualTo(testTask.getName());
        assertThat(taskDTO.getDescription()).isEqualTo(testTask.getDescription());
        assertThat(taskDTO.getTaskStatusId()).isEqualTo(testTaskStatus.getId());
        assertThat(taskDTO.getAssigneeId()).isEqualTo(testUser.getId());
    }

    @Test
    void testShowNotFound() throws Exception {
        var result = mockMvc.perform(get("/api/tasks/9999"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void testCreate() throws Exception {
        var data = new HashMap<String, Object>();
        data.put("name", "New Task Name");
        data.put("description", "Test description for new task");
        data.put("taskStatusId", testTaskStatus.getId());
        data.put("assigneeId", testUser.getId());
        data.put("index", 5);

        var request = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        TaskDTO createdTaskDTO = objectMapper.readValue(body, TaskDTO.class);

        assertThat(createdTaskDTO.getName()).isEqualTo("New Task Name");
        assertThat(createdTaskDTO.getDescription()).isEqualTo("Test description for new task");
        assertThat(createdTaskDTO.getIndex()).isEqualTo(5);
        assertThat(createdTaskDTO.getTaskStatusId()).isEqualTo(testTaskStatus.getId());
        assertThat(createdTaskDTO.getAssigneeId()).isEqualTo(testUser.getId());

        // Проверяем, что задача действительно сохранена в БД
        Task createdTask = taskRepository.findById(createdTaskDTO.getId()).orElse(null);
        assertNotNull(createdTask);
        assertThat(createdTask.getName()).isEqualTo("New Task Name");
    }

    @Test
    void testCreateWithInvalidData() throws Exception {
        // Пытаемся создать задачу без обязательных полей
        var invalidData = new HashMap<String, Object>();
        invalidData.put("description", "Test description"); // нет name и taskStatusId

        var request = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidData));

        mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testUpdate() throws Exception {
        var data = new HashMap<>();
        data.put("name", "Updated name");
        data.put("description", "Updated description");

        var request = put("/api/tasks/" + testTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        var body = result.getResponse().getContentAsString();
        TaskDTO updatedTaskDTO = objectMapper.readValue(body, TaskDTO.class);

        Task updatedTask = taskRepository.findById(updatedTaskDTO.getId()).get();
        assertThat(updatedTask).isNotNull();
        assertThat(updatedTask.getDescription()).isEqualTo("Updated description");
    }

    @Test
    void testPartialUpdate() throws Exception {
        // Обновляем только имя
        var data = Map.of("name", "Partially Updated Name");

        var request = put("/api/tasks/" + testTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        TaskDTO updatedTaskDTO = objectMapper.readValue(body, TaskDTO.class);

        assertThat(updatedTaskDTO.getName()).isEqualTo("Partially Updated Name");
        // Остальные поля должны остаться без изменений
        assertThat(updatedTaskDTO.getDescription()).isEqualTo(testTask.getDescription());
        assertThat(updatedTaskDTO.getIndex()).isEqualTo(testTask.getIndex());
    }

    @Test
    void testUpdateNotFound() throws Exception {
        var data = Map.of("name", "Updated Name");

        var request = put("/api/tasks/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void testDelete() throws Exception {
        var request = delete("/api/tasks/" + testTask.getId());
        mockMvc.perform(request).andExpect(status().isNoContent());

        Task task = taskRepository.findById(testTask.getId()).orElse(null);
        assertThat(task).isNull();
    }

    @Test
    void testDeleteNotFound() throws Exception {
        mockMvc.perform(delete("/api/tasks/9999"))
                .andExpect(status().isNotFound());
    }
}
