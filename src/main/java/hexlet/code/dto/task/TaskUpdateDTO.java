package hexlet.code.dto.task;

import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskUpdateDTO {
    private JsonNullable<String> name;
    private JsonNullable<Long> taskStatusId;
    private JsonNullable<Long> assigneeId;
    private JsonNullable<Integer> index;
    private JsonNullable<String> description;
}
