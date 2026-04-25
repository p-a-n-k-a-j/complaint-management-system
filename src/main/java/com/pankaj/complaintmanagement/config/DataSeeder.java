package com.pankaj.complaintmanagement.config;

import com.pankaj.complaintmanagement.auth.repository.AuthRepository;
import com.pankaj.complaintmanagement.common.enums.AccountStatus;
import com.pankaj.complaintmanagement.entity.User;
import com.pankaj.complaintmanagement.entity.UserProfile;
import com.pankaj.complaintmanagement.user.repository.UserProfileRepository;
import com.pankaj.complaintmanagement.util.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileRepository profileRepository;

    public DataSeeder(AuthRepository authRepository, PasswordEncoder passwordEncoder, UserProfileRepository profileRepository) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileRepository = profileRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check karo agar Super Admin pehle se hai
        if (authRepository.findByEmail("pankajtirdiya2001@gmail.com") ==null) {

            User superAdmin = new User();
            superAdmin.setEmail("pankajtirdiya2001@gmail.com");
            superAdmin.setPassword(passwordEncoder.encode("@SuperAdmin123")); // Safe password
            superAdmin.setRoles(Set.of(UserRole.ROLE_SUPER_ADMIN, UserRole.ROLE_ADMIN, UserRole.ROLE_USER));
            superAdmin.setStatus(AccountStatus.ACTIVE);
            superAdmin.setCreatedAt(LocalDateTime.now());

            User savedUser = authRepository.save(superAdmin);

            // Profile bhi create karni padegi varna null pointer aayega
            UserProfile profile = new UserProfile();
            profile.setUser(savedUser);
            profile.setFullName("Pankaj (super-admin)");
            profileRepository.save(profile);

            System.out.println("✅ Super Admin created successfully!");
        }
    }
}
