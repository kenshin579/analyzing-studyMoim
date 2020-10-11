package com.studyolle.modules.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class NotificationService {
    private final NotificationRepository repository;

    public void markAsRead(List<Notification> notifications) {
        notifications.forEach(notification -> notification.setChecked(true));
        repository.saveAll(notifications);
    }
}
