package com.app.univchat.domain.school;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString
public class Facility {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String building; //건물

    private String name; //이름

    private String location; //위치

    private String time; //운영시간

    private String phone; //전화번호


}
