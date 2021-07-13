package com.behnam.notification;

import com.behnam.notification.config.AppConfig;
import com.behnam.notification.domain.Notification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfigTests {
    @Autowired
    AppConfig appConfig;
    @Test
    public void newLimitedNotification() {
        appConfig.notificationHandler().send(Notification.builder().id(Long.valueOf(10)).build());
    }
}
