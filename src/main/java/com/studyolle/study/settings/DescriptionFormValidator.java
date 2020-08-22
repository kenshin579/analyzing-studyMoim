package com.studyolle.study.settings;

import com.studyolle.study.settings.Form.DescriptionForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class DescriptionFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(DescriptionForm.class);
    }

    @Override
    public void validate(Object object, Errors errors) {
        DescriptionForm descriptionForm = (DescriptionForm)object;
        if(descriptionForm.getShortDescription().length() > 100){
            errors.rejectValue("shortDescription","invalid.shortDescription","100자 미만으로 작성해주세요.");
        }
    }
}
