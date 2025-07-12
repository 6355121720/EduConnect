package com.educonnect.chat.controller;


import com.educonnect.chat.dto.request.GroupChatMessageRequest;
import com.educonnect.chat.dto.request.GroupChatRequest;
import com.educonnect.chat.dto.request.PrivateChatRequest;
import com.educonnect.chat.dto.request.PrivateSocketRequest;
import com.educonnect.chat.dto.response.GroupChatMessageResponse;
import com.educonnect.chat.dto.response.PrivateSocketResponse;
import com.educonnect.chat.entity.PrivateChatMessage;
import com.educonnect.user.dto.response.FindResponse;
import com.educonnect.user.entity.Users;
import com.educonnect.user.service.UserService;
import org.hibernate.annotations.processing.Find;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    private final UserService userService;

    public ChatController(SimpMessagingTemplate messagingTemplate, UserService userService){
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
    }

    @MessageMapping("/private-chat")
    public void sendPrivateMessage(@Payload PrivateChatRequest request, Principal principal){
        FindResponse sender = userService.find(request.getSenderUname());
        FindResponse receiver = userService.find(request.getReceiverUname());

//        System.out.println(simpUserRegistry.getUserCount() + simpUserRegistry.getUsers().toString());
//        for (SimpUser user : simpUserRegistry.getUsers()) {
//            System.out.println("Username: " + user.getName());
//            user.getSessions().forEach(session -> {
//                System.out.println("  Session ID: " + session.getId());
//                session.getSubscriptions().forEach(sub -> {
//                    System.out.println("    Subscribed to: " + sub.getDestination());
//                });
//            });
//        }

        messagingTemplate.convertAndSendToUser(
                request.getReceiverUname(),
                "/queue/message",
                PrivateSocketResponse.builder()
                        .content(request.getContent())
                        .fileUrl(request.getFileUrl())
                        .timestamp(request.getTimestamp())
                        .mediaType(request.getMediaType())
                        .receiver(receiver.getUser())
                        .fileName(request.getFileName())
                        .sender(sender.getUser())
                        .build()
        );
    }

    @MessageMapping("/group-chat")
    public void sendGroupMessage(@Payload GroupChatMessageRequest request, Principal principal){
        messagingTemplate.convertAndSend("/topic/group/"+request.getName(),
                new GroupChatMessageResponse(
                        request.getSender(),
                        request.getMediaType(),
                        request.getFileUrl(),
                        request.getFileName(),
                        request.getContent()
                )
                );
    }

}
