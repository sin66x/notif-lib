package com.behnam.notification.service;

import com.behnam.notification.config.AppConfig;
import com.behnam.notification.domain.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService extends Thread{
    NotificationService notificationService;
    AppConfig appConfig;
    @Autowired
    public ScheduleService(NotificationService notificationService,AppConfig appConfig){
        this.notificationService = notificationService;
        this.appConfig = appConfig;
    }

    @Override
    public void run(){
        while(true){
            findAndSend();
            checkDeliveries();
            try {
                Thread.sleep(appConfig.getCheckingInterval());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkDeliveries() {
        //TODO: Not implemented Yet
    }

    public void findAndSend() {
        List<Notification> notificationList= notificationService.findToSends(appConfig.userHandler().getAdmins());
        notificationList.forEach( n -> send(n));
    }

    public void send(Notification notification) {
        if(appConfig.notificationHandler().send(notification)){
            notificationService.markAsSent(notification);
        }
    }
}
