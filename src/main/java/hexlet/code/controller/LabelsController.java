package hexlet.code.controller;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.service.LabelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class LabelsController {
    @Autowired
    private LabelService labelService;

    @GetMapping
    public ResponseEntity<List<LabelDTO>> index() {
        return ResponseEntity.ok(labelService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabelDTO> show(@PathVariable Long id) {
        LabelDTO labelDTO = labelService.findById(id);
        return ResponseEntity.ok(labelDTO);
    }

    @PostMapping
    public ResponseEntity<LabelDTO> create(@Valid @RequestBody LabelCreateDTO labelCreateDTO) {
        LabelDTO created = labelService.create(labelCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabelDTO> update(@PathVariable Long id,
                                                @Valid @RequestBody LabelUpdateDTO labelUpdateDTO) {
        LabelDTO updated = labelService.update(id, labelUpdateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        labelService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
