package hexlet.code.config;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Get credentials from environment variables
        String adminEmail = System.getenv("ADMIN_EMAIL");
        String adminPassword = System.getenv("ADMIN_PASSWORD");

        if (adminEmail == null || adminPassword == null) {
            System.out.println("Admin credentials not set in environment variables");
            return;
        }

        // Create admin user if it doesn't exist
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPasswordDigest(passwordEncoder.encode(adminPassword));
            admin.setFirstName("Admin");
            admin.setLastName("Hexlet");
            userRepository.save(admin);
            System.out.println("Admin user created with email: " + adminEmail);
        }
    }
}
