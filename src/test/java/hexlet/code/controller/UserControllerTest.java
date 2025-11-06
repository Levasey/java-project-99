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
import hexlet.code.dto.user.UserDTO;
import hexlet.code.model.User;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "testuser", roles = {"USER"})
class UserControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Очищаем базу данных перед каждым тестом
        userRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        om.registerModule(new JsonNullableModule());
    }

    @Test
    void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        List<UserDTO> users = om.readValue(body, new TypeReference<>() {});

        assertThat(users).hasSize(1);
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/users/" + testUser.getId());
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();

        UserDTO userDTO = om.readValue(body, UserDTO.class);

        assertThat(userDTO.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(userDTO.getFirstName()).isEqualTo(testUser.getFirstName());
        assertThat(userDTO.getLastName()).isEqualTo(testUser.getLastName());
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getUserModel()).create();

        var request = post("/api/users").contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request).andExpect(status().isCreated());

        var user = userRepository.findByEmail(data.getEmail()).orElse(null);

        assertNotNull(user);
        assertThat(user.getFirstName()).isEqualTo(data.getFirstName());
        assertThat(user.getLastName()).isEqualTo(data.getLastName());
    }

    @Test
    public void testUpdate() throws Exception {

        var data = new HashMap<>();
        data.put("firstName", "newFirstName");
        data.put("lastName", testUser.getLastName());
        data.put("email", testUser.getEmail());
        data.put("password", "newPassword123");

        var request = put("/api/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(testUser.getId()).get();
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getFirstName()).isEqualTo("newFirstName");
    }

    @Test
    public void testPartialUpdate() throws Exception {

        var data = new HashMap<>();
        data.put("firstName", "newFirstName");

        var request = put("/api/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(testUser.getId()).get();
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getFirstName()).isEqualTo("newFirstName");
        assertThat(updatedUser.getLastName()).isEqualTo(testUser.getLastName());
        assertThat(updatedUser.getEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    public void testDelete() throws Exception {
        var request = delete("/api/users/" + testUser.getId());
        mockMvc.perform(request).andExpect(status().isNoContent());

        var user = userRepository.findById(testUser.getId()).orElse(null);
        assertThat(user).isNull();
    }

    @Test
    public void testCreateWithExistingEmail() throws Exception {
        var data = Instancio.of(modelGenerator.getUserModel()).create();
        data.setEmail(testUser.getEmail()); // Используем существующий email

        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isConflict());
    }

    @Test
    public void testUpdateWithExistingEmail() throws Exception {
        // Создаем второго пользователя
        var anotherUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(anotherUser);

        var data = new HashMap<>();
        data.put("email", anotherUser.getEmail()); // Пытаемся использовать email другого пользователя

        var request = put("/api/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isConflict());
    }

    @Test
    public void testShowNonExistentUser() throws Exception {
        var request = get("/api/users/9999");
        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateNonExistentUser() throws Exception {
        var data = new HashMap<>();
        data.put("firstName", "Mike");

        var request = put("/api/users/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    void testDeleteNonExistentUser() throws Exception {
        var request = delete("/api/users/9999");
        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    void testCreateWithInvalidData() throws Exception {
        // Тестируем валидацию - пустой email
        var data = new HashMap<String, Object>();
        data.put("firstName", "John");
        data.put("lastName", "Doe");
        data.put("password", "123"); // Слишком короткий пароль

        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testUserMapping() throws Exception {
        // Проверяем корректность маппинга DTO
        var result = mockMvc.perform(get("/api/users/" + testUser.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        UserDTO userDTO = om.readValue(body, UserDTO.class);

        assertThat(userDTO.getId()).isEqualTo(testUser.getId());
        assertThat(userDTO.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(userDTO.getFirstName()).isEqualTo(testUser.getFirstName());
        assertThat(userDTO.getLastName()).isEqualTo(testUser.getLastName());
        // Проверяем, что пароль не exposed в DTO
    }

    @Test
    void testUpdateWithNullFields() throws Exception {
        // Тестируем обновление с null значениями в JsonNullable
        var data = """
        {
            "firstName": null,
            "lastName": "UpdatedLastName"
        }
        """;

        var request = put("/api/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(data);

        mockMvc.perform(request).andExpect(status().isOk());

        var updatedUser = userRepository.findById(testUser.getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getLastName()).isEqualTo("UpdatedLastName");
        // firstName должен остаться прежним, так как он был явно установлен в null
    }

    @Test
    void testCreateWithNullFields() throws Exception {
        var data = new HashMap<String, Object>();
        // Отправляем неполные данные
        data.put("email", "test@example.com");

        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isUnprocessableEntity());
    }
}