package com.educonnect.chat.service;


import com.educonnect.chat.dto.request.PrivateChatRequest;
import com.educonnect.chat.entity.PrivateChatMessage;
import com.educonnect.chat.repository.GroupChatMessageRepository;
import com.educonnect.chat.repository.GroupChatRepository;
import com.educonnect.chat.repository.PrivateChatMessageRepository;
import com.educonnect.exceptionhandling.exception.BusinessRuleViolationException;
import com.educonnect.user.entity.Users;
import com.educonnect.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final PrivateChatMessageRepository privateChatMessageRepository;

    private final GroupChatMessageRepository groupChatMessageRepository;

    private final GroupChatRepository groupChatRepository;

    private final UserRepository userRepository;

    public ChatService(UserRepository userRepository, PrivateChatMessageRepository privateChatMessageRepository, GroupChatMessageRepository groupChatMessageRepository, GroupChatRepository groupChatRepository){
        this.privateChatMessageRepository = privateChatMessageRepository;
        this.groupChatRepository = groupChatRepository;
        this.groupChatMessageRepository = groupChatMessageRepository;
        this.userRepository = userRepository;
    }

    public void privateChat(PrivateChatRequest request){
        if (request.getMediaType() == null || request.getContent() == null || request.getFileUrl() == null || request.getFileName() == null || request.getSenderUname() == null || request.getReceiverUname() == null || request.getTimestamp() == null){
            throw new BusinessRuleViolationException("Null attributes given.");
        }

        Users sender = userRepository.findByUsername(request.getSenderUname());
        Users receiver = userRepository.findByUsername(request.getReceiverUname());

        if (sender == null || receiver == null){
            throw new BusinessRuleViolationException("sender or receiver uname doesn't found.");
        }

        PrivateChatMessage message = PrivateChatMessage.builder()
                .content(request.getContent())
                .fileUrl(request.getFileUrl())
                .fileName(request.getFileName())
                .type(request.getMediaType())
                .receiver(receiver)
                .timestamp(request.getTimestamp())
                .sender(sender)
                .build();

        privateChatMessageRepository.save(message);

        return;

    }

    public List<PrivateChatMessage> getMessages(Users sender, String receiverUname){
        Users receiver = userRepository.findByUsername(receiverUname);
        if (sender == null || receiver == null){
            throw new BusinessRuleViolationException("sender or receiver doesn't found.");
        }
        return privateChatMessageRepository.chatWith(sender, receiver);
    }

}
