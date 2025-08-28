package com.educonnect.notifications.repository;
// package com.educonnect.notification.repository;
import com.educonnect.notifications.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId, Pageable pageable);

    long countByRecipientIdAndSeenFalse(UUID recipientId);

    @Modifying
    @Query("update Notification n set n.seen = true where n.recipient.id = :recipientId and n.seen = false")
    int markAllAsSeen(UUID recipientId);
}
