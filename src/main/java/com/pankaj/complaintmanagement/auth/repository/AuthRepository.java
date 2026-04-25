package com.pankaj.complaintmanagement.auth.repository;

import com.pankaj.complaintmanagement.entity.User;
import com.pankaj.complaintmanagement.util.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuthRepository extends JpaRepository<User, Long> {
    // Role ke basis par users find karne ke liye
    Page<User> findAllByRoles(UserRole role, Pageable pageable);

    User findByEmail(String email);

    @Query(value = "SELECT u FROM User u " +
            "JOIN FETCH u.userProfile " + // Dono tables ko merge kiya
            "JOIN u.roles r " +            // Roles collection mein ghuse
            "WHERE r = :role",             // Filter lagaya
            countQuery = "SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = :role")
    Page<User> findAllByRolesWithProfile(@Param("role") UserRole role, Pageable pageable);
}
