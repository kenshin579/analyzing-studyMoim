package com.studyolle.settings.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ProfileForm {
    @Length(max=35)
    private String bio;

    private String personalUrl;

    private String occupation;

    private String livingArea;
}
