package hexlet.code.mapper;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.model.Task;
import org.mapstruct.*;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {
    @Mapping(source = "taskStatus.id", target = "taskStatusId")
    @Mapping(source = "assignee.id", target = "assigneeId")
    public abstract TaskDTO map(Task model);

    @Mapping(target = "taskStatus", source = "taskStatusId")
    @Mapping(target = "assignee", source = "assigneeId")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(source = "taskStatusId", target = "taskStatus.id")
    @Mapping(source = "assigneeId", target = "assignee.id")
    public abstract Task map(TaskDTO dto);

    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task model);
}
