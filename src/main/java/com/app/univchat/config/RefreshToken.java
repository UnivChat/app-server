package com.app.univchat.config;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@NoArgsConstructor
@Table(name="refresh_token")
@Entity
public class RefreshToken {

    @Id
    @Column(name = "rt_key")
    private String key; // id 값

    @Column(name = "rt_value")
    private String value;   // Refresh Token

    @Builder
    public RefreshToken(String key, String value) {
        this.key=key;
        this.value=value;
    }

    public RefreshToken updateValue(String token) {
        this.value = token;
        return this;
    }

    // 만료된 토큰들을 삭제해주는 작업 위해 생성/수정 시간 컬럼 추가?
}
