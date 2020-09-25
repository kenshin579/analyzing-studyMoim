package com.studyolle.event;

import com.studyolle.domain.Account;
import com.studyolle.domain.Enrollment;
import com.studyolle.domain.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
@Transactional
@RequiredArgsConstructor
@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;

    public void addFCFSEnrollment(Event event, Account account, boolean joinStat) {
        Enrollment enrollment = new Enrollment();
        enrollment.setAccount(account);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setEvent(event);
        enrollment.setAccepted(joinStat);
        enrollmentRepository.save(enrollment);
    }
    public boolean alreadyEnroll(Event event, Account account){
        return event.getEnrollments().contains(account);
    }

    public void removeFCFSEnrollment(Enrollment enrollment) {
        enrollmentRepository.delete(enrollmentRepository.findById(enrollment.getId()).orElseThrow());
    }

    public void updateEnrollStat(List<Enrollment> enrollments, int limitOfEnrollment) {
        for(int i =0; i< limitOfEnrollment;i++){
            enrollments.get(i).setAccepted(true);
        }
    }

    public Enrollment getEnrollmentId(Event event, Account account) {
        return enrollmentRepository.findByEventAndAccount(event, account);
    }
}
