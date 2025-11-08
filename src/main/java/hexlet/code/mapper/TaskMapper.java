package hexlet.code.mapper;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {
    @Autowired
    private LabelRepository labelRepository;

    @Mapping(source = "taskStatus.id", target = "taskStatusId")
    @Mapping(source = "assignee.id", target = "assigneeId")
    public abstract TaskDTO map(Task model);

    @Mapping(target = "taskStatus", source = "taskStatusId")
    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "forLabels")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(source = "taskStatusId", target = "taskStatus.id")
    @Mapping(source = "assigneeId", target = "assignee.id")
    public abstract Task map(TaskDTO dto);

    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task model);

    @Named("forLabels")
    public List<Label> forLabels(List<Long> labelsIds) {
        List<Label> labels = new ArrayList<>();
        for (var i : labelsIds) {
            var label = labelRepository.findById(i).orElseThrow();
            labels.add(label);
        }
        return labels;
    }
}
