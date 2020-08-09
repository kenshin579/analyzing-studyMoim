package com.studyolle.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of ="id")
@Entity
public class Zone {
    @Id @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String city;  //영문 도시 이름
    @Column(nullable = false)
    private String localNameOfCity; //한국어 도시 이름
    @Column(nullable = true)
    private String province; //주이름

    @Override
    public String toString() {
        return  localNameOfCity +"("+city+")/" +province;
    }
}
