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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "labels")
@EntityListeners(AuditingEntityListener.class)
public class Label implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Column(unique = true)
    @Size(min = 3, max = 1000, message = "Name must be between 3 and 1000 characters long")
    private String name;

    @ManyToMany(mappedBy = "labels")
    @JsonIgnore
    List<Task> tasks = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;
}
