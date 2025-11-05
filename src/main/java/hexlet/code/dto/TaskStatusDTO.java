package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskStatusDTO {
    private Long id;
    private Long authorId;
    private String name;
    private String slug;
}
