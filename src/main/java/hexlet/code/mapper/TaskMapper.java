package hexlet.code.mapper;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.mapstruct.*;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "description", source = "content")
    @Mapping(source = "taskStatusId", target = "taskStatus.id")
    @Mapping(target = "name", source = "title")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "forLabels")
    public abstract Task map(TaskCreateDTO data);

    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "name", target = "title")
    @Mapping(source = "description", target = "content")
    @Mapping(source = "taskStatus.id", target = "taskStatusId")
    @Mapping(source = "labels", target = "taskLabelIds", qualifiedByName = ("reverseLabels"))
    public abstract TaskDTO map(Task data);

    @Mapping(target = "assignee.id", source = "assigneeId")
    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(source = "taskStatusId", target = "taskStatus.id")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "forLabels")
    public abstract Task map(TaskDTO data);

    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "mapAssignee")
    @Mapping(target = "description", source = "content")
    @Mapping(source = "taskStatusId", target = "taskStatus.id")
    @Mapping(target = "name", source = "title")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "forLabels")
    public abstract void update(TaskUpdateDTO data, @MappingTarget Task task);

    @Named("reverseLabels")
    public List<Long> reverseLabels(List<Label> labelList) {
        if (labelList != null) {
            List<Long> labels = new ArrayList<>();
            for (var label : labelList) {
                labels.add(label.getId());
            }
            return labels;
        }
        return null;
    }


    @Named("forLabels")
    public List<Label> forLabels(List<Long> labelsIds) {
        List<Label> labels = new ArrayList<>();
        for (var i : labelsIds) {
            var label = labelRepository.findById(i).orElseThrow();
            labels.add(label);
        }
        return labels;
    }

    @Named("mapAssignee")
    public User mapAssignee(JsonNullable<Long> assigneeId) {
        if (!assigneeId.isPresent() || assigneeId == null) {
            return null;
        }
        if (assigneeId.get() == null) {
            return null;
        }
        return userRepository.findById(assigneeId.get())
                .orElseThrow();
    }
}
