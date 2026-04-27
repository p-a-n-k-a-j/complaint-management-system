package com.pankaj.complaintmanagement.complaint.service;

import com.pankaj.complaintmanagement.common.enums.ComplaintStatus;
import com.pankaj.complaintmanagement.complaint.dto.*;
import com.pankaj.complaintmanagement.entity.Complaint;
import com.pankaj.complaintmanagement.entity.User;
import com.pankaj.complaintmanagement.util.ComplaintCategory;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ComplaintService {

    /**
     * Registers a new complaint in the system.
     */
    void createComplaint(ComplaintRequest request, User user);

    /**
     * Updates an existing complaint with full request data.
     */
    void updateComplaint(ComplaintUpdateRequest request, User user);

    /**
     * Updates the status and adds a remark to a specific complaint.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    ComplaintResponseDTO updateComplaintStatus(Long complaintId, ComplaintStatus status, String remark, User admin);

    /**
     * Retrieves all activity logs for a given ticket ID.
     */
    List<ComplaintLogResponseDTO> getLogsByTicketId(String ticketId, User user);

    /**
     * Fetches complaints updated today that are assigned to a specific admin.
     */
    @PreAuthorize("hasRole('ADMIN')")
    List<ComplaintResponseDTO> getTodayUpdatesAssignedAdmin(User admin);

    /**
     * Fetches today's updates for complaints belonging to a specific user.
     */
    @PreAuthorize("hasRole('USER')")
    List<ComplaintResponseDTO> getTodayUpdatesForUser(User user);

    /**
     * Fetches all complaint updates performed across the system today.
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    List<ComplaintResponseDTO> getTodayUpdates();

    /**
     * Deletes a complaint. Authorized for the owner or a Super Admin.
     */
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    void deleteComplaint(Long complaintId, User userAndSuperAdmin);

    /**
     * Adds a management remark to a complaint without changing its status.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    ComplaintResponseDTO setRemarkToComplaint(Long complaintId, String remark, User adminAndSuperAdmin);

    /**
     * Assigns a complaint to a specific admin.
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
     Complaint assignTo(Long complaintId, Long adminId, User superAdmin);

    /**
     * Retrieves a paginated list of complaints assigned to the current admin.
     */
    @PreAuthorize("hasRole('ADMIN')")
    Page<ComplaintResponseDTO> getMyAssignedComplaint(int page, int size, User admin);

    /**
     * Retrieves a paginated list of complaints created by the current user.
     */
    Page<ComplaintResponseDTO> getMyComplaints(int page, int size, User user);

    /**
     * Provides a filtered list of all complaints for Super Admin oversight.
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    Page<ComplaintResponseDTO> getAllComplaintsForSuperAdmin(int page, int size, ComplaintStatus status, ComplaintCategory category);

    /**
     * Fetches the full audit trail (logs) for a specific complaint.
     */
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    Page<ComplaintLogResponseDTO> getComplaintHistory(int page, int size, Long complaintId, User userOrAdmin);

    /**
     * Retrieves detailed information of a complaint by its unique Ticket ID.
     */
    ComplaintResponseDTO getComplaintByTicketId(String ticketId, User currentUser);

    /**
     * Performs a partial update on complaint details for the user.
     */
    @PreAuthorize("hasRole('USER')")
    void partialUpdateComplaint(ComplaintRequest request, User user);


    /**
     * Replaces all existing attachments for a user's complaint.
     * This method will delete all currently stored files from Cloudinary
     * and replace them with the newly provided list of files.
     *
     * @param user  The current authenticated user owning the complaint.
     * @param files The new list of files to be uploaded.
     */
    void updateAttachments(Long complaintId, List<MultipartFile> files, User user);

    /**
     * Appends new attachments to the existing list of files.
     * This method does not delete any previous files; it only uploads
     * additional files to the user's current complaint.
     * @param complaintId That complaint id you want to set these files.
     * @param user  The current authenticated user.
     * @param files The list of additional files to be uploaded.
     */
    void addAttachments(Long complaintId, List<MultipartFile> files, User user);

    /**
     * Return All Stats of complaint with count,
     *  for example,
     *  PENDING: 0,
     *  IN_PROGRESS: 3,
     *  RESOLVED: 4
     *  TOTAL_WORKLOAD:7
     *  etc.
     *
     * ***/

    @PreAuthorize("hasRole('ADMIN','SUPER_ADMIN')")
    Map<String, Long> getAdminStats(Long adminId);


    /**
     * Return All Stats of complaint with count for user,
     *  for example,
     *  PENDING: 0,
     *  IN_PROGRESS: 3,
     *  RESOLVED: 4
     *  TOTAL_Complaints:7
     *  etc.
     *
     * ***/
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    Map<String, Long> getUserStats(Long userId);

    void deleteAttachment(Long complaintId, User user);
}

