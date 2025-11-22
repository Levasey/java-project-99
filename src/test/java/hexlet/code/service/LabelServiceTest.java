package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.impl.LabelServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class LabelServiceTest {

    @Mock
    private LabelRepository labelRepository;

    @Mock
    private LabelMapper labelMapper;

    @InjectMocks
    private LabelServiceImpl labelService;

    private Label testLabel;
    private LabelDTO testLabelDTO;
    private LabelCreateDTO testLabelCreateDTO;
    private LabelUpdateDTO testLabelUpdateDTO;

    @BeforeEach
    void setUp() {
        testLabel = new Label();
        testLabel.setId(1L);
        testLabel.setName("Test Label");
        testLabel.setCreatedAt(LocalDateTime.now());

        testLabelDTO = new LabelDTO();
        testLabelDTO.setId(1L);
        testLabelDTO.setName("Test Label");

        testLabelCreateDTO = new LabelCreateDTO();
        testLabelCreateDTO.setName("Test Label");

        testLabelUpdateDTO = new LabelUpdateDTO();
        testLabelUpdateDTO.setName(JsonNullable.of("Updated Label"));
    }

    @Test
    void testFindAll() {
        // Given
        Label label1 = new Label();
        label1.setId(1L);
        label1.setName("Label 1");

        Label label2 = new Label();
        label2.setId(2L);
        label2.setName("Label 2");

        LabelDTO labelDTO1 = new LabelDTO();
        labelDTO1.setId(1L);
        labelDTO1.setName("Label 1");

        LabelDTO labelDTO2 = new LabelDTO();
        labelDTO2.setId(2L);
        labelDTO2.setName("Label 2");

        when(labelRepository.findAll()).thenReturn(Arrays.asList(label1, label2));
        when(labelMapper.map(label1)).thenReturn(labelDTO1);
        when(labelMapper.map(label2)).thenReturn(labelDTO2);

        // When
        List<LabelDTO> result = labelService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Label 1");
        assertThat(result.get(1).getName()).isEqualTo("Label 2");

        verify(labelRepository, times(1)).findAll();
        verify(labelMapper, times(1)).map(label1);
        verify(labelMapper, times(1)).map(label2);
    }

    @Test
    void testFindById() {
        // Given
        when(labelRepository.findById(1L)).thenReturn(Optional.of(testLabel));
        when(labelMapper.map(testLabel)).thenReturn(testLabelDTO);

        // When
        LabelDTO result = labelService.findById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Label");

        verify(labelRepository, times(1)).findById(1L);
        verify(labelMapper, times(1)).map(testLabel);
    }

    @Test
    void testFindByIdNotFound() {
        // Given
        when(labelRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> labelService.findById(999L));

        verify(labelRepository, times(1)).findById(999L);
        verify(labelMapper, never()).map(any(Label.class));
    }

    @Test
    void testCreate() {
        // Given
        when(labelRepository.findByName("Test Label")).thenReturn(Optional.empty());
        when(labelMapper.map(testLabelCreateDTO)).thenReturn(testLabel);
        when(labelRepository.save(testLabel)).thenReturn(testLabel);
        when(labelMapper.map(testLabel)).thenReturn(testLabelDTO);

        // When
        LabelDTO result = labelService.create(testLabelCreateDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Label");

        verify(labelRepository, times(1)).findByName("Test Label");
        verify(labelMapper, times(1)).map(testLabelCreateDTO);
        verify(labelRepository, times(1)).save(testLabel);
        verify(labelMapper, times(1)).map(testLabel);
    }

    @Test
    void testCreateWithDuplicateName() {
        // Given
        when(labelRepository.findByName("Test Label")).thenReturn(Optional.of(testLabel));

        // When & Then
        assertThrows(ResourceAlreadyExistsException.class, () -> labelService.create(testLabelCreateDTO));

        verify(labelRepository, times(1)).findByName("Test Label");
        verify(labelMapper, never()).map(any(LabelCreateDTO.class));
        verify(labelRepository, never()).save(any(Label.class));
    }

    @Test
    void testUpdate() {
        // Given
        when(labelRepository.findById(1L)).thenReturn(Optional.of(testLabel));
        when(labelRepository.findByName("Updated Label")).thenReturn(Optional.empty());
        when(labelRepository.save(testLabel)).thenReturn(testLabel);
        when(labelMapper.map(testLabel)).thenReturn(testLabelDTO);

        // When
        LabelDTO result = labelService.update(1L, testLabelUpdateDTO);

        // Then
        assertThat(result).isNotNull();

        verify(labelRepository, times(1)).findById(1L);
        verify(labelRepository, times(1)).findByName("Updated Label");
        verify(labelMapper, times(1)).update(testLabelUpdateDTO, testLabel);
        verify(labelRepository, times(1)).save(testLabel);
        verify(labelMapper, times(1)).map(testLabel);
    }

    @Test
    void testUpdateWithDuplicateName() {
        // Given
        Label existingLabel = new Label();
        existingLabel.setId(2L);
        existingLabel.setName("Updated Label");

        when(labelRepository.findById(1L)).thenReturn(Optional.of(testLabel));
        when(labelRepository.findByName("Updated Label")).thenReturn(Optional.of(existingLabel));

        // When & Then
        assertThrows(ResourceAlreadyExistsException.class, () -> labelService.update(1L, testLabelUpdateDTO));

        verify(labelRepository, times(1)).findById(1L);
        verify(labelRepository, times(1)).findByName("Updated Label");
        verify(labelMapper, never()).update(any(LabelUpdateDTO.class), any(Label.class));
        verify(labelRepository, never()).save(any(Label.class));
    }

    @Test
    void testUpdateWithSameName() {
        // Given
        testLabelUpdateDTO.setName(JsonNullable.of("Test Label")); // То же имя

        when(labelRepository.findById(1L)).thenReturn(Optional.of(testLabel));
        when(labelRepository.findByName("Test Label")).thenReturn(Optional.of(testLabel)); // Тот же объект
        when(labelRepository.save(testLabel)).thenReturn(testLabel);
        when(labelMapper.map(testLabel)).thenReturn(testLabelDTO);

        // When
        LabelDTO result = labelService.update(1L, testLabelUpdateDTO);

        // Then
        assertThat(result).isNotNull();

        verify(labelRepository, times(1)).findById(1L);
        verify(labelRepository, times(1)).findByName("Test Label");
        verify(labelMapper, times(1)).update(testLabelUpdateDTO, testLabel);
        verify(labelRepository, times(1)).save(testLabel);
        verify(labelMapper, times(1)).map(testLabel);
    }

    @Test
    void testUpdateNotFound() {
        // Given
        when(labelRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> labelService.update(999L, testLabelUpdateDTO));

        verify(labelRepository, times(1)).findById(999L);
        verify(labelRepository, never()).findByName(any(String.class));
        verify(labelMapper, never()).update(any(LabelUpdateDTO.class), any(Label.class));
        verify(labelRepository, never()).save(any(Label.class));
    }

    @Test
    void testDelete() {
        // Given
        when(labelRepository.existsById(1L)).thenReturn(true);

        // When
        labelService.delete(1L);

        // Then
        verify(labelRepository, times(1)).existsById(1L);
        verify(labelRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteNotFound() {
        // Given
        when(labelRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> labelService.delete(999L));

        verify(labelRepository, times(1)).existsById(999L);
        verify(labelRepository, never()).deleteById(999L);
    }

    @Test
    void testUpdateWithNullName() {
        // Given
        LabelUpdateDTO updateDTOWithNullName = new LabelUpdateDTO();
        updateDTOWithNullName.setName(JsonNullable.undefined());

        when(labelRepository.findById(1L)).thenReturn(Optional.of(testLabel));
        when(labelRepository.save(testLabel)).thenReturn(testLabel);
        when(labelMapper.map(testLabel)).thenReturn(testLabelDTO);

        // When
        LabelDTO result = labelService.update(1L, updateDTOWithNullName);

        // Then
        assertThat(result).isNotNull();

        verify(labelRepository, times(1)).findById(1L);
        verify(labelRepository, never()).findByName(any(String.class)); // Не должно проверять имя
        verify(labelMapper, times(1)).update(updateDTOWithNullName, testLabel);
        verify(labelRepository, times(1)).save(testLabel);
        verify(labelMapper, times(1)).map(testLabel);
    }
}
