package com.pankaj.complaintmanagement.complaint.repository;

import com.pankaj.complaintmanagement.common.enums.ComplaintStatus;
import com.pankaj.complaintmanagement.entity.Complaint;
import com.pankaj.complaintmanagement.entity.User;
import com.pankaj.complaintmanagement.util.ComplaintCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    boolean existsByTicketId(String ticketId);

    Page<Complaint> findByUser(User user, Pageable pageable);
    Page<Complaint> findAll(Pageable pageable);
    Page<Complaint> findByComplaintStatus(ComplaintStatus status, Pageable pageable);
    Page<Complaint> findByCategory(ComplaintCategory category, Pageable pageable);
    Page<Complaint> findByComplaintStatusAndCategory(ComplaintStatus status, ComplaintCategory category, Pageable pageable);

}
