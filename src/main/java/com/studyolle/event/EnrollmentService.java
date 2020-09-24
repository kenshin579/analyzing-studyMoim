package com.studyolle.event;

import com.studyolle.domain.Account;
import com.studyolle.domain.Enrollment;
import com.studyolle.domain.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;

    public void addFCFSEnrollment(Event event, Account account) {
        Enrollment enrollment = new Enrollment();
        enrollment.setAccount(account);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setEvent(event);
        enrollment.setAccepted(true);
        enrollmentRepository.save(enrollment);
    }
    public boolean alreadyEnroll(Event event, Account account){
        return event.getEnrollments().contains(account);
    }

    public void removeFCFSEnrollment(Event event, Account account) {
        enrollmentRepository.delete(enrollmentRepository.findByEventAndAccount(event, account));
    }
}
