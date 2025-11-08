package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.util.ModelGenerator;
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

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LabelControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private LabelRepository labelRepository;

    private Label testLabel;

    @BeforeEach
    void setUp() {
        labelRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();

        objectMapper.registerModule(new JsonNullableModule());

        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);
    }

    @Test
    void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/labels"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        List<LabelDTO> labelDTOS = objectMapper.readValue(body, new TypeReference<List<LabelDTO>>() {});

        assertThat(labelDTOS).hasSize(1);
        assertThatJson(body).isNotNull();
        assertThatJson(body).isArray();
    }

    @Test
    void testShow() throws Exception {
        var result = mockMvc.perform(get("/api/labels/" + testLabel.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        LabelDTO labelDTO = objectMapper.readValue(body, LabelDTO.class);

        assertThat(labelDTO.getId()).isEqualTo(testLabel.getId());
        assertThat(labelDTO.getName()).isEqualTo(testLabel.getName());
    }

    @Test
    void testShowNotFound() throws Exception {
        var result = mockMvc.perform(get("/api/labels/9999"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void testCreate() throws Exception {
        var data = new HashMap<String, Object>();
        data.put("name", "Created name");

        var request = post("/api/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        LabelDTO createdLabelDTO = objectMapper.readValue(body, LabelDTO.class);

        assertThat(createdLabelDTO.getName()).isEqualTo("Created name");
    }

    @Test
    void testUpdate() throws Exception {
        var data = new HashMap<>();
        data.put("name", "Updated name");

        var request = put("/api/labels/" + testLabel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();

        LabelDTO updatedLabelDTO = objectMapper.readValue(body, LabelDTO.class);

        Label updatedLabel = labelRepository.findById(updatedLabelDTO.getId()).get();
        assertThat(updatedLabel).isNotNull();
        assertThat(updatedLabel.getName()).isEqualTo("Updated name");
    }

    @Test
    void testUpdateNotFound() throws Exception {
        var data = Map.of("name", "Updated Name");

        var request = put("/api/labels/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void testDelete() throws Exception {
        var request = delete("/api/labels/" + testLabel.getId());
        mockMvc.perform(request).andExpect(status().isNoContent());

        Label label = labelRepository.findById(testLabel.getId()).orElse(null);
        assertThat(label).isNull();
    }

    @Test
    void testDeleteNotFound() throws Exception {
        mockMvc.perform(delete("/api/labels/9999"))
                .andExpect(status().isNotFound());
    }
}
