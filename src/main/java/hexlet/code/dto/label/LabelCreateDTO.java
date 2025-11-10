package hexlet.code.dto.label;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabelCreateDTO {
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 3, max = 1000, message = "Name must be between 3 and 1000 characters long")
    private String name;
}
