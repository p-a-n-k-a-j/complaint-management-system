package com.pankaj.complaintmanagement.complaint.service;

import com.pankaj.complaintmanagement.common.enums.ComplaintStatus;
import com.pankaj.complaintmanagement.complaint.dto.ComplaintLogDTO;
import com.pankaj.complaintmanagement.complaint.dto.ComplaintRequest;
import com.pankaj.complaintmanagement.complaint.dto.ComplaintResponseDTO;
import com.pankaj.complaintmanagement.entity.Complaint;
import com.pankaj.complaintmanagement.entity.User;
import com.pankaj.complaintmanagement.util.ComplaintCategory;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ComplaintService {

    // Nayi complaint register karne ke liye
    void createComplaint(ComplaintRequest request, User user);

    // Specific complaint ka status aur remark badalne ke liye (Admin/SuperAdmin)
    void updateComplaintStatus(Long complaintId, ComplaintStatus status, String remark, User admin);

    // Complaint deletes karna (Standard: Only User who created or Admin)
    void deleteComplaint(Long complaintId);

    // User ko uski apni complaints dikhane ke liye (Pagination)
    Page<ComplaintResponseDTO> getMyComplaints(int page, int size, User user);

    // Admin ko saari complaints dikhane ke liye (With filters like Status/Category)
    Page<ComplaintResponseDTO> getAllComplaints(int page, int size, ComplaintStatus status, ComplaintCategory category);

    // Complaint ki poori history (Logs) fetch karne ke liye
    List<ComplaintLogDTO> getComplaintHistory(String ticketId);

    // Individual complaint details ticketId se nikalne ke liye
    ComplaintResponseDTO getComplaintByTicketId(String ticketId);

    void updateComplaint(ComplaintRequest request, User user);
}

