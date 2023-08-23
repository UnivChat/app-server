package com.app.univchat.batch;

import com.app.univchat.batch.writer.CsvClassWriter;
import com.app.univchat.batch.writer.CsvFacilityWriter;
import com.app.univchat.batch.writer.CsvPhoneWriter;
import com.app.univchat.batch.writer.CsvScheduleWriter;
import com.app.univchat.dto.ClassRoomDto;
import com.app.univchat.dto.school.FacilityDto;
import com.app.univchat.dto.school.PhoneDto;
import com.app.univchat.dto.school.ScheduleDto;
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
        private  final CsvScheduleWriter csvScheduleWriter;
        private final CsvFacilityWriter csvFacilityWriter;

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

    /**
     * 학사일정 저장 Job
     */
    @Bean
    public Job csvScheduleJob(){
        return jobBuilderFactory.get("csvScheduleJob")
                .start(csvScheduleReaderStep())
                .build();
    }

    /**
     * 편의시설 저장 Job
     */
    @Bean
    public Job csvFacilityJob() {
        return jobBuilderFactory.get("csvFacilityJob")
                .start(csvFacilityReaderStep())
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

    @Bean
    public Step csvScheduleReaderStep(){
        return stepBuilderFactory.get("csvScheduleReaderStep")
                .<ScheduleDto, ScheduleDto>chunk(chunkSize)
                .reader(csvReader.csvScheduleReader())
                .writer(csvScheduleWriter)
//                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step csvFacilityReaderStep() {
        return stepBuilderFactory.get("csvFacilityReaderStep")
                .<FacilityDto, FacilityDto>chunk(chunkSize)
                .reader(csvReader.csvFacilityReader())
                .writer(csvFacilityWriter)
//                .allowStartIfComplete(true)
                .build();
    }

}
