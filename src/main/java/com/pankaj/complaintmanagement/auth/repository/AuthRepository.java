package com.pankaj.complaintmanagement.auth.repository;

import com.pankaj.complaintmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<User, Long> {


    User findByEmail(String email);
}
