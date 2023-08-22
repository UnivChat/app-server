package com.app.univchat.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Builder
@ToString
@Table(name = "inquiry")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquiryId;

    @Column(nullable = false)
    public String receiverEmail;

    @Column(nullable = false, columnDefinition = "TEXT")
    public String content;
}
