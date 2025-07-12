package com.educonnect.chat.repository;


import com.educonnect.chat.entity.GroupRequestJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRequestJoinRepository extends JpaRepository<GroupRequestJoin, Long> {



}
