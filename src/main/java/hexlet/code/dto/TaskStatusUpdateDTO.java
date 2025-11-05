package hexlet.code.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskStatusUpdateDTO {
    private JsonNullable<Long> authorId;
    @Size(min = 1, message = "Name must be at least 1 characters")
    private JsonNullable<String> name;

    @Size(min = 1, message = "Slug must be at least 1 characters")
    private JsonNullable<String> slug;
}
