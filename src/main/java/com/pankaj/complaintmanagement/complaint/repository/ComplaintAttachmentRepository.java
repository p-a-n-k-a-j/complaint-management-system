package com.pankaj.complaintmanagement.complaint.repository;

import com.pankaj.complaintmanagement.entity.Complaint;
import com.pankaj.complaintmanagement.entity.ComplaintAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintAttachmentRepository  extends JpaRepository<ComplaintAttachment, Long> {
    List<ComplaintAttachment> findByComplaint(Complaint complaint);
}
