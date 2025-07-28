/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.service.init;

import com.sololevelling.gym.sololevelling.model.Role;
import com.sololevelling.gym.sololevelling.model.Stats;
import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.model.init.SystemSetting;
import com.sololevelling.gym.sololevelling.repo.RoleRepository;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import com.sololevelling.gym.sololevelling.repo.init.SystemSettingRepository;
import com.sololevelling.gym.sololevelling.util.log.SoloLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SystemSettingRepository systemSettingRepository;
    @Value("${app.admin.email}")
    private String adminEmail;
    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        boolean alreadyInitialized = systemSettingRepository.existsById("init-flag");
        if (alreadyInitialized) {
            SoloLogger.info("🚩 Initial setup flag found. Skipping initialization.");
            return;
        }
        SoloLogger.info("🏗️ Starting system initialization...");
        createDefaultRoles();
        createAdminUser();

        systemSettingRepository.save(new SystemSetting("init-flag", true));
    }

    private void createDefaultRoles() {
        SoloLogger.info("🛡️ Creating default roles");
        List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN");

        for (String roleName : roles) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
        }
    }

    private void createAdminUser() {
        SoloLogger.info("👑 Creating admin user with email: {}", adminEmail);
        String email = adminEmail;
        if (!userRepository.existsByEmail(email)) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail(email);
            admin.setPassword(new BCryptPasswordEncoder().encode(adminPassword));
            admin.setRoles(Set.of(
                    roleRepository.findByName("ROLE_ADMIN").orElseThrow()
            ));
            admin.setLevel(99);
            admin.setExperience(999999);
            admin.setStats(new Stats());
            userRepository.save(admin);
        }
    }
}
