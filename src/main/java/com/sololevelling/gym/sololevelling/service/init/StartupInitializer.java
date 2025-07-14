/*

  * © 2025 Praveen Kumar. All rights reserved.
  *
  * This software is licensed under the MIT License.
  * See the LICENSE file in the root directory for more information.


 */

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
import com.sololevelling.gym.sololevelling.repo.RoleRepository;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class StartupInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        createDefaultRoles();
        createAdminUser();
    }

    private void createDefaultRoles() {
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
        String email = "admin@sololevel.com";
        if (!userRepository.existsByEmail(email)) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail(email);
            admin.setPassword(new BCryptPasswordEncoder().encode("admin123")); // Use a stronger pass in prod
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
