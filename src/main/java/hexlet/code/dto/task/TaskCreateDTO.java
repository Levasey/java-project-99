package hexlet.code.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TaskCreateDTO {
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, message = "Name must be at least 1 character long")
    private String name;

    private int index;
    private String description;

    @NotNull(message = "Task status is required")
    private Long taskStatusId;

    private Long assigneeId;

    private List<Long> taskLabelIds = new ArrayList<>();
}
