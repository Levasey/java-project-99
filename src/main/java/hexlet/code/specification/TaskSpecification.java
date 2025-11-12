package hexlet.code.specification;

import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.model.Task;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {
    public Specification<Task> build(TaskParamsDTO params) {
        return withAssigneeId(params.getAssigneeId())
                .and(withLabelId(params.getLabelId()))
                .and(withTitleCont(params.getTitleCont()))
                .and(withStatus(params.getStatus()));
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, cb) ->
                assigneeId == null ? cb.conjunction() : cb.equal(root.get("assignee").get("id"), assigneeId);
    }

    private Specification<Task> withLabelId(Long labelId) {
        return ((root, query, cb) ->
                labelId == null ? cb.conjunction()
                        : cb.equal(root.join("labels", JoinType.INNER).get("id"), labelId));
    }

    private Specification<Task> withTitleCont(String titleCont) {
        return (root, query, cb) ->
                titleCont == null ? cb.conjunction() :
                        cb.like(cb.lower(root.get("name")), "%" + titleCont.toLowerCase() + "%");
    }

    private Specification<Task> withStatus(String status) {
        return (root, query, cb) ->
                status == null ?
                        cb.conjunction() :
                        cb.equal(root.get("taskStatus").get("slug"), status);
    }
}
