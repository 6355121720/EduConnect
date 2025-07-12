package com.educonnect.chat.service;


import com.educonnect.chat.dto.request.GroupChatRequest;
import com.educonnect.chat.dto.request.GroupJoinRequest;
import com.educonnect.chat.dto.request.PrivateChatRequest;
import com.educonnect.chat.entity.GroupChat;
import com.educonnect.chat.entity.GroupChatMessage;
import com.educonnect.chat.entity.GroupRequestJoin;
import com.educonnect.chat.entity.PrivateChatMessage;
import com.educonnect.chat.repository.GroupChatMessageRepository;
import com.educonnect.chat.repository.GroupChatRepository;
import com.educonnect.chat.repository.GroupRequestJoinRepository;
import com.educonnect.chat.repository.PrivateChatMessageRepository;
import com.educonnect.exceptionhandling.exception.BusinessRuleViolationException;
import com.educonnect.exceptionhandling.exception.InvalidCredentialsException;
import com.educonnect.user.entity.Users;
import com.educonnect.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    private final PrivateChatMessageRepository privateChatMessageRepository;

    private final GroupChatMessageRepository groupChatMessageRepository;

    private final GroupChatRepository groupChatRepository;

    private final UserRepository userRepository;

    private final GroupRequestJoinRepository groupRequestJoinRepository;

    public ChatService(
            UserRepository userRepository,
            PrivateChatMessageRepository privateChatMessageRepository,
            GroupChatMessageRepository groupChatMessageRepository,
            GroupChatRepository groupChatRepository,
            GroupRequestJoinRepository groupRequestJoinRepository
    ){
        this.privateChatMessageRepository = privateChatMessageRepository;
        this.groupChatRepository = groupChatRepository;
        this.groupChatMessageRepository = groupChatMessageRepository;
        this.userRepository = userRepository;
        this.groupRequestJoinRepository = groupRequestJoinRepository;
    }

    public PrivateChatMessage privateChat(PrivateChatRequest request){
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

        return message;

    }

    public List<PrivateChatMessage> getMessages(Users sender, String receiverUname){
        Users receiver = userRepository.findByUsername(receiverUname);
        if (sender == null || receiver == null){
            throw new BusinessRuleViolationException("sender or receiver doesn't found.");
        }
        return privateChatMessageRepository.chatWith(sender, receiver);
    }

    public GroupChat makeGroup(GroupChatRequest request, Users admin){
        if (request.getName() == null || request.getName().isEmpty() || admin == null){
            throw new BusinessRuleViolationException("null attributes given.");
        }

        GroupChat groupChat = GroupChat.builder()
                .admin(admin)
                .isPrivate(request.isPrivate())
                .name(request.getName())
                .build();

        return groupChatRepository.save(groupChat);
    }

    public GroupChat getGroup(String name){
        if (name == null){
            throw new BusinessRuleViolationException("null attributes given.");
        }

        GroupChat groupChat = groupChatRepository.findByName(name).orElseThrow(() -> {
            throw new InvalidCredentialsException("invalid name for group.");
        });

        return groupChat;
    }

    public List<GroupChatMessage> getGroupMessages(String name){
        GroupChat groupChat = groupChatRepository.findByName(name).orElseThrow(() -> {
            throw new BusinessRuleViolationException("group name is wrong.");
        });

        List<GroupChatMessage> messages = groupChatMessageRepository.getMessages(groupChat);

        return messages;
    }

    public List<GroupChat> myGroups(Users user){
        List<GroupChat> groups = groupChatRepository.myGroups(user);

        return groups;
    }

    public void joinRequest(GroupJoinRequest request){
        Users sender = userRepository.findByUsername(request.getUsername());
        Optional<GroupChat> groupChat = groupChatRepository.findByName(request.getGroupName());

        if (sender == null || groupChat.isEmpty()){
            throw new BusinessRuleViolationException("wrong attributes given.");
        }

        GroupRequestJoin groupRequestJoin = new GroupRequestJoin();
        groupRequestJoin.setGroup(groupChat.get());
        groupRequestJoin.setSender(sender);

        groupRequestJoinRepository.save(groupRequestJoin);

    }

    public void joinMember(GroupJoinRequest request){
        Users sender = userRepository.findByUsername(request.getUsername());
        Optional<GroupChat> groupChat = groupChatRepository.findByName(request.getGroupName());

        if (sender == null || groupChat.isEmpty()){
            throw new BusinessRuleViolationException("wrong attributes given.");
        }

        groupChat.get().getMembers().add(sender);

        groupChatRepository.save(groupChat.get());
    }

    public List<GroupChat> searchGroup(String search){
        List<GroupChat> groups = groupChatRepository.search(search);
        return groups;
    }

}
