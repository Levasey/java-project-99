package hexlet.code.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@ToString(includeFieldNames = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Task implements BaseEntity {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @ToString.Include
    @Size(min = 1, message = "Name must be at least 1 character long")
    private String name;

    @ToString.Include
    private int index;

    @ToString.Include
    private String description;

    @ManyToMany
    @JoinTable(
            name = "task_labels",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    List<Label> labels = new ArrayList<>();

    @NotNull(message = "Task status cannot be blank")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_status_id")
    @ToString.Include
    private TaskStatus taskStatus;

    @ManyToOne
    @ToString.Include
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @CreatedDate
    private LocalDate createdAt;
}
