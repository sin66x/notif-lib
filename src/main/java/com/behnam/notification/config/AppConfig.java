package com.behnam.notification.config;

import com.behnam.notification.domain.Notification;
import com.behnam.notification.service.NotificationHandler;
import com.behnam.notification.service.UserHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Configuration
@ConditionalOnClass(NotificationHandler.class)
public class AppConfig {
    private int checkingInterval=1000;

    public int getCheckingInterval(){
        return checkingInterval;
    }

    @Bean(name = "notificationHandler")
    @ConditionalOnMissingBean
    public NotificationHandler notificationHandler() {
        return new NotificationHandler() {
            @Override
            public boolean send(Notification obj) {
                System.out.println("Sending id: "+obj.getId());
                return true;
            }

            @Override
            public boolean checkDelivery(Notification obj) {
                System.out.println("Checking delivery id: "+obj.getId());
                return true;
            }
        };
    }

    @Bean(name = "userHandler")
    @ConditionalOnMissingBean
    public UserHandler userHandler() {
        return new UserHandler() {
            @Override
            public Set<String> getAdmins() {
                Set<String> adminUsers;
                adminUsers = new HashSet<>();
                adminUsers.add("ROBBERT_BARATHEON");
                adminUsers.add("JOHN_SNOW");
                return adminUsers;
            }
        };
    }

}
