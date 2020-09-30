package com.studyolle.modules.account.form;

import lombok.Data;

@Data
public class NotificationsForm {
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
}
