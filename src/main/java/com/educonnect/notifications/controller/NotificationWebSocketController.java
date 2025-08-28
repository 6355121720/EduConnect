package com.educonnect.notifications.controller;

import com.educonnect.notifications.dto.responses.NotificationResponse;
import com.educonnect.notifications.service.NotificationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class NotificationWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    public NotificationWebSocketController(
            SimpMessagingTemplate messagingTemplate,
            NotificationService notificationService
    ) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }
    @MessageMapping("/notify")
    @SendToUser("/queue/notifications")
    public NotificationResponse sendNotification(Principal principal, NotificationResponse notification) {
        // You can add any processing logic here
        return notification;
    }

    // Optional: Method to mark notifications as read via WebSocket
    @MessageMapping("/notifications/mark-read")
    public void markNotificationAsRead(Principal principal, String notificationId) {
        // Implement logic to mark a notification as read
        // This would typically call notificationService.markAsRead(notificationId)
    }
}