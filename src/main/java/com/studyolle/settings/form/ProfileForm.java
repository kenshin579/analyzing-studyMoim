package com.studyolle.settings.form;

import com.studyolle.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@Data
public class ProfileForm {
    @Length(max=35)
    private String bio;

    private String personalUrl;

    private String occupation;

    private String livingArea;

    public ProfileForm(Account account) {
        this.bio = account.getBio();
        this.personalUrl = account.getPersonalUrl();
        this.occupation = account.getOccupation();
        this.livingArea = account.getLivingArea();
    }
}
