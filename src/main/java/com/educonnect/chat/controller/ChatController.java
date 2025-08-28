package com.educonnect.chat.controller;


import com.educonnect.chat.dto.dto.GroupChatMessageDTO;
import com.educonnect.chat.dto.request.GroupChatMessageRequest;
import com.educonnect.chat.dto.request.PrivateChatRequest;
import com.educonnect.chat.dto.response.PrivateSocketResponse;
import com.educonnect.chat.mapper.GroupChatMessageMapper;
import com.educonnect.user.dto.response.FindResponse;
import com.educonnect.user.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    private final UserService userService;

    private final GroupChatMessageMapper groupChatMessageMapper;

    public ChatController(
            SimpMessagingTemplate messagingTemplate,
            UserService userService,
            GroupChatMessageMapper groupChatMessageMapper
    ){
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.groupChatMessageMapper = groupChatMessageMapper;
    }

    @MessageMapping("/private-chat")
    public void sendPrivateMessage(@Payload PrivateChatRequest request, Principal principal){
        FindResponse sender = userService.find(request.getSenderUname());
        FindResponse receiver = userService.find(request.getReceiverUname());

        System.out.println("wwehuiweguiwe gwhiuw gd7pwegiuwgduwge g");

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
        System.out.println(request);
        messagingTemplate.convertAndSend("/topic/group/"+request.getName(),
                request
        );
    }
}