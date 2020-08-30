package com.studyolle.study;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class StudyForm {
    final static String VALID_PATH_PATTERN = "^[a-zㄱ-ㅎ가-힣0-9-_]{3,20}$";
    @NotBlank
    @Length(max=40)
    private String title;

    @NotBlank
    @Length(min=2, max=20)
    @Pattern(regexp = VALID_PATH_PATTERN)
    private String path;

    @NotBlank
    @Length(max = 100)
    private String shortDescription;

    @NotBlank
    private String fullDescription;
}
