package com.pankaj.complaintmanagement.notification;

import com.pankaj.complaintmanagement.common.enums.ComplaintStatus;
import com.pankaj.complaintmanagement.complaint.dto.ComplaintResponseDTO;
import com.pankaj.complaintmanagement.entity.Complaint;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final String CONSTANT_FROM = "Complaint Management Team";
    private static final String CONSTANT_FROM_EMAIL="noreply.complaintmanagement@gmail.com";
    private static final String APP_PASSWORD="grqj xepm fjzs xabv";

    private static final Mailer mailer = MailerBuilder
            .withSMTPServer("smtp.gmail.com" , 587, CONSTANT_FROM_EMAIL, APP_PASSWORD.trim())
            .withTransportStrategy(TransportStrategy.SMTP_TLS)
            .buildMailer();
    private OtpService otpService;
    public EmailService(OtpService otpService) {
        this.otpService = otpService;
    }


    public int sendOtpEmail(String recipientEmail){
        int otp = otpService.generateOtp();

        // 2. Email ka content fix (Template) rakho
        String subject = "Verification Code: " + otp;
        String htmlMessage = "<h3>Hello User,</h3>" +
                "<p>Your One-Time Password (OTP) for login is:</p>" +
                "<h1 style='color:blue;'>" + otp + "</h1>" +
                "<p>This code is valid for 5 minutes. Do not share it.</p>";

        Email email = EmailBuilder.startingBlank()
                .from(CONSTANT_FROM, CONSTANT_FROM_EMAIL)
                .to(recipientEmail)
                .withSubject(subject)
                .withReplyTo(recipientEmail)
                .withHTMLText(htmlMessage)
                .buildEmail();
        mailer.sendMail(email);
        otpService.saveOtp(recipientEmail, otp);
        return otp;
    }


    public void sendCustomEmailInHtml(String recipientEmail, String subject, String message){
        Email email = EmailBuilder.startingBlank()
                .from(CONSTANT_FROM, CONSTANT_FROM_EMAIL)
                .to(recipientEmail)
                .withSubject(subject)
                .withReplyTo(recipientEmail)
                .withHTMLText(message)
                .buildEmail();
        mailer.sendMail(email);
    }
    public String getBlockEmailTemplate(String userName, String reason) {
        String supportEmail = "noreply.complaintmanagment@gmail.com";

        return "<html>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "    <div style='max-width: 600px; margin: 0 auto; border: 1px solid #ddd; padding: 20px; border-radius: 10px;'>" +
                "        <h2 style='color: #d9534f;'>Account Access Restricted</h2>" +
                "        <p>Hi <strong>" + userName + "</strong>,</p>" +
                "        <p>This is to inform you that your account on <strong>Complaint Management System</strong> has been blocked by the Administrator.</p>" +
                "        <div style='background-color: #f9f9f9; padding: 15px; border-left: 5px solid #d9534f; margin: 20px 0;'>" +
                "            <strong>Reason for blocking:</strong><br>" +
                reason +
                "        </div>" +
                "        <p>If you believe this action was taken in error or you wish to appeal this decision, please click the button below to contact our support team:</p>" +
                "        <div style='text-align: center; margin: 30px 0;'>" +
                "            <a href='mailto:" + supportEmail + "?subject=Appeal for Blocked Account: " + userName + "' " +
                "               style='background-color: #0275d8; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold;'>" +
                "               Connect to Support" +
                "            </a>" +
                "        </div>" +
                "        <p>Regards,<br><strong>Admin Team</strong><br>Complaint Management System</p>" +
                "        <hr style='border: 0; border-top: 1px solid #eee; margin: 20px 0;'>" +
                "        <p style='font-size: 12px; color: #777;'>Note: This is a system-generated email. Please do not reply directly to this address.</p>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }
    public String getActiveEmailTemplate(String userName) {
        return "<html>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "    <div style='max-width: 600px; margin: 0 auto; border: 1px solid #ddd; padding: 20px; border-radius: 10px;'>" +
                "        <h2 style='color: #5cb85c;'>Account Re-Activated!</h2>" + // Green color for success
                "        <p>Hi <strong>" + userName + "</strong>,</p>" +
                "        <p>Great news! Your account on the <strong>Complaint Management System</strong> has been successfully re-activated by the Administrator.</p>" +
                "        <p>You can now log in to your dashboard and continue using our services as usual.</p>" +
                "        <div style='text-align: center; margin: 30px 0;'>" +
                "            <a href='http://your-app-url.com/login' " + // Apna login URL daal dena
                "               style='background-color: #5cb85c; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold;'>" +
                "               Login Now" +
                "            </a>" +
                "        </div>" +
                "        <p>If you face any issues while logging in, feel free to contact our support team.</p>" +
                "        <p>Regards,<br><strong>Admin Team</strong><br>Complaint Management System</p>" +
                "        <hr style='border: 0; border-top: 1px solid #eee; margin: 20px 0;'>" +
                "        <p style='font-size: 12px; color: #777;'>Note: This is an automated notification.</p>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }
    public void sendCustomEmailInPlainText(String recipientEmail, String subject, String message){
        Email email = EmailBuilder.startingBlank()
                .from(CONSTANT_FROM, CONSTANT_FROM_EMAIL)
                .to(recipientEmail)
                .withSubject(subject)
                .withReplyTo(recipientEmail)
                .withPlainText(message)
                .buildEmail();
        mailer.sendMail(email);
    }

    public void sendRegistrationEmail(String recipientEmail, String name){
        String subject = "Welcome to Complaint Management System";

        String message = String .format("""
        <html>
        <body style="font-family: Arial, sans-serif; background-color:#f4f6f8; padding:20px;">
            <div style="max-width:600px; margin:auto; background:white; padding:20px; border-radius:8px;">
                
                <h2 style="color:#2c3e50;">Welcome to Complaint Management System</h2>
                
                <p>Dear %s,</p>
                
                <p>Your account has been successfully registered with the 
                <strong>Complaint Management Team</strong>.</p>
                
                <p>You can now submit complaints, track their status, and receive updates directly through the system.</p>
                
                <p style="margin-top:20px;">
                    If you did not create this account, please contact our support team immediately.
                </p>
                
                <hr style="margin:25px 0;">
                
                <p style="font-size:12px; color:#7f8c8d;">
                    This is an automated message from the Complaint Management System.<br>
                    Please do not reply to this email.
                </p>
                
                <p style="font-size:12px; color:#7f8c8d;">
                    Regards,<br>
                    Complaint Management Team
                </p>
                
            </div>
        </body>
        </html>
        """, name);

        sendCustomEmailInHtml(recipientEmail, subject, message);
    }
    public void sendRegistrationEmailToAdmin(String recipientEmail, String name, String tempPassword) {
        String subject = "Welcome! Your Admin Account is Ready";

        String message = String.format("""
    <html>
    <body style="font-family: 'Segoe UI', Arial, sans-serif; background-color:#f4f7f9; padding:20px; color: #333;">
        <div style="max-width:600px; margin:auto; background:white; padding:30px; border-radius:12px; border: 1px solid #e1e4e8;">
            
            <div style="text-align: center; margin-bottom: 25px;">
                <h2 style="color:#2c3e50; margin:0;">Account Registered</h2>
                <p style="color:#7f8c8d; font-size:14px;">Complaint Management System</p>
            </div>
            
            <p>Hello <strong>%s</strong>,</p>
            
            <p>Your account has been successfully created by the Super Admin. You can now log in to the dashboard using the credentials below:</p>
            
            <div style="background-color:#f8f9fa; padding:20px; border-radius:8px; border-left: 4px solid #3498db; margin:20px 0;">
                <p style="margin:0; font-size:14px; color:#555;"><strong>Login Email:</strong> %s</p>
                <p style="margin:10px 0 0 0; font-size:14px; color:#555;"><strong>Temporary Password:</strong> <span style="color:#e74c3c; font-weight:bold;">%s</span></p>
            </div>
            
            <p style="font-size: 14px; color: #e67e22; font-weight: bold;">
                Note: For security reasons, please change your password immediately after your first login.
            </p>
            
            <div style="text-align: center; margin-top: 30px;">
                <a href="http://your-app-url.com/login" 
                   style="background-color: #3498db; color: white; padding: 12px 25px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block;">
                   Login to Your Dashboard
                </a>
            </div>
            
            <hr style="border: 0; border-top: 1px solid #eee; margin:30px 0;">
            
            <p style="font-size:12px; color:#95a5a6; text-align: center;">
                Regards, <br>
                <strong>System Administrator</strong>
            </p>
        </div>
    </body>
    </html>
    """, name, recipientEmail, tempPassword);

        sendCustomEmailInHtml(recipientEmail, subject, message);
    }

    public void sendAdminPromotionEmail(String recipientEmail, String name) {
        String subject = "Promotion: You are now an Admin!";

        String message = String.format("""
    <html>
    <body style="font-family: 'Segoe UI', Arial, sans-serif; background-color:#f9f9f9; padding:20px;">
        <div style="max-width:600px; margin:auto; background:white; padding:30px; border-radius:12px; border: 1px solid #ddd; box-shadow: 0 4px 10px rgba(0,0,0,0.05);">
            
            <div style="text-align: center; margin-bottom: 20px;">
                <span style="background-color: #f1c40f; color: #000; padding: 5px 15px; border-radius: 20px; font-size: 14px; font-weight: bold;">SYSTEM UPDATE</span>
            </div>

            <h2 style="color:#2c3e50; text-align: center;">Congratulations, %s!</h2>
            
            <p style="font-size: 16px; color: #34495e;">
                We are pleased to inform you that your account has been <strong>elevated to Admin status</strong>.
            </p>
            
            <p style="color: #555; line-height: 1.6;">
                As an Admin, you now have the authority to:
                <ul style="color: #555;">
                    <li>Access the Admin Dashboard</li>
                    <li>Manage and resolve user complaints</li>
                    <li>Update complaint statuses and add remarks</li>
                </ul>
            </p>
            
            <div style="margin: 30px 0; text-align: center;">
                <a href="http://your-app-url.com/admin/dashboard" 
                   style="background-color: #3498db; color: white; padding: 12px 25px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block;">
                   Go to Admin Dashboard
                </a>
            </div>

            <p style="font-size: 14px; color: #e67e22; font-weight: bold;">
                Note: Your existing login credentials remain the same.
            </p>
            
            <hr style="border: 0; border-top: 1px solid #eee; margin: 30px 0;">
            
            <p style="font-size:12px; color:#95a5a6; text-align: center;">
                Regards, <br>
                <strong>System Administration Team</strong>
            </p>
        </div>
    </body>
    </html>
    """, name);

        sendCustomEmailInHtml(recipientEmail, subject, message);
    }
    public void sendComplaintStatusUpdateEmail(String recipientEmail, String name, String remark, ComplaintStatus status, Long complaintId) {

        String subject = "Update on Your Complaint #" + complaintId;

        String message = String.format("""
        <html>
        <body style="font-family: Arial, sans-serif; background-color:#f4f6f8; padding:20px;">
            <div style="max-width:600px; margin:auto; background:white; padding:20px; border-radius:8px;">
                
                <h2 style="color:#2c3e50;">Complaint Status Update</h2>
                
                <p>Dear %s,</p>
                
                <p>Your complaint with ID <strong>%s</strong> has been updated.</p>
                
                <p><strong>Current Status:</strong> %s</p>
                
                <p>%s</p>
                
                <p>You can log in to the Complaint Management System to view more details.</p>
                
                <hr style="margin:25px 0;">
                
                <p style="font-size:12px; color:#7f8c8d;">
                    This is an automated notification from the Complaint Management Team.
                </p>
                
                <p style="font-size:12px; color:#7f8c8d;">
                    Regards,<br>
                    Complaint Management Team
                </p>
                
            </div>
        </body>
        </html>
        """, name, complaintId, status, remark);

        sendCustomEmailInHtml(recipientEmail, subject, message);
    }
    public void sendRemarkChangeEmail(String recipientEmail, String remark, String ticketId) {
        // 1. HTML Template using Text Block (Java 15+)
        String htmlContent = """
        <div style="font-family: Arial, sans-serif; border: 1px solid #ddd; padding: 20px; border-radius: 10px; max-width: 500px; margin: auto; text-align: center;">
            <h2 style="color: #2c3e50;">Complaint Update</h2>
            <p style="font-size: 16px; color: #555;">Admin has added a new remark to your complaint.</p>
            
            <div style="background-color: #f9f9f9; padding: 15px; border-left: 5px solid #3498db; margin: 20px 0; text-align: left;">
                <strong>Remark:</strong>
                <p style="font-style: italic; color: #333;">%s</p>
            </div>

            <p style="font-size: 12px; color: #888;">Ticket ID: <strong>%s</strong></p>
            <hr style="border: 0; border-top: 1px solid #eee;">
            <p style="font-size: 14px; color: #2ecc71;">Keep tracking your complaint for further updates!</p>
        </div>
        """.formatted(remark, ticketId);

        // 2. Call your email service
        String subject = "Update on Complaint #" + ticketId;
        sendCustomEmailInHtml(recipientEmail, subject, htmlContent);
    }

    public void sendAssignmentEmail(String adminEmail, String userEmail, String adminName, String ticketId, String priority) {

        String htmlContent = """
    <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; border: 1px solid #e0e0e0; padding: 25px; border-radius: 12px; max-width: 550px; margin: auto; background-color: #ffffff;">
        <div style="text-align: center; margin-bottom: 20px;">
            <span style="background-color: #3498db; color: white; padding: 5px 15px; border-radius: 50px; font-size: 12px; font-weight: bold; text-transform: uppercase;">New Task</span>
            <h2 style="color: #2c3e50; margin-top: 10px;">New Complaint Assigned</h2>
        </div>
        
        <p style="color: #7f8c8d; font-size: 15px; line-height: 1.5;">
            Hello %s, <br>
            A new complaint has been assigned to you by the Super Admin. Please review the details below and take necessary action.
        </p>
        
        <div style="background-color: #f8f9fa; border-radius: 8px; padding: 15px; margin: 20px 0; border-left: 4px solid #3498db;">
            <table style="width: 100%%; border-collapse: collapse;">
                <tr>
                    <td style="padding: 5px 0; color: #7f8c8d; font-size: 14px;">Ticket ID:</td>
                    <td style="padding: 5px 0; color: #2c3e50; font-weight: bold;">#%s</td>
                </tr>
                <tr>
                    <td style="padding: 5px 0; color: #7f8c8d; font-size: 14px;">Priority:</td>
                    <td style="padding: 5px 0; color: #e74c3c; font-weight: bold;">%s</td>
                </tr>
                <tr>
                    <td style="padding: 5px 0; color: #7f8c8d; font-size: 14px;">User Email:</td>
                    <td style="padding: 5px 0; color: #2c3e50;">%s</td>
                </tr>
            </table>
        </div>

        <div style="text-align: center; margin-top: 25px;">
            <a href="http://your-app-url.com/admin/complaints/%s" 
               style="background-color: #2ecc71; color: white; padding: 12px 25px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block;">
               View Complaint Details
            </a>
        </div>
    </div>
    """.formatted(
                adminName,
                ticketId,
                priority,
                userEmail,
                ticketId
        );

        String userHtml = """
        <div style="font-family: sans-serif; border: 1px solid #e0e0e0; padding: 25px; border-radius: 12px; max-width: 550px; margin: auto;">
            <h2 style="color: #2c3e50;">Complaint Update</h2>
            <p>Hello,</p>
            <p>Your complaint (Ticket <b>#%s</b>) has been assigned to our support executive <b>%s</b>.</p>
            <p>Priority: <b>%s</b></p>
            <p>They will start working on it soon.</p>
        </div>
    """.formatted(ticketId, adminName, priority);

        sendCustomEmailInHtml(adminEmail, "New Assignment - Ticket #" + ticketId, htmlContent);
        sendCustomEmailInHtml(userEmail, "Update on your Complaint - Ticket #" + ticketId, userHtml);
    }

}
