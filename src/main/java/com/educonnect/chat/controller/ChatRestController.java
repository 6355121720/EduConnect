package com.educonnect.chat.controller;


import com.educonnect.auth.service.AuthService;
import com.educonnect.chat.dto.request.PrivateChatRequest;
import com.educonnect.chat.entity.PrivateChatMessage;
import com.educonnect.chat.service.ChatService;
import com.educonnect.exceptionhandling.exception.FileUploadException;
import com.educonnect.user.entity.Users;
import com.educonnect.utils.aws.s3.S3FileUploadUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    private final S3FileUploadUtil s3FileUploadUtil;

    private final ChatService chatService;

    private final AuthService authService;

    public ChatRestController(S3FileUploadUtil s3FileUploadUtil, ChatService chatService, AuthService authService){
        this.s3FileUploadUtil = s3FileUploadUtil;
        this.chatService = chatService;
        this.authService = authService;
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
    public ResponseEntity<?> privateChat(@RequestBody PrivateChatRequest request){
        chatService.privateChat(request);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/get-private")
    public ResponseEntity<?> getPrivateChat(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "with") String receiverUname){
        Users currentUser = authService.me(request, response);
        List<PrivateChatMessage> messages = chatService.getMessages(currentUser, receiverUname);
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }

}
