package com.pankaj.complaintmanagement.complaint.repository;

import com.pankaj.complaintmanagement.common.enums.ComplaintStatus;
import com.pankaj.complaintmanagement.entity.Complaint;
import com.pankaj.complaintmanagement.entity.User;
import com.pankaj.complaintmanagement.util.ComplaintCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    boolean existsByTicketId(String ticketId);

    Page<Complaint> findByUser(User user, Pageable pageable);
    Page<Complaint> findAll(Pageable pageable);
    Page<Complaint> findByStatus(ComplaintStatus status, Pageable pageable);
    Page<Complaint> findByCategory(ComplaintCategory category, Pageable pageable);
    Page<Complaint> findByStatusAndCategory(ComplaintStatus status, ComplaintCategory category, Pageable pageable);

    Optional<Complaint> findByTicketId(String ticketId);

    Optional<Complaint> findByUserAndId(Long id, User user);
    Page <Complaint> findByAssignedTo(User admin,Pageable pageable);


    List<Complaint> findByAssignedToAndUpdatedAtBetween(User admin, LocalDateTime startOfDay, LocalDateTime endOfDay);
    List<Complaint> findByUserAndUpdatedAtBetween(User user, LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Complaint> findByUpdatedAtBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("SELECT c.status, COUNT(c) FROM Complaint c WHERE c.assignedTo.id = :adminId GROUP BY c.status")
    List<Object[]> countComplaintsByStatusForAdmin(@Param("adminId") Long adminId);

    @Query("SELECT c.status, COUNT(c) FROM Complaint c WHERE c.user.id = :userId GROUP BY c.status")
    List<Object[]> countComplaintsByStatusForUser(@Param("userId") Long userId);
}
