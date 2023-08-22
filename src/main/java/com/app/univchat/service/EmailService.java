package com.app.univchat.service;

import com.app.univchat.dto.MemberRes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String adminEmail;

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

    // 문의 접수 완료 메일 전송
    public void sendInquirySuccessMail(String userEmail, String inquiry) throws MessagingException {

        // 메일 제목 및 내용
        String title = "[CatChat] 접수하신 문의가 성공적으로 등록되었습니다.";
        String content = "";
        content += "<div style='margin:10%; padding:20px; background-color:#f5f5f5;'>";
        content += "<h2 style='color: #003091; text-align:center; padding: 10px;'>접수하신 문의가 성공적으로 등록되었습니다.</h2>";
        content += "<div style='background-color: white; padding: 20px; border-radius: 10px;'>";
        content += "<p style='font-size: 18px; color: #555;'>문의하신 내용은 아래와 같습니다.</p>";
        content += "<pre style='background-color: #f9f9f9; padding: 10px; border-radius: 5px; color: #003091;'>" + inquiry + "</pre>";
        content += "<p style='font-size: 18px; color: #555;'>빠른 시일 내 문의해주신 내용에 답변 메일을 보내드리겠습니다. 감사합니다.</p>";
        content += "</div>";
        content += "</div>";

        // 메일 전송
        MimeMessage message = emailSender.createMimeMessage();
        message.setFrom(adminEmail);
        message.addRecipients(MimeMessage.RecipientType.TO, userEmail);
        message.setSubject(title);
        message.setText(content, "utf-8", "html");
        emailSender.send(message);
    }
}