package com.app.univchat.service;

import com.app.univchat.dto.MemberRes;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;


    //회원가입 시 메일 인증 코드
    public MemberRes.EmailAuthRes sendEmail(String email) throws MessagingException, UnsupportedEncodingException {
        MemberRes.EmailAuthRes emailAuthRes = new MemberRes.EmailAuthRes();

        Random random = new Random();
        int checkCode = random.nextInt(888888) + 111111;

        String sender = "yujinkwon.dev@gmail.com"; //보내는 사람
        String receiver = email; //받는 사람
        String title = "'CATCHAT' 회원가입 인증 번호"; //제목
        String text = ""; //내용
        text += "<div style='margin:10%;'>";
        text += "<h2> 안녕하세요 CATCHAT입니다</h2>";
        text += "<br>";
        text += "<p>아래 코드를 회원가입 창으로 돌아가 입력해주세요<p>";
        text += "<br>";
        text += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        text += "<h3 style='color:green;'>회원가입 인증 코드입니다.</h3>";
        text += "<div style='font-size:130%'>";
        text += "CODE : <strong>";
        text += checkCode + "</strong><div><br/> ";
        text += "</div>";


        MimeMessage message = emailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, receiver); //받는 이메일 설정
        message.setSubject(title); //제목 설정
        message.setFrom(sender); //보내는 사람
        message.setText(text, "utf-8", "html");  //내용
        emailSender.send(message);

        emailAuthRes.setCheckCode(checkCode);
        return emailAuthRes;
    }


}