package com.studyolle.modules.account.form;

import com.studyolle.modules.zone.Zone;
import lombok.Data;

@Data
public class ZoneForm {
    //TODO validate 추가해서 whitelist에 없는 값이 들어왔을 때, 처리 안하도록 만들어야 함.
    private String zoneName;
    public String getCityName() {
        return zoneName.substring(zoneName.indexOf("(")+1, zoneName.indexOf(")"));
    }
    public String getLocalName(){
        return zoneName.substring(0, zoneName.indexOf("("));
    }
    public String getProvinceName(){
        return zoneName.substring(zoneName.indexOf("/")+1);
    }

    public Zone getZone(){
        return Zone.builder().city(this.getCityName()).localNameOfCity(this.getLocalName()).province(this.getProvinceName()).build();
    }
}
