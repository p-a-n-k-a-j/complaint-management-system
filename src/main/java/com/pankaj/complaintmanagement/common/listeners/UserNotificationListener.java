package com.pankaj.complaintmanagement.common.listeners;

import com.pankaj.complaintmanagement.common.enums.AccountStatus;
import com.pankaj.complaintmanagement.common.events.*;
import com.pankaj.complaintmanagement.notification.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
public class UserNotificationListener {

    private final EmailService emailService;
    private final Logger logger = LoggerFactory.getLogger(UserNotificationListener.class);
    public UserNotificationListener(EmailService emailService) {
        this.emailService = emailService;
    }
    @Async
    @EventListener
    public void handlerUserRegistrationEvent(UserRegistrationEvent event){
        try {
        logger.info("Email Service: Background thread started for user registration email to [{}].", event.recipientEmail());
        emailService.sendRegistrationEmail(event.recipientEmail(), event.name());
        logger.info("Email Service: Registration email successfully dispatched to [{}].", event.recipientEmail());
        }catch (Exception e) {
            logger.error("Email Service Error: Failed to process notification for [{}]. Error: {}", event.recipientEmail(), e);
        }
    }

    @Async //<- this is important: because of this the work is done in background
    @EventListener
    public void handleSendOtpEvent(SendOtpEvent event){
        try{
        logger.info("Email Service: Initiating OTP delivery for [{}].", event.recipientEmail());
        emailService.sendOtpEmail(event.recipientEmail());
        logger.info("Otp is send successfully to :{}", event.recipientEmail());
        }catch (Exception e) {
            logger.error("Email Service Error: Failed to process notification for [{}]. Error: {}", event.recipientEmail(), e);
        }
    }

    @Async
    @EventListener
    public void handleAdminRegistrationEvent(AdminRegistrationEvent event){
        try{
        logger.info("Email Service: Processing admin onboard email for [{}].", event.recipientEmail());
        emailService.sendRegistrationEmailToAdmin(event.recipientEmail(), event.name(), event.temPassword());
        logger.info("Email Service: Admin credentials successfully sent to [{}].", event.recipientEmail());
        }catch (Exception e) {
            logger.error("Email Service Error: Failed to process notification for [{}]. Error: {}", event.recipientEmail(), e);
        }
    }

    @Async
    @EventListener
    public void handleAdminPromotionEvent(AdminPromotionEvent event){
        try{
        logger.info("Email Service: Sending role promotion notification to [{}].", event.recipientEmail());
        emailService.sendAdminPromotionEmail(event.recipientEmail(), event.name());
        logger.info("Email Service: Promotion email delivered to [{}].", event.recipientEmail());
        }catch (Exception e) {
            logger.error("Email Service Error: Failed to process notification for [{}]. Error: {}", event.recipientEmail(), e);
        }
    }


    @Async
    @EventListener
    public void handleUpdateComplaintStatusEvent(UpdateComplaintStatusEvent event){
        try{
        logger.info("Email Service: Status update notification triggered for Complaint ID: {} to [{}].", event.complaintId(), event.recipientEmail());
        emailService.sendComplaintStatusUpdateEmail(event.recipientEmail(), event.name(), event.remark(), event.status(), event.complaintId());
        logger.info("Email Service: Status update email sent for Complaint ID: {}.", event.complaintId());
        }catch (Exception e) {
            logger.error("Email Service Error: Failed to process notification for [{}]. Error: {}", event.recipientEmail(), e);
        }
    }

    @Async
    @EventListener
    public void handleRemarkUpdateEvent(RemarkUpdateEvent event){
        try{
        logger.info("Email Service: Remark change notification for Ticket: {} initiated for [{}].", event.ticketId(), event.recipientEmail());
        emailService.sendRemarkChangeEmail(event.recipientEmail(), event.remark(), event.ticketId());
        logger.info("Email Service: Remark update email successfully sent for Ticket: {}.", event.ticketId());
        }catch (Exception e) {
            logger.error("Email Service Error: Failed to process notification for [{}]. Error: {}", event.recipientEmail(), e);
        }
    }

    @Async
    @EventListener
    public void handleAssignComplaintEvent(ComplaintAssignedEvent event){
        try {
            logger.info("Email Service: Assigning Ticket: {} to Admin [{}]. Sending notifications.", event.ticketId(), event.adminEmail());
            emailService.sendAssignmentEmail(event.adminEmail(), event.userEmail(), event.adminName(), event.ticketId(), event.priority());
            logger.info("Email Service: Assignment notifications dispatched to Admin [{}] and User [{}].", event.adminEmail(), event.userEmail());
        }catch (Exception e) {
                logger.error("Email Service Error: Failed to process notification for [{}]. Error: {}", event.adminEmail(), e);
            }
    }

    @Async
    @EventListener
    public void handleUserBlockAndActiveEvent(UserBlockAndActiveEvent event){
      try {
          if (event.status() == AccountStatus.ACTIVE) {
              logger.info("Email Service: Account activation notification started for [{}].", event.recipientEmail());
              String activeEmailTemplate = emailService.getActiveEmailTemplate(event.username());
              emailService.sendCustomEmailInHtml(
                      event.recipientEmail(),
                      "Account Re-activated",
                      activeEmailTemplate
              );
              logger.info("Email Service: Re-activation email successfully delivered to [{}].", event.recipientEmail());
              return;
          }
          logger.info("Email Service: Account restriction notification started for [{}]. Reason: {}", event.recipientEmail(), event.reason());
          String blockEmailTemplate = emailService.getBlockEmailTemplate(event.username(), event.reason());

          emailService.sendCustomEmailInHtml(
                  event.recipientEmail(),
                  "Important: Your Account has been Blocked",
                  blockEmailTemplate
          );
          logger.info("Email Service: Block notification successfully delivered to [{}].", event.recipientEmail());
      }catch (Exception e) {
              logger.error("Email Service Error: Failed to process notification for [{}]. Error: {}", event.recipientEmail(), e);
      }
    }

}
