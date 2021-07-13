package com.behnam.notification;

import com.behnam.notification.domain.Destination;
import com.behnam.notification.domain.Notification;
import com.behnam.notification.exceptions.NotificationNotFound;
import com.behnam.notification.repository.NotificationRepository;
import com.behnam.notification.service.NotificationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class NotificationTests {
    private final int MILISECONDS_TO_SEND = 10000;
    private final int DEST_TESTNO = 5;
    private final String creatorUser = "BEHNAM";

    @Autowired
    NotificationRepository notificationRepository;
    NotificationService notificationService;

    Notification broadcastNotification;
    Notification limitedNotification;
    List<Destination> destinations;
    Set<String> adminUsers;


    private void initAdmins() {
        adminUsers = new HashSet<>();
        adminUsers.add("ROBBERT_BARATHEON");
        adminUsers.add("JOHN_SNOW");

    }

    private void initBroadCast() {
        broadcastNotification = Notification.builder()
                .creator(creatorUser)
                .broadCast(true)
                .needDelivery(false)
                .sendingDate(new Date((new Date()).getTime() + MILISECONDS_TO_SEND))
                .title("BROADCAST")
                .text("This is a Broadcast Test")
                .build();
    }

    private void initLimited() {
        destinations = new ArrayList<>();
        limitedNotification = Notification.builder()
                .creator(creatorUser)
                .broadCast(false)
                .needDelivery(true)
                .sendingDate(new Date((new Date()).getTime()))
                .title("LIMITED")
                .text("This is a limited destination test")
                .destinations(destinations)
                .build();

        for (int i = 0; i < DEST_TESTNO; i++) {
            destinations.add(
                    Destination.builder()
                            .destination("reciver" + i + "@mail.com")
                            .notification(limitedNotification)
                            .build()
            );
        }
    }

    @Before
    public void setUp() throws Exception {
        initAdmins();
        initBroadCast();
        initLimited();
        notificationService = new NotificationService(notificationRepository);

    }

    @Test
    public void newLimitedNotification() {
        Notification notification = notificationService.save(limitedNotification);
        Assert.assertNotNull(notification);
        Assert.assertNotNull(notification.getCreator());
        Assert.assertNotNull(notification.getDestinations());
        Assert.assertNotEquals(notification.getId(), Long.valueOf(0));
    }

    @Test
    public void newBroadcastNotification() {
        notificationService.save(limitedNotification);
        Assert.assertNotNull(limitedNotification.getDestinations());
        Assert.assertFalse(limitedNotification.getDestinations().size() == 0);
        Assert.assertTrue(limitedNotification.getDestinations().get(0).getId() != 0);
    }

    @Test
    public void findToSendNotifications() {
        List<Notification> notifications = notificationService.findToSends(adminUsers);
        Assert.assertNotNull(notifications);
        Assert.assertTrue(notifications.size() == 2);
        for (Notification not : notifications) {
            Assert.assertNotEquals(not.getId(),Long.valueOf(1002));
        }
    }

    @Test
    public void findById() {
        Notification notification =notificationService.findById(1002);
        Assert.assertNotNull(notification);
        Assert.assertEquals(notification.getTitle(),"Hear Me Roar");
    }

    @Test
    public void markAsSent(){
        Notification notification = notificationService.save(broadcastNotification);
        Assert.assertNotNull(notification);
        Assert.assertEquals(notification.getTitle(),"BROADCAST");
        Assert.assertNotNull(notification.getId());
        Assert.assertNotEquals(notification.getId(),Long.valueOf(0));
        Assert.assertNotNull(notification.getSendingDate());

    }

    @Test(expected = NotificationNotFound.class)
    public void findByIdException() {
        Notification notification =notificationService.findById(-1);
    }

    @Test
    public void confirmById(){
        Notification notification = notificationService.confirm(Long.valueOf(1002),"JAIMIE_LANNISTER");
        Assert.assertNotNull(notification);
        Assert.assertEquals(notification.getConfirmations().size(),2);
        Assert.assertEquals(notification.getConfirmations().stream().filter(c->c.getUser().equals("JAIMIE_LANNISTER")).count(),1);
        Assert.assertTrue(notification.getConfirmations().stream().filter(c->c.getUser().equals("JAIMIE_LANNISTER")).findFirst().get().getDecision().equals("CONFIRMED"));
        Assert.assertNotEquals(notification.getConfirmations().stream().filter(c->c.getUser().equals("JAIMIE_LANNISTER")).findFirst().get().getId(),Long.valueOf(0));
    }

    @Test
    public void confirm(){
        Notification notification = notificationService.confirm(limitedNotification,"JAIMIE_LANNISTER");
        Assert.assertNotNull(notification);
        Assert.assertEquals(notification.getConfirmations().stream().filter(c->c.getUser().equals("JAIMIE_LANNISTER")).count(),1);
        Assert.assertTrue(notification.getConfirmations().stream().filter(c->c.getUser().equals("JAIMIE_LANNISTER")).findFirst().get().getDecision().equals("CONFIRMED"));
        Assert.assertNotEquals(notification.getConfirmations().stream().filter(c->c.getUser().equals("JAIMIE_LANNISTER")).findFirst().get().getId(),Long.valueOf(0));
    }

    @Test
    public void changeDecision(){
        Notification notification = notificationService.reject(Long.valueOf(1002),"ROBBERT_BARATHEON");
        Assert.assertNotNull(notification);
        Assert.assertEquals(notification.getConfirmations().stream().filter(c->c.getUser().equals("ROBBERT_BARATHEON")).count(),1);
        Assert.assertTrue(notification.getConfirmations().stream().filter(c->c.getUser().equals("ROBBERT_BARATHEON")).findFirst().get().getDecision().equals("REJECTED"));
        Assert.assertNotEquals(notification.getConfirmations().stream().filter(c->c.getUser().equals("ROBBERT_BARATHEON")).findFirst().get().getId(),Long.valueOf(0));
        notification = notificationService.confirm(Long.valueOf(1002),"ROBBERT_BARATHEON");
        Assert.assertNotNull(notification);
        Assert.assertEquals(notification.getConfirmations().stream().filter(c->c.getUser().equals("ROBBERT_BARATHEON")).count(),1);
        Assert.assertTrue(notification.getConfirmations().stream().filter(c->c.getUser().equals("ROBBERT_BARATHEON")).findFirst().get().getDecision().equals("CONFIRMED"));
        Assert.assertNotEquals(notification.getConfirmations().stream().filter(c->c.getUser().equals("ROBBERT_BARATHEON")).findFirst().get().getId(),Long.valueOf(0));

    }

    @Test
    public void allToghetherSuccess(){
        notificationService.save(limitedNotification);
        Assert.assertNotNull(limitedNotification.getDestinations());
        Assert.assertFalse(limitedNotification.getDestinations().size() == 0);
        Assert.assertTrue(limitedNotification.getDestinations().get(0).getId() != 0);

        Notification notification = notificationService.confirm(limitedNotification,"ROBBERT_BARATHEON");
        Assert.assertNotNull(notification);
        Assert.assertEquals(notification.getConfirmations().stream().filter(c->c.getUser().equals("ROBBERT_BARATHEON")).count(),1);
        Assert.assertTrue(notification.getConfirmations().stream().filter(c->c.getUser().equals("ROBBERT_BARATHEON")).findFirst().get().getDecision().equals("CONFIRMED"));
        Assert.assertNotEquals(notification.getConfirmations().stream().filter(c->c.getUser().equals("ROBBERT_BARATHEON")).findFirst().get().getId(),Long.valueOf(0));

        notification = notificationService.confirm(limitedNotification,"JAIMIE_LANNISTER");
        Assert.assertNotNull(notification);
        Assert.assertEquals(notification.getConfirmations().stream().filter(c->c.getUser().equals("JAIMIE_LANNISTER")).count(),1);
        Assert.assertTrue(notification.getConfirmations().stream().filter(c->c.getUser().equals("JAIMIE_LANNISTER")).findFirst().get().getDecision().equals("CONFIRMED"));
        Assert.assertNotEquals(notification.getConfirmations().stream().filter(c->c.getUser().equals("JAIMIE_LANNISTER")).findFirst().get().getId(),Long.valueOf(0));

        notification = notificationService.confirm(limitedNotification,"JOHN_SNOW");
        Assert.assertNotNull(notification);
        Assert.assertEquals(notification.getConfirmations().stream().filter(c->c.getUser().equals("JOHN_SNOW")).count(),1);
        Assert.assertTrue(notification.getConfirmations().stream().filter(c->c.getUser().equals("JOHN_SNOW")).findFirst().get().getDecision().equals("CONFIRMED"));
        Assert.assertNotEquals(notification.getConfirmations().stream().filter(c->c.getUser().equals("JOHN_SNOW")).findFirst().get().getId(),Long.valueOf(0));

        List<Notification> notifications = notificationService.findToSends(adminUsers);
        Assert.assertNotNull(notifications);
        Assert.assertTrue(notifications.stream().filter(n->n.getTitle().equals("LIMITED")).findFirst().isPresent());
    }

    @Test
    public void allToghetherFail(){
        notificationService.save(limitedNotification);
        Assert.assertNotNull(limitedNotification.getDestinations());
        Assert.assertFalse(limitedNotification.getDestinations().size() == 0);
        Assert.assertTrue(limitedNotification.getDestinations().get(0).getId() != 0);

        Notification notification = notificationService.reject(limitedNotification,"ROBBERT_BARATHEON");
        Assert.assertNotNull(notification);
        Assert.assertEquals(notification.getConfirmations().stream().filter(c->c.getUser().equals("ROBBERT_BARATHEON")).count(),1);
        Assert.assertTrue(notification.getConfirmations().stream().filter(c->c.getUser().equals("ROBBERT_BARATHEON")).findFirst().get().getDecision().equals("REJECTED"));
        Assert.assertNotEquals(notification.getConfirmations().stream().filter(c->c.getUser().equals("ROBBERT_BARATHEON")).findFirst().get().getId(),Long.valueOf(0));

        notification = notificationService.confirm(limitedNotification,"JAIMIE_LANNISTER");
        Assert.assertNotNull(notification);
        Assert.assertEquals(notification.getConfirmations().stream().filter(c->c.getUser().equals("JAIMIE_LANNISTER")).count(),1);
        Assert.assertTrue(notification.getConfirmations().stream().filter(c->c.getUser().equals("JAIMIE_LANNISTER")).findFirst().get().getDecision().equals("CONFIRMED"));
        Assert.assertNotEquals(notification.getConfirmations().stream().filter(c->c.getUser().equals("JAIMIE_LANNISTER")).findFirst().get().getId(),Long.valueOf(0));

        notification = notificationService.confirm(limitedNotification,"JOHN_SNOW");
        Assert.assertNotNull(notification);
        Assert.assertEquals(notification.getConfirmations().stream().filter(c->c.getUser().equals("JOHN_SNOW")).count(),1);
        Assert.assertTrue(notification.getConfirmations().stream().filter(c->c.getUser().equals("JOHN_SNOW")).findFirst().get().getDecision().equals("CONFIRMED"));
        Assert.assertNotEquals(notification.getConfirmations().stream().filter(c->c.getUser().equals("JOHN_SNOW")).findFirst().get().getId(),Long.valueOf(0));

        List<Notification> notifications = notificationService.findToSends(adminUsers);
        Assert.assertNotNull(notifications);
        Assert.assertFalse(notifications.stream().filter(n->n.getTitle().equals("LIMITED")).findFirst().isPresent());
    }
}
