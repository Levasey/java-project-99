package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LabelService {
    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    @Transactional(readOnly = true)
    public List<LabelDTO> findAll() {
        return labelRepository.findAll()
                .stream()
                .map(labelMapper::map)
                .toList();
    }

    @Transactional(readOnly = true)
    public LabelDTO findById(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + id));
        return labelMapper.map(label);
    }

    public LabelDTO create(LabelCreateDTO labelCreateDTO) {
        // Проверка на уникальность имени
        if (labelRepository.existsByName(labelCreateDTO.getName())) {
            throw new IllegalArgumentException("Label with name '" + labelCreateDTO.getName() + "' already exists");
        }

        Label label = labelMapper.map(labelCreateDTO);
        Label saved = labelRepository.save(label);
        return labelMapper.map(saved);
    }

    public LabelDTO update(Long id, LabelUpdateDTO labelUpdateDTO) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + id));

        // Проверка на уникальность имени при обновлении
        if (labelUpdateDTO.getName() != null &&
                !labelUpdateDTO.getName().get().equals(label.getName()) &&
                labelRepository.existsByName(labelUpdateDTO.getName().get())) {
            throw new IllegalArgumentException("Label with name '" + labelUpdateDTO.getName().get() + "' already exists");
        }

        labelMapper.update(labelUpdateDTO, label);
        Label updated = labelRepository.save(label);
        return labelMapper.map(updated);
    }

    public void delete(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + id));

        // Проверяем, используется ли метка в задачах
        if (!label.getTasks().isEmpty()) {
            throw new IllegalStateException("Cannot delete label that is used in tasks");
        }

        labelRepository.deleteById(id);
    }
}