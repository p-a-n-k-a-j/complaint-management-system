package com.pankaj.complaintmanagement.common.services;

import com.pankaj.complaintmanagement.complaint.dto.ComplaintLogResponseDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
/***
 * this service is responsible to do all websocket related work:
 * for now I keep it simple and use for sending complaint logs or notification.
 * for private endpoint like /queue this is spacial one only authorized person can call
 * otherwise my interceptor that I implemented in WebSocketConfiguration will block your request.
 * in the future, this service will help to maintain real time conversation
 * */
@Service
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;
    public WebSocketService(SimpMessagingTemplate messagingTemplate){
        this.messagingTemplate = messagingTemplate;
    }

    public void sendUpdatedLog(ComplaintLogResponseDTO responseDTO){
        // Topic: /topic/complaint/101
        String topic = "/topic/complaint/" + responseDTO.getComplaintId();
        messagingTemplate.convertAndSend(topic, responseDTO);
    }
    public void notifyUser(String userEmail, ComplaintLogResponseDTO responseDTO) {
        // Ye sirf us user ko dikhega jiski email 'userEmail' hai
        messagingTemplate.convertAndSendToUser(userEmail, "/queue/notifications", responseDTO);
    }
    public void sendPrivateNotification(String email, NotificationResponse payload) {
        messagingTemplate.convertAndSendToUser(email, "/queue/notifications", payload);
    }

    public record NotificationResponse(
            String title,
            String message,
            String ticketId
    ){}
}
