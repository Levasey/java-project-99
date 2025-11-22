package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;

import java.util.List;

public interface LabelService {
    List<LabelDTO> findAll();

    LabelDTO findById(Long id);

    LabelDTO create(LabelCreateDTO labelCreateDTO);

    LabelDTO update(Long id, LabelUpdateDTO labelUpdateDTO);

    void delete(Long id);
}
