package com.educonnect.chat.controller;


import com.educonnect.auth.service.AuthService;
import com.educonnect.chat.dto.request.GroupChatRequest;
import com.educonnect.chat.dto.request.GroupJoinRequest;
import com.educonnect.chat.dto.request.PrivateChatRequest;
import com.educonnect.chat.entity.GroupChat;
import com.educonnect.chat.entity.GroupChatMessage;
import com.educonnect.chat.entity.GroupRequestJoin;
import com.educonnect.chat.entity.PrivateChatMessage;
import com.educonnect.chat.service.ChatService;
import com.educonnect.exceptionhandling.exception.FileUploadException;
import com.educonnect.user.entity.Users;
import com.educonnect.utils.aws.s3.S3FileUploadUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    private final S3FileUploadUtil s3FileUploadUtil;

    private final ChatService chatService;

    private final AuthService authService;

    private final SimpUserRegistry simpUserRegistry;

    public ChatRestController(S3FileUploadUtil s3FileUploadUtil, ChatService chatService, AuthService authService, SimpUserRegistry simpUserRegistry){
        this.s3FileUploadUtil = s3FileUploadUtil;
        this.chatService = chatService;
        this.authService = authService;
        this.simpUserRegistry = simpUserRegistry;
    }

    @PostMapping("/fileupload")
    public ResponseEntity<String> fileUpload(@RequestParam("file") MultipartFile file){
        String url;
        try{
            url = s3FileUploadUtil.uploadFile(file);
        }
        catch (Exception e){
            throw new FileUploadException("Exception while uploading file." + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(url);
    }

    @PostMapping("/private")
    public ResponseEntity<PrivateChatMessage> privateChat(@RequestBody PrivateChatRequest request){
        System.out.println(request);
        PrivateChatMessage message = chatService.privateChat(request);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/get-private")
    public ResponseEntity<?> getPrivateChat(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "with") String receiverUname){
        Users currentUser = authService.me(request, response);
        List<PrivateChatMessage> messages = chatService.getMessages(currentUser, receiverUname);
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }

    @GetMapping("/get-online")
    public ResponseEntity<Integer> getCount(){
        return ResponseEntity.ok(simpUserRegistry.getUserCount());
    }

    @PostMapping("/make-group")
    public ResponseEntity<GroupChat> makeGroup(HttpServletRequest request, HttpServletResponse response, @RequestBody GroupChatRequest requestData){
        Users admin = authService.me(request, response);
        GroupChat groupChat = chatService.makeGroup(requestData, admin);
        return ResponseEntity.ok(groupChat);
    }

    @GetMapping("/get-group/{groupName}")
    public ResponseEntity<GroupChat> getGroup(@PathVariable("groupName") String name){
        GroupChat groupChat = chatService.getGroup(name);
        return ResponseEntity.ok(groupChat);
    }

    @GetMapping("/get-group-messages/{groupName}")
    public ResponseEntity<List<GroupChatMessage>> getGroupMessages(@PathVariable("groupName") String name){
        List<GroupChatMessage> messages = chatService.getGroupMessages(name);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/my-groups")
    public ResponseEntity<List<GroupChat>> myGroups(HttpServletRequest request, HttpServletResponse response){
        Users currentUser = authService.me(request, response);
        List<GroupChat> groups = chatService.myGroups(currentUser);
        return ResponseEntity.ok(groups);
    }

    @PostMapping("/group-join")
    public ResponseEntity<?> joinMember(@RequestBody GroupJoinRequest request){
        chatService.joinMember(request);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/group-request")
    public ResponseEntity<?> joinRequest(@RequestBody GroupJoinRequest request){
        chatService.joinRequest(request);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/group-search")
    public ResponseEntity<List<GroupChat>> searchGroup(@RequestParam("search") String search){
        List<GroupChat> groups = chatService.searchGroup(search);
        return ResponseEntity.ok(groups);
    }

}
