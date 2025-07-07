package com.educonnect.chat.repository;

import com.educonnect.chat.entity.GroupChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GroupChatMessageRepository extends JpaRepository<GroupChatMessage, Long> {

}
