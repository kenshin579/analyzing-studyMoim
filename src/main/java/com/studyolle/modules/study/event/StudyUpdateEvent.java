package com.studyolle.modules.study.event;

import com.studyolle.modules.study.Study;
import lombok.Getter;

@Getter
public class StudyUpdateEvent{
    private Study study;
    private String message;
    public StudyUpdateEvent(Study study, String message) {
        this.study = study;
        this.message = message;
    }
}
