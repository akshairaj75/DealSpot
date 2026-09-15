package com.backend.dealspot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.backend.dealspot.entity.AdminUser;
import com.backend.dealspot.enums.AdminRole;
import com.backend.dealspot.repository.AdminUserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    public DataInitializer(AdminUserRepository adminUserRepository,
                           PasswordEncoder passwordEncoder,
                           JdbcTemplate jdbcTemplate) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        // Ensure audit_logs legacy columns permit null values for flexible auditing
        try {
            jdbcTemplate.execute("ALTER TABLE audit_logs MODIFY COLUMN performed_by BIGINT NULL");
        } catch (Exception ex) {
            log.debug("Schema update note (performed_by): {}", ex.getMessage());
        }

        try {
            jdbcTemplate.execute("ALTER TABLE audit_logs MODIFY COLUMN entity_id BIGINT NULL");
        } catch (Exception ex) {
            log.debug("Schema update note (entity_id): {}", ex.getMessage());
        }

        try {
            jdbcTemplate.execute("ALTER TABLE audit_logs MODIFY COLUMN entity_type VARCHAR(80) NULL");
        } catch (Exception ex) {
            log.debug("Schema update note (entity_type): {}", ex.getMessage());
        }

        if (adminUserRepository.count() == 0) {
            AdminUser superAdmin = new AdminUser();
            superAdmin.setFullName("Super Administrator");
            superAdmin.setEmail("admin@dealspot.com");
            superAdmin.setPasswordHash(passwordEncoder.encode("admin123"));
            superAdmin.setRole(AdminRole.SUPER_ADMIN);
            superAdmin.setActive(true);

            adminUserRepository.save(superAdmin);
            log.info(">>> Initialized default Super Admin: admin@dealspot.com / admin123");
        }
    }
}
