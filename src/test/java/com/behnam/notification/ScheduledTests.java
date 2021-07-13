package com.behnam.notification;

import com.behnam.notification.config.AppConfig;
import com.behnam.notification.domain.Notification;
import com.behnam.notification.service.NotificationService;
import com.behnam.notification.service.ScheduleService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScheduledTests {

    @Autowired
    AppConfig appConfig;

    ScheduleService scheduleService;
    NotificationService notificationServiceMock;
    Set<String> adminUsers;
    List<Notification> notifications;
    private final String creatorUser = "BEHNAM";

    private void initAdmins() {
        adminUsers = new HashSet<>();
        adminUsers.add("ROBBERT_BARATHEON");
        adminUsers.add("JOHN_SNOW");
    }

    private void initNotifications() {
        notifications = new ArrayList<>();
        notifications.add(
                Notification.builder()
                        .creator(creatorUser)
                        .broadCast(true)
                        .needDelivery(false)
                        .sendingDate(new Date((new Date()).getTime()))
                        .title("BROADCAST")
                        .text("This is a Broadcast Test")
                        .build());
    }


    @Before
    public void setUp() throws Exception {
        initNotifications();
        notificationServiceMock = Mockito.mock(NotificationService.class);
        Mockito.when(notificationServiceMock.findToSends(appConfig.userHandler().getAdmins())).thenReturn(notifications);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                notifications.get(0).setId(Long.valueOf(1));
                return null;
            }
        }).when(notificationServiceMock).markAsSent(notifications.get(0));

        scheduleService = new ScheduleService(notificationServiceMock,appConfig);
    }

    @Test
    public void send() {
        scheduleService.send(notifications.get(0));
        Assert.assertNotNull(notifications.get(0));
        Assert.assertEquals(notifications.get(0).getTitle(),"BROADCAST");
        Assert.assertNotNull(notifications.get(0).getId());
        Assert.assertNotEquals(notifications.get(0).getId(),Long.valueOf(0));
        Assert.assertNotNull(notifications.get(0).getSendingDate());
    }

    @Test
    public void findAndSend() {
        scheduleService.findAndSend();
        Assert.assertNotNull(notifications.get(0));
        Assert.assertEquals(notifications.get(0).getTitle(),"BROADCAST");
        Assert.assertNotNull(notifications.get(0).getId());
        Assert.assertNotEquals(notifications.get(0).getId(),Long.valueOf(0));
        Assert.assertNotNull(notifications.get(0).getSendingDate());
    }
}
