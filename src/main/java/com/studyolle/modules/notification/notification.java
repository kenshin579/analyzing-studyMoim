package com.studyolle.modules.notification;

import com.studyolle.modules.account.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter @Setter
@EqualsAndHashCode(of="id")
@Entity
public class notification {
    @Id @GeneratedValue
    private Long id;

    private String title;

    private String link;

    private String message;

    @ManyToOne
    private Account account;

    private LocalDateTime createdLocalDateTime;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

}
