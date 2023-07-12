package com.app.univchat.domain;

import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString
public class ClassRoom {

    @Id
    @Column(unique = true, nullable = false)
    private String classNumber; //과목번호

    @Column(nullable = false)
    private String className; //교과목명

    private String professor; //교수님

    @Column(nullable = false)
    private String classTime; //시간표

    @Column(nullable = false)
    private String  section;//이수구분

    @Column(nullable = false)
    private int credit; //학점

    private String etc; //비고

}
