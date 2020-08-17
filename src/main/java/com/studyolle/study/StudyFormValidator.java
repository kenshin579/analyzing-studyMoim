package com.studyolle.study;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
@RequiredArgsConstructor
@Component
public class StudyFormValidator implements Validator {
    private final StudyRepository studyRepository;
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(StudyForm.class);
    }

    @Override
    public void validate(Object object, Errors errors) {
        StudyForm studyForm = (StudyForm)object;
        if(studyRepository.existsByPath(studyForm.getPath())){
            errors.rejectValue("path","invalid.path","존재하는 스터디 경로입니다.");
        }
    }
}
