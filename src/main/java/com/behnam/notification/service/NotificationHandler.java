package com.behnam.notification.service;

import com.behnam.notification.domain.Notification;

public interface NotificationHandler {
    public boolean send(Notification notification);
    public boolean checkDelivery(Notification notification);
}
