package com.project.Multi_Tenant_SaaS_Backend.startup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Multi_Tenant_SaaS_Backend.data.enums.Role;
import com.project.Multi_Tenant_SaaS_Backend.data.models.User;
import com.project.Multi_Tenant_SaaS_Backend.data.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class DefaultSuperAdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    @Value("${default.admin.password}")
    private String defaultAdminPassword;

    private static final String ADMIN_FILE_PATH = "/jsonFiles/superAdmins.json";

    @Override
    public void run(String... args) {

        try (InputStream inputStream =
                     getClass().getResourceAsStream(ADMIN_FILE_PATH)) {

            if (inputStream == null) {
                log.warn("Admin JSON file not found at: {}", ADMIN_FILE_PATH);
                return;
            }

            JsonNode rootNode = objectMapper.readTree(inputStream);
            JsonNode adminsNode = rootNode.get("admins");

            if (adminsNode == null || !adminsNode.isArray()) {
                log.warn("Invalid superAdmins.json structure.");
                return;
            }

            for (JsonNode adminNode : adminsNode) {

                String email = adminNode.asText().trim();
                if (email.isEmpty()) continue;

                userRepository.findByEmail(email).ifPresentOrElse(
                        user -> log.info("System admin already exists: {}", email),
                        () -> {

                            User admin = new User();
                            admin.setName("SUPER_ADMIN");
                            admin.setEmail(email);
                            admin.setPassword(passwordEncoder.encode(defaultAdminPassword));
                            admin.setRole(Role.SYSTEM_ADMIN);
                            admin.setCompany(null);

                            userRepository.save(admin);

                            log.info("Created SYSTEM_ADMIN account: {}", email);
                        }
                );
            }

        } catch (Exception e) {
            log.error("Failed to initialize default admins", e);
        }
    }
}
