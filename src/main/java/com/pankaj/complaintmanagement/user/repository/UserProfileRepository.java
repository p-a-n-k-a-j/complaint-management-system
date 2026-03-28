package com.pankaj.complaintmanagement.user.repository;

import com.pankaj.complaintmanagement.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    UserProfile findByUserId(Long userId);
}
