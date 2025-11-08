package hexlet.code.config;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private CustomUserDetailsService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var email = "hexlet@example.com";

        if (userRepository.findByEmail(email).isEmpty()) {
            var userData = new User();
            userData.setEmail(email);
            userData.setPasswordDigest("qwerty");
            userService.createUser(userData);
        }

        if (taskStatusRepository.findAll().isEmpty()) {
            createTaskStatus("draft", "draft");
            createTaskStatus("to review", "to_review");
            createTaskStatus("to be fixed", "to_be_fixed");
            createTaskStatus("to publish", "to_publish");
            createTaskStatus("published", "published");
        }

        if (labelRepository.findAll().isEmpty()) {
            createLabel("feature");
            createLabel("bug");
        }
    }

    private TaskStatus createTaskStatus(String name, String slug) {
        var taskStatus = new TaskStatus();
        taskStatus.setName(name);
        taskStatus.setSlug(slug);
        taskStatusRepository.save(taskStatus);
        return taskStatus;
    }

    private Label createLabel(String name) {
        var label = new Label();
        label.setName(name);
        labelRepository.save(label);
        return label;
    }
}
