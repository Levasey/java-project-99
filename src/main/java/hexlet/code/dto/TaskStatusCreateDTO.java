package hexlet.code.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusCreateDTO {
    private Long authorId;
    @NotBlank
    @Column(unique = true)
    @Size(min = 1, message = "Name must be at least 1 characters")
    private String name;

    @NotBlank
    @Column(unique = true)
    @Size(min = 1, message = "Slug must be at least 1 characters")
    private String slug;
}
