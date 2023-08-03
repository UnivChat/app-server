package com.app.univchat.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class FCMConfig {
    @Value("${fcm.id}")
    private String id;

    @Value("${fcm.key}")
    private String key;

    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException, ParseException {

        ClassPathResource resource = new ClassPathResource("firebase/fcm-key.json");
        Path path = Paths.get(resource.getURI());

        JSONParser parser=new JSONParser();
        Reader reader=new FileReader(path.toString());
        JSONObject jsonObject=(JSONObject)parser.parse(reader);

        jsonObject.replace("private_key_id",id);
        jsonObject.replace("private_key",key);

        FileWriter file = new FileWriter(path.toString());
        file.write(jsonObject.toJSONString());
        file.flush();
        file.close();

        InputStream refreshToken=resource.getInputStream();


        FirebaseApp firebaseApp=null;
        List<FirebaseApp> firebaseAppList=FirebaseApp.getApps();

        if(firebaseAppList!=null && !firebaseAppList.isEmpty()) {
            for(FirebaseApp app:firebaseAppList) {
                if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
                    firebaseApp=app;
                }
            }
        }
        else {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(refreshToken))
                    .build();

            firebaseApp=FirebaseApp.initializeApp(options);
        }

        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
