package hexlet.code.dto.task;

import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

@Getter
@Setter
public class TaskUpdateDTO {
    private JsonNullable<String> title;
    private JsonNullable<Long> taskStatusId;
    private JsonNullable<Long> assigneeId;
    private JsonNullable<List<Long>> taskLabelIds;
    private JsonNullable<Integer> index;
    private JsonNullable<String> content;
}
