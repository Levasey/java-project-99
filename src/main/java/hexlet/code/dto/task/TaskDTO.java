package hexlet.code.dto.task;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TaskDTO {
    private Long id;
    private String name;
    private int index;
    private String description;
    private Long taskStatusId;
    private Long assigneeId;
    private List<Long> taskLabelIds = new ArrayList<>();
    private LocalDateTime createdAt;
}
