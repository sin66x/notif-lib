package com.behnam.notification.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "CONFIRMATIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Confirmation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String user;
    @ManyToOne
    @JoinColumn(name="NOTIFICATION_ID", nullable=false, updatable=false)
    Notification notification;
    String decision;
    Date decisionDate;
}
