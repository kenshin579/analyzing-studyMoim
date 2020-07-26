package com.studyolle.settings;

import com.studyolle.domain.Account;
import com.sun.istack.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ProfileForm {
    @Nullable
    private String bio;
    @Nullable
    private String personalUrl;
    @Nullable
    private String occupation;
    @Nullable
    private String livingArea;

    public ProfileForm(Account account) {
        this.bio = account.getBio();
        this.personalUrl = account.getPersonalUrl();
        this.occupation = account.getOccupation();
        this.livingArea = account.getLivingArea();
    }
}
