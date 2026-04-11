package com.pankaj.complaintmanagement.complaint.repository;

import com.pankaj.complaintmanagement.entity.ComplaintLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintLogRepository extends JpaRepository<ComplaintLog, Long> {
}
