package com.behnam.notification.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "NOTIFICATIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"destinations","confirmations"})
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Date createDate;
    Date sendingDate;
    String title;
    String text;
    Boolean needDelivery;
    Boolean broadCast;
    String state;
    @OneToMany(mappedBy = "notification",cascade = CascadeType.ALL)
    List<Destination> destinations;
    @OneToMany(mappedBy = "notification",cascade = CascadeType.ALL)
    Set<Confirmation> confirmations;
    String creator;


}
