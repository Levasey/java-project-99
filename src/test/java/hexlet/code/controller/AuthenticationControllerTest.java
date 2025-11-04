package hexlet.code.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.AuthRequest;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void login_withValidCredentials_returnsToken() throws Exception {
        // Создание тестового пользователя с заполненными обязательными полями
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordDigest(passwordEncoder.encode("password"));
        user.setFirstName("John");
        user.setLastName("Doe");
        userRepository.save(user);

        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("test@example.com");
        authRequest.setPassword("password");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isString()); // Токен возвращается как строка, не как JSON объект
    }
}
