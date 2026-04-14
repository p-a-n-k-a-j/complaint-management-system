package com.pankaj.complaintmanagement.complaint.repository;

import com.pankaj.complaintmanagement.entity.Complaint;
import com.pankaj.complaintmanagement.entity.ComplaintLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface ComplaintLogRepository extends JpaRepository<ComplaintLog, Long> {
    Page<ComplaintLog> findAllByTicketId(String ticketId, Pageable pageable);
    Page<ComplaintLog> findAllByComplaint(Complaint complaint, Pageable pageable);
}
