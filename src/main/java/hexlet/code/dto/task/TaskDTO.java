package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TaskDTO {
    private Long id;
    private String title;
    private JsonNullable<Integer> index;
    private JsonNullable<String> content;
    private Long taskStatusId;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    private List<Long> taskLabelIds = new ArrayList<>();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdAt;
}
