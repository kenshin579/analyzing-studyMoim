package com.studyolle.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NamedEntityGraph(
        name="Event.withEnrollment",
        attributeNodes = @NamedAttributeNode("enrollments")
)
@Setter @Getter @EqualsAndHashCode(of="id")
@Entity
public class Event {
    @Id @GeneratedValue
    private Long id;
    @ManyToOne
    private Study study;

    @ManyToOne
    private Account createBy;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;
    @Column(nullable = false)
    private LocalDateTime createdDateTime;
    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;
    @Column(nullable = false)
    private LocalDateTime startDateTime;
    @Column(nullable = false)
    private LocalDateTime endDateTime;
    private Integer limitOfEnrollments;

    @OrderBy("enrolledAt")
    @OneToMany(mappedBy = "event")
    private List<Enrollment> enrollments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    public int NumOfFixedParticipants(){
        long count = this.enrollments.stream().filter(Enrollment::isAccepted).count();
        return (int)count;
    }

    public void addEnrollment(Enrollment enrollment) {
        this.enrollments.add(enrollment);
        enrollment.setEvent(this);
    }

    public void removeEnrollment(Enrollment enrollment) {
        this.enrollments.remove(enrollment);
        enrollment.setEvent(null);
    }

    public boolean isAbleToAcceptEnroll() {
        return this.eventType == EventType.FCFS && this.getLimitOfEnrollments() > this.getNumberOfAcceptEnrollments();
    }

    private long getNumberOfAcceptEnrollments() {
        return this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }

    public boolean isEndEnrollmentDateTime(){
        return this.endEnrollmentDateTime.isBefore(LocalDateTime.now());
    }

    public boolean isClosed(){
        return this.endDateTime.isBefore(LocalDateTime.now());
    }

    public void updateFCFSMemberAccept() {
        for(int i =0; i< this.enrollments.size(); i++){
            if (isAbleToAcceptEnroll()){
                this.enrollments.get(i).setAccepted(true);
            }
        }
    }

    public void accept(Enrollment enrollment) {
        if (this.getEnrollments().contains(enrollment) && !enrollment.isAccepted()){
            this.getEnrollments().get(this.getEnrollments().indexOf(enrollment)).setAccepted(true);
            enrollment.setAccepted(true);
        }
    }

    public void reject(Enrollment enrollment) {
        if (this.getEnrollments().contains(enrollment) && enrollment.isAccepted()){
            this.getEnrollments().get(this.getEnrollments().indexOf(enrollment)).setAccepted(false);
            enrollment.setAccepted(false);
        }
    }
}
