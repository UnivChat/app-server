package com.app.univchat.service;

import com.app.univchat.base.BaseException;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.dto.ChatReq;
import com.app.univchat.dto.ChatRes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Service
public class CipherService {

    @Value("${hash.seed}")
    private String HASH_SEED;
    @Value("${hash.type}")
    private String HASH_TYPE;
    @Value("${cipher.type}")
    private String CIPHER_TYPE;
    @Value("${cipher.mode}")
    private String CIPHER_MODE;
    private final static byte[] IV = new byte[16];

    public ChatReq encryptChat(ChatReq chat) {

        try {

        // 비밀키 생성
        MessageDigest secret = MessageDigest.getInstance(HASH_TYPE);
        byte[] secretDigest = secret.digest((HASH_SEED + chat.getMemberNickname()).getBytes(StandardCharsets.UTF_8));

        // 암호화 준비
        SecretKeySpec keySpec = new SecretKeySpec(secretDigest, CIPHER_TYPE);
        IvParameterSpec ivSpec = new IvParameterSpec(IV);

        // 암호화 적용
        Cipher cipher = Cipher.getInstance(CIPHER_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] cipherChat = cipher.doFinal(chat.getMessageContent().getBytes("UTF-8"));

        // base64 인코딩
        String encodedCipherChat = new String(Base64.getEncoder().encode(cipherChat), StandardCharsets.UTF_8);
        chat.setMessageContent(encodedCipherChat);
        return chat;

        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.CHATTING_ENCRYPTION_FAIL_ERROR);
        }
    }

    public ChatRes decryptChat(ChatRes chat) {

        try {

        // 비밀키 생성
        MessageDigest secret = MessageDigest.getInstance(HASH_TYPE);
        byte[] secretDigest = secret.digest((HASH_SEED + chat.getMemberNickname()).getBytes(StandardCharsets.UTF_8));

        // base64 디코딩
        byte[] decodedCipherChat = Base64.getDecoder().decode(chat.getMessageContent().getBytes("UTF-8"));

        // 복호화 준비
        SecretKeySpec keySpec = new SecretKeySpec(secretDigest, CIPHER_TYPE);
        IvParameterSpec ivSpec = new IvParameterSpec(IV);

        // 복호화 적용
        Cipher cipher = Cipher.getInstance(CIPHER_MODE);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        String plainChat = new String(cipher.doFinal(decodedCipherChat), StandardCharsets.UTF_8);
        chat.setMessageContent(plainChat);

        return chat;

        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.CHATTING_DECRYPTION_FAIL_ERROR);
        }
    }
}
