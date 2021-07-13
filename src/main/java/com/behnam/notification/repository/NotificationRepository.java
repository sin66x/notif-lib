package com.behnam.notification.repository;

import com.behnam.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {

    @Query(value = "from Notification n where n.sendingDate<CURRENT_TIMESTAMP and n.state is null")
    List<Notification> findToSends();
}
