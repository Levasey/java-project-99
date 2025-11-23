package hexlet.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "task_statuses")
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TaskStatus implements BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @JsonIgnore
    @OneToMany(mappedBy = "taskStatus", cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();

    @NotBlank
    @Column(unique = true)
    @Size(min = 1, message = "Name must be at least 1 characters")
    @ToString.Include
    private String name;

    @NotBlank
    @Column(unique = true)
    @Size(min = 1, message = "Slug must be at least 1 characters")
    @ToString.Include
    private String slug;

    @CreatedDate
    private LocalDate createdAt;
}
