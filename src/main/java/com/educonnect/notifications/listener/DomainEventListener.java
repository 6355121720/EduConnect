package com.educonnect.notifications.listener;

import com.educonnect.connection.service.ConnectionService;
import com.educonnect.event.service.EventCreatedDomainEvent;
import com.educonnect.notifications.service.NotificationService;
import com.educonnect.user.service.UserService;
import com.educonnect.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DomainEventListener {

    private final NotificationService notificationService;
    private final ConnectionService conection;
    private final UserService userService;

    @Async
    @EventListener
    public void onEventCreated(EventCreatedDomainEvent ev) {

        Users user = userService.findById(ev.creatorId());

        List<Users> recipients = conection.getConnections(user);
        System.out.println("Kunj kunj ************** jsdhfuia hgfewgcfewgy cfbj gfuyewjf hsuj");

        for (Users r : recipients) {
            notificationService.createNotification(r, userService.findById(ev.creatorId()), "NEW_EVENT",
                    "New event created by: " + user.getFullName() , "/events/" + ev.eventId());
        }
    }
}
