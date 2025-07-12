package com.educonnect.chat.dto.request;


import com.educonnect.user.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroupChatRequest {

    private String name;

    private boolean isPrivate;

}
