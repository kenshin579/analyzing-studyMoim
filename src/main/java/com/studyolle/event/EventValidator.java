package com.studyolle.event;


import com.studyolle.domain.Event;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
public class EventValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return EventForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {
        EventForm eventForm = (EventForm)object;
        if(!eventForm.getEndEnrollmentDateTime().isAfter(LocalDateTime.now())){
            errors.rejectValue("endEnrollmentDateTime","wrong.datetime","접수 종료일의 시기가 안 맞습니다.");
        }

        if(!eventForm.getEndDateTime().isAfter(eventForm.getStartDateTime())){
            errors.rejectValue("endDateTime","wrong.datetime","모임 종료 일시가 안 맞습니다.");
        }

        if(!eventForm.getStartDateTime().isBefore(eventForm.getEndDateTime()) || !eventForm.getStartDateTime().isAfter(LocalDateTime.now()) || !eventForm.getStartDateTime().isAfter(eventForm.getEndEnrollmentDateTime())){
            errors.rejectValue("startDateTime","wrong.datetime","모임 시작일의 일시가 맞지 않습니다.");
        }

    }

    public void validateUpdateForm(EventForm eventForm, Event event, Errors errors) {
        if(event.getLimitOfEnrollments() > eventForm.getLimitOfEnrollments()){
            errors.rejectValue("limitOfEnrollments","wrong.value","모임 신청 인원보다 적은 모집 인원으로 변경할 수 없습니다.");
        }
    }
}
