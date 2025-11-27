package hexlet.code.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.mapper.TaskMapper;
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
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
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
    private TaskMapper taskMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private Task testTask;

    private User testUser;

    private TaskStatus testTaskStatus;

    private Label testLabel;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
        labelRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();

        objectMapper.registerModule(new JsonNullableModule());

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);

        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(testTaskStatus);

        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);

        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(testTaskStatus);
        testTask.getLabels().add(testLabel);
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
        taskRepository.save(testTask);
    }

    @Test
    void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/tasks").with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        var taskDTOs = objectMapper.readValue(body, new TypeReference<List<TaskDTO>>() {});

        var expected = taskRepository.findAll();
        var actual = taskDTOs.stream().map(taskMapper::map).toList();

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void testShow() throws Exception {
        var result = mockMvc.perform(get("/api/tasks/" + testTask.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        TaskDTO taskDTO = objectMapper.readValue(body, TaskDTO.class);

        assertThat(taskDTO.getId()).isEqualTo(testTask.getId());
        assertThat(taskDTO.getTitle()).isEqualTo(testTask.getName());
        assertThat(taskDTO.getContent()).isEqualTo(testTask.getDescription());
        assertThat(taskDTO.getTaskStatusId()).isEqualTo(testTaskStatus.getId());
        assertThat(taskDTO.getAssigneeId()).isEqualTo(testUser.getId());
    }

    @Test
    void testShowNotFound() throws Exception {
        var result = mockMvc.perform(get("/api/tasks/9999").with(jwt()))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void testCreate() throws Exception {
        var data = new HashMap<String, Object>();
        data.put("title", "New Task title");
        data.put("content", "Test content for new task");
        data.put("status", testTaskStatus.getSlug());
        data.put("assigneeId", testUser.getId());
        data.put("index", 5);

        var request = post("/api/tasks").with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        TaskDTO createdTaskDTO = objectMapper.readValue(body, TaskDTO.class);

        assertThat(createdTaskDTO.getTitle()).isEqualTo("New Task title");
        assertThat(createdTaskDTO.getContent()).isEqualTo("Test content for new task");
        assertThat(createdTaskDTO.getIndex()).isEqualTo(5);
        assertThat(createdTaskDTO.getTaskStatusId()).isEqualTo(testTaskStatus.getId());
        assertThat(createdTaskDTO.getAssigneeId()).isEqualTo(testUser.getId());

        // Проверяем, что задача действительно сохранена в БД
        Task createdTask = taskRepository.findById(createdTaskDTO.getId()).orElse(null);
        assertNotNull(createdTask);
        assertThat(createdTask.getName()).isEqualTo("New Task title");
    }

    @Test
    void testCreateWithInvalidData() throws Exception {
        // Пытаемся создать задачу без обязательных полей
        var invalidData = new HashMap<String, Object>();
        invalidData.put("description", "Test description"); // нет name и taskStatusId

        var request = post("/api/tasks").with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidData));

        mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testUpdate() throws Exception {
        var data = new HashMap<>();
        data.put("title", "Updated name");
        data.put("content", "Updated content");

        var request = put("/api/tasks/" + testTask.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        var body = result.getResponse().getContentAsString();
        TaskDTO updatedTaskDTO = objectMapper.readValue(body, TaskDTO.class);

        Task updatedTask = taskRepository.findById(updatedTaskDTO.getId()).get();
        assertThat(updatedTask).isNotNull();
        assertThat(updatedTask.getDescription()).isEqualTo("Updated content");
    }

    @Test
    void testPartialUpdate() throws Exception {
        // Обновляем только имя
        var data = Map.of("title", "Partially Updated title");

        var request = put("/api/tasks/" + testTask.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        TaskDTO updatedTaskDTO = objectMapper.readValue(body, TaskDTO.class);

        assertThat(updatedTaskDTO.getTitle()).isEqualTo("Partially Updated title");
        // Остальные поля должны остаться без изменений
        assertThat(updatedTaskDTO.getContent()).isEqualTo(testTask.getDescription());
        assertThat(updatedTaskDTO.getIndex()).isEqualTo(testTask.getIndex());
    }

    @Test
    void testUpdateNotFound() throws Exception {
        var data = Map.of("title", "Updated title");

        var request = put("/api/tasks/9999").with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void testDelete() throws Exception {
        var request = delete("/api/tasks/" + testTask.getId()).with(jwt());
        mockMvc.perform(request).andExpect(status().isNoContent());

        Task task = taskRepository.findById(testTask.getId()).orElse(null);
        assertThat(task).isNull();
    }

    @Test
    void testDeleteNotFound() throws Exception {
        mockMvc.perform(delete("/api/tasks/9999").with(jwt()))
                .andExpect(status().isNotFound());
    }
}
