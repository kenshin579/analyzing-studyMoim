package com.studyolle.modules.event;

import com.studyolle.modules.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Enrollment findByEventAndAccount(Event event, Account account);

    boolean existsByEventAndAccount(Event event, Account account);

    List<Enrollment> findByEvent(Event event);
}
