package com.behnam.notification.service;

import com.behnam.notification.domain.Confirmation;
import com.behnam.notification.domain.Notification;
import com.behnam.notification.exceptions.NotificationNotFound;
import com.behnam.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification save(Notification notification) {
        if (notification == null) {
            throw new NullPointerException("Null Notification could not be saved!");
        }

        // Broadcasts doesn't need destinations
        if (notification.getBroadCast()) {
            notification.setDestinations(null);
        }

        notification.setCreateDate(new Date());
        notificationRepository.save(notification);
        return notification;
    }


    public List<Notification> findToSends(Set<String> adminUsers) {
        List<Notification> notifications = notificationRepository.findToSends();
        return notifications.stream()
                .filter(n -> n.getConfirmations().stream().filter(
                        c -> c.getDecision().equals("CONFIRMED"))
                        .collect(Collectors.toSet()).stream().map(Confirmation::getUser).collect(Collectors.toSet()).containsAll(adminUsers))
                .collect(Collectors.toList());
    }

    public Notification findById(long id) {
        Optional<Notification> notification = notificationRepository.findById(id);
        if (notification.isPresent()) {
            return notification.get();
        } else {
            throw new NotificationNotFound("Given ID not found");
        }

    }

    public Notification confirm(Notification notification, String user) {
        return addConfirmationState(notification,user,"CONFIRMED");
    }

    public Notification confirm(Long notificationId, String user) {
        return confirm(findById(notificationId),user);
    }

    public Notification reject(Notification notification, String user) {
        return addConfirmationState(notification,user,"REJECTED");
    }

    public Notification reject(Long notificationId, String user) {
        return reject(findById(notificationId),user);
    }

    private Notification addConfirmationState(Notification notification, String user, String decision) {
        if(notification==null)
            throw new NullPointerException("Cannot add a decision to a null notification");
        if (notification.getConfirmations() == null) {
            notification.setConfirmations(new HashSet<>());
        }

        // If already confirmed/rejected change to the new value
        Optional<Confirmation> confirmation = notification.getConfirmations().stream().filter(c->c.getUser().equals(user)).findFirst();
        if(confirmation.isPresent()){
            confirmation.get().setDecision(decision);
        } else {
            notification.getConfirmations().add(Confirmation.builder()
                    .decision(decision)
                    .decisionDate(new Date())
                    .notification(notification)
                    .user(user)
                    .build());
        }
        return save(notification);

    }

    public void markAsSent(Notification notification) {
        notification.setSendingDate(new Date());
        notification.setState("SENT");
        save(notification);
    }
}
