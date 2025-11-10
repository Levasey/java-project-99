package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
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
        // Проверяем, существует ли метка с таким именем
        labelRepository.findByName(labelCreateDTO.getName())
                .ifPresent(label -> {
                    throw new ResourceAlreadyExistsException("Label with name '" + labelCreateDTO.getName() + "' already exists");
                });

        Label label = labelMapper.map(labelCreateDTO);
        Label saved = labelRepository.save(label);
        return labelMapper.map(saved);
    }

    public LabelDTO update(Long id, LabelUpdateDTO labelUpdateDTO) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + id));

        // Если обновляется имя, проверяем на дублирование
        if (labelUpdateDTO.getName() != null && labelUpdateDTO.getName().isPresent()) {
            String newName = labelUpdateDTO.getName().get();
            labelRepository.findByName(newName)
                    .ifPresent(existingLabel -> {
                        if (!existingLabel.getId().equals(id)) {
                            throw new ResourceAlreadyExistsException("Label with name '" + newName + "' already exists");
                        }
                    });
        }

        labelMapper.update(labelUpdateDTO, label);
        Label updated = labelRepository.save(label);
        return labelMapper.map(updated);
    }

    public void delete(Long id) {
        if (!labelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Label not found with id: " + id);
        }
        labelRepository.deleteById(id);
    }
}