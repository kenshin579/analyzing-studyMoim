package com.studyolle.study;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class DescriptionForm {
    @Length(max = 100)
    private String shortDescription;

    private String fullDescription;
}
