package hexlet.code.mapper;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import org.mapstruct.*;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskStatusMapper {
    @Mapping(target = "author", source = "authorId")
    public abstract TaskStatus map(TaskStatusCreateDTO model);

    @Mapping(target = "authorId", source = "author.id")
    public abstract TaskStatusDTO map(TaskStatus data);

    public abstract TaskStatus map(TaskStatusDTO data);

    public abstract void update(TaskStatusUpdateDTO data, @MappingTarget TaskStatus model);

    public User mapAuthorIdToUser(Long authorId) {
        if (authorId == null) {
            return null;
        }
        User user = new User();
        user.setId(authorId);
        return user;
    }
}
