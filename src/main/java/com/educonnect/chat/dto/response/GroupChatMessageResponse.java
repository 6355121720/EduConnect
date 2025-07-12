package com.educonnect.chat.dto.response;


import com.educonnect.chat.entity.GroupChatMessage;
import com.educonnect.user.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroupChatMessageResponse {

    private Users sender;

    private GroupChatMessage.MediaType mediaType;

    private String fileUrl;

    private String fileName;

    private String content;

}
