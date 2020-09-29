package com.studyolle.event;

import com.studyolle.domain.Account;
import com.studyolle.domain.Enrollment;
import com.studyolle.domain.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;

    public Enrollment getEnrollment(Event event, Account account) {
        return enrollmentRepository.findByEventAndAccount(event, account);
    }

    public Enrollment getEnrollement(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId).orElseThrow();
    }
}
