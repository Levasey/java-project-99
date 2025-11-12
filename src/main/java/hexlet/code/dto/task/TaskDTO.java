package hexlet.code.dto.task;

import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TaskDTO {
    private Long id;
    private String title;
    private int index;
    private String content;
    private Long taskStatusId;
    private Long assigneeId;
    private List<Long> taskLabelIds = new ArrayList<>();
    private LocalDateTime createdAt;
}
