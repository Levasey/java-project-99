package hexlet.code.dto.taskStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusCreateDTO {
    private Long authorId;
    @NotBlank
    @Size(min = 1, message = "Name must be at least 1 characters")
    private String name;

    @NotBlank
    @Size(min = 1, message = "Slug must be at least 1 characters")
    private String slug;
}
