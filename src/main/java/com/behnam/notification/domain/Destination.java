package com.behnam.notification.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "DESTINATIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Destination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Boolean sent;
    Boolean delivered;
    String destination;
    @ManyToOne(optional=false)
    @JoinColumn(name="NOTIFICATION_ID", nullable=false, updatable=false)
    Notification notification;
}
