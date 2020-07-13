package com.studyolle.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @EqualsAndHashCode
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {
    //로그인
    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    private String email;

    private String password;

    // 이메일 인증이 된 계정인지 확인 플레그.
    private boolean emailVerified;

    //이메일 인증 시 사용할 토큰 값.
    private String emailCheckToken;

    //인증을 거치면 그 때, 가입이 된 걸로 즉, 가입날
    private LocalDateTime joinedAt;

    //프로필 정보에 사용할 정보
    private String bio;

    private String personalUrl;

    private String occupation;

    private String livingArea;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String ProfileImg;


    //스터디가 만들어 졌다는 것을 이메일로 알람을 받을 것인지 체크
    private boolean alarmStudyCreationToEmail;

    //스터디가 만들어 졌다는 것을 웹으로 알람을 받을 것인지 체크
    private boolean alarmStudyCreationToWeb;

    //스터디 가입 신청 결과를 이메일로 받을 것인지.
    private boolean alarmApplyResultToEmail;

    //스터디 가입 신청 결과를 웹로 받을 것인지.
    private boolean alarmApplyResultToWeb;

    //스터디 변경 정보를 이메로 받을 것인지.
    private boolean alarmUpdateInfoToEmail;

    //스터디 변경 정보를 웹로 받을 것인지.
    private boolean alarmUpdateInfoToWeb;

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }

    public void CompleteSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.getEmailCheckToken().equals(token);
    }
}
