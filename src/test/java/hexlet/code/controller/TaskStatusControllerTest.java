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
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskStatusControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private TaskStatus testTaskStatus;

    private User testUser;

    @BeforeEach
    void setUp() {
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();

        objectMapper.registerModule(new JsonNullableModule());

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);

        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        testTaskStatus.setAuthor(testUser);
        taskStatusRepository.save(testTaskStatus);

    }

    @Test
    void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/task_statuses"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        List<TaskStatusDTO> taskStatusDTOS = objectMapper.readValue(body, new TypeReference<>() {
        });

        assertThat(taskStatusDTOS).hasSize(1);
        assertThatJson(body).isArray();
    }

    @Test
    void testIndexWhenEmpty() throws Exception {
        taskStatusRepository.deleteAll();

        var result = mockMvc.perform(get("/api/task_statuses"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        List<TaskStatusDTO> taskStatusDTOS = objectMapper.readValue(body, new TypeReference<>() {
        });

        assertThat(taskStatusDTOS).isEmpty();
        assertThatJson(body).isArray();
    }

    @Test
    void testShow() throws Exception {
        var request = get("/api/task_statuses/" + testTaskStatus.getId());
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();

        TaskStatusDTO taskStatusDTO = objectMapper.readValue(body, TaskStatusDTO.class);

        assertThat(taskStatusDTO.getName()).isEqualTo(testTaskStatus.getName());
    }

    @Test
    void testShowNotFound() throws Exception {
        var request = get("/api/task_statuses/99999");
        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    void testCreate() throws Exception {
        var data = new HashMap<>();
        data.put("name", "Test Status");
        data.put("slug", "test-status");

        var request = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        TaskStatus taskStatus = taskStatusRepository.findBySlug("test-status").orElse(null);
        assertNotNull(taskStatus);
        assertThat(taskStatus.getName()).isEqualTo("Test Status");
    }

    @Test
    void testCreateWithInvalidData() throws Exception {
        // Test with empty name
        var data = new HashMap<>();
        data.put("name", "");
        data.put("slug", "valid-slug");

        var request = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isUnprocessableEntity());

        // Test with empty slug
        var data2 = new HashMap<>();
        data2.put("name", "Valid Name");
        data2.put("slug", "");

        var request2 = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data2));

        mockMvc.perform(request2).andExpect(status().isUnprocessableEntity());

        // Test with missing name
        var data3 = new HashMap<>();
        data3.put("slug", "valid-slug");

        var request3 = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data3));

        mockMvc.perform(request3).andExpect(status().isUnprocessableEntity());

        // Test with missing slug
        var data4 = new HashMap<>();
        data4.put("name", "Valid Name");

        var request4 = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data4));

        mockMvc.perform(request4).andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testUpdate() throws Exception {
        var data = new HashMap<>();
        data.put("name", "Test Status");
        data.put("slug", "test-status");

        var request = put("/api/task_statuses/" + testTaskStatus.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        TaskStatus updated = taskStatusRepository.findById(testTaskStatus.getId()).get();
        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("Test Status");
    }

    @Test
    void testPartialUpdate() throws Exception {
        // Update only name
        var data = new HashMap<>();
        data.put("name", "Partially Updated");

        var request = put("/api/task_statuses/" + testTaskStatus.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isOk());

        TaskStatus updated = taskStatusRepository.findById(testTaskStatus.getId()).get();
        assertThat(updated.getName()).isEqualTo("Partially Updated");
        assertThat(updated.getSlug()).isEqualTo(testTaskStatus.getSlug());
    }

    @Test
    void testUpdateWithInvalidData() throws Exception {
        // Test with empty name
        var data = new HashMap<>();
        data.put("name", "");
        data.put("slug", "valid-slug");

        var request = put("/api/task_statuses/" + testTaskStatus.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isUnprocessableEntity());

        // Test with empty slug
        var data2 = new HashMap<>();
        data2.put("name", "Valid Name");
        data2.put("slug", "");

        var request2 = put("/api/task_statuses/" + testTaskStatus.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data2));

        mockMvc.perform(request2).andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testUpdateNotFound() throws Exception {
        var data = new HashMap<>();
        data.put("name", "Updated Status");
        data.put("slug", "updated-status");

        var request = put("/api/task_statuses/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    void testDelete() throws Exception {
        var request = delete("/api/task_statuses/" + testTaskStatus.getId());
        mockMvc.perform(request).andExpect(status().isNoContent());

        var taskStatus = taskStatusRepository.findById(testTaskStatus.getId()).orElse(null);
        assertThat(taskStatus).isNull();
    }

    @Test
    void testDeleteNotFound() throws Exception {
        var request = delete("/api/task_statuses/99999");
        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    void testCreateWithLongNamesAndSlugs() throws Exception {
        var longName = "A".repeat(255); // Maximum typical length
        var longSlug = "a".repeat(255);

        var data = new HashMap<>();
        data.put("name", longName);
        data.put("slug", longSlug);

        var request = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        TaskStatus taskStatus = taskStatusRepository.findBySlug(longSlug).orElse(null);
        assertNotNull(taskStatus);
        assertThat(taskStatus.getName()).isEqualTo(longName);
        assertThat(taskStatus.getSlug()).isEqualTo(longSlug);
    }
}
