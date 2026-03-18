package com.pankaj.complaintmanagement.notification;

import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class EmailService {
    private static final String CONSTANT_FROM = "Complaint Management Team";
    private static final String CONSTANT_FROM_EMAIL="pankajtirdiya2004@gmail.com";
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


    public void sendCustomEmail(String recipientEmail, String subject, String message){
        Email email = EmailBuilder.startingBlank()
                .from(CONSTANT_FROM, CONSTANT_FROM_EMAIL)
                .to(recipientEmail)
                .withSubject(subject)
                .withReplyTo(recipientEmail)
                .withHTMLText(message)
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

        sendCustomEmail(recipientEmail, subject, message);
    }

    public void sendComplaintStatusUpdateEmail(
            String recipientEmail,
            String name,
            String complaintId,
            String status,
            String adminMessage) {

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
        """, name, complaintId, status, adminMessage);

        sendCustomEmail(recipientEmail, subject, message);
    }


}
