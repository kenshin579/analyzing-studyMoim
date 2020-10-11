package com.studyolle.modules.study.event;

import com.studyolle.infra.config.AppProperties;
import com.studyolle.infra.mail.EmailMessage;
import com.studyolle.infra.mail.EmailService;
import com.studyolle.modules.account.Account;
import com.studyolle.modules.notification.Notification;
import com.studyolle.modules.notification.NotificationRepository;
import com.studyolle.modules.notification.NotificationType;
import com.studyolle.modules.study.Study;
import com.studyolle.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Async
@Transactional
@RequiredArgsConstructor
@Component
public class StudyUpdateListener {
    private final StudyRepository studyRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final AppProperties appProperties;
    private final TemplateEngine templateEngine;

    public void handleStudyUpdateEvent(StudyUpdateEvent studyUpdateEvent){
        Study study = studyRepository.findStudyWithManagersAndMembersById(studyUpdateEvent.getStudy().getId());
        Set<Account> accounts = new HashSet<>();
        accounts.addAll(study.getManagers());
        accounts.addAll(study.getMembers());
        accounts.forEach(account -> {
            if(account.isAlarmUpdateInfoToEmail()){
                sendStudyUpdateEmail(studyUpdateEvent, study, account);
            }
            if(account.isAlarmUpdateInfoToWeb()){
                saveStudyUpdateNotification(studyUpdateEvent, study, account);
            }
        });
    }

    private void sendStudyUpdateEmail(StudyUpdateEvent studyUpdateEvent, Study study, Account account) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/study/"+ study.getPath());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message", studyUpdateEvent.getMessage());
        context.setVariable("host", appProperties.getHost());
        context.setVariable("content", study.getShortDescription());
        String message = templateEngine.process("mail/studyCreateMessage", context);
        EmailMessage emailMessage = EmailMessage.builder()
                .subject("스터디모임, '"+study.getTitle()+"'해당 스터디의 정보가 업데이트되었습니다.")
                .to(account.getEmail())
                .message(message)
                .build();
        emailService.sendEmail(emailMessage);
    }

    private void saveStudyUpdateNotification(StudyUpdateEvent studyUpdateEvent, Study study, Account account) {
        Notification notification = new Notification();
        notification.setTitle(study.getTitle());
        notification.setLink("/study/"+ study.getPath());
        notification.setMessage(studyUpdateEvent.getMessage());
        notification.setCreatedLocalDateTime(LocalDateTime.now());
        notification.setNotificationType(NotificationType.STUDY_UPDATE);
        notification.setAccount(account);
        notification.setChecked(false);
        notificationRepository.save(notification);
    }
}
