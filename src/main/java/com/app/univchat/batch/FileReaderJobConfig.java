package com.app.univchat.batch;

import com.app.univchat.dto.ClassRoomDto;
import com.app.univchat.dto.school.PhoneDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FileReaderJobConfig {

        private final JobBuilderFactory jobBuilderFactory;
        private final StepBuilderFactory stepBuilderFactory;
        private final CsvReader csvReader;
        private final CsvClassWriter csvClassWriter;
        private final CsvPhoneWriter csvPhoneWriter;

    private static final int chunkSize = 1000; //데이터 처리할 row size


    /**
     * Job - 배치 작업 단위, 여러 step이 존재할 수 있음
     * 클래스 저장 Job
     */
    @Bean
    public Job csvFileReaderJob(){
        return jobBuilderFactory.get("csvFileReaderJob")
                .start(csvFileReaderStep())
                .build();
    }

    /**
     * 연락망 저장 Job
     */
    @Bean
    public Job csvPhoneReaderJob(){
        return jobBuilderFactory.get("csvPhoneReaderJob")
                .start(csvPhoneReaderStep())
                .build();
    }

    @Bean
    public Step csvFileReaderStep(){
        return stepBuilderFactory.get("csvFileReaderStep")
                .<ClassRoomDto, ClassRoomDto>chunk(chunkSize) //<reader에서 읽을 타입, writer에 넘겨줄 타입>
                .reader(csvReader.csvFileReader())
                .writer(csvClassWriter)
//                .allowStartIfComplete(true) //JobInstance COMPLETED 상태여도 재시작
                .build();
    }

    @Bean
    public Step csvPhoneReaderStep(){
        return stepBuilderFactory.get("csvPhoneReaderStep")
                .<PhoneDto, PhoneDto>chunk(chunkSize)
                .reader(csvReader.csvPhoneReader())
                .writer(csvPhoneWriter)
//                .allowStartIfComplete(true)
                .build();

    }


}
