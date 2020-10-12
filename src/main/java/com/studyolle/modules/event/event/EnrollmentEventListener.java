package com.studyolle.modules.event.event;

import com.studyolle.infra.config.AppProperties;
import com.studyolle.infra.mail.EmailMessage;
import com.studyolle.infra.mail.EmailService;
import com.studyolle.modules.account.Account;
import com.studyolle.modules.notification.Notification;
import com.studyolle.modules.notification.NotificationRepository;
import com.studyolle.modules.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class EnrollmentEventListener {
    private final NotificationRepository repository;
    private final TemplateEngine templateEngine;
    private final EmailService emailService;
    private final AppProperties appProperties;
    @EventListener
    public void handlerEnrollmentEvent(EnrollmentEvent enrollmentEvent){
        //TODO 모임 참여 승인 또는 거절을 당하는 알림을 받는 사람은 모임을 신청한 사람.
        Account account = enrollmentEvent.getEnrollment().getAccount();

        if(account.isAlarmApplyResultToEmail()){
            emailAlarm(enrollmentEvent, account);

        }
        if(account.isAlarmApplyResultToWeb()){
            webAlarm(enrollmentEvent, account);
        }

    }

    private void emailAlarm(EnrollmentEvent enrollmentEvent, Account account) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/study/"+enrollmentEvent.getEnrollment().getEvent().getStudy().getPath()+"/events/"+enrollmentEvent.getEnrollment().getEvent().getId());
        context.setVariable("linkName", enrollmentEvent.getEnrollment().getEvent().getTitle());
        context.setVariable("message", enrollmentEvent.getMessage());
        context.setVariable("host", appProperties.getHost());
        context.setVariable("content", enrollmentEvent.getMessage());
        String message = templateEngine.process("mail/studyCreateMessage", context);
        EmailMessage emailMessage = EmailMessage.builder()
                .subject(enrollmentEvent.getMessage())
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    private void webAlarm(EnrollmentEvent enrollmentEvent, Account account) {
        Notification notification = new Notification();
        notification.setTitle(enrollmentEvent.getMessage());
        notification.setLink("/study/"+enrollmentEvent.getEnrollment().getEvent().getStudy().getPath()+"/events/"+enrollmentEvent.getEnrollment().getEvent().getId());
        notification.setMessage(enrollmentEvent.getMessage());
        notification.setCreatedLocalDateTime(LocalDateTime.now());
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.EVENT_ENROLLMENT);
        notification.setChecked(false);
        repository.save(notification);
    }
}
