package com.studyolle.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@NamedEntityGraph(name="Event.withEnrollment", attributeNodes =  @NamedAttributeNode("enrollments"))
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

    @OneToMany(mappedBy = "event")
    private List<Enrollment> enrollments;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    public int NumOfFixedParticipants(){
        long count = this.enrollments.stream().filter(Enrollment::isAccepted).count();
        return (int)count;
    }

}
