package com.app.univchat.batch;

import com.app.univchat.dto.ClassRoomDto;
import com.app.univchat.dto.school.FacilityDto;
import com.app.univchat.dto.school.PhoneDto;
import com.app.univchat.dto.school.ScheduleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * class.csv 파일 읽기
 */
@Configuration
@RequiredArgsConstructor
public class CsvReader {

    /**
     * 클래스 csv 파일 읽기
     */
    @Bean
    public FlatFileItemReader<ClassRoomDto> csvFileReader(){
        /* 파일 읽기 */
        FlatFileItemReader<ClassRoomDto> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource("/csv/class.csv")); //경로 지정
        flatFileItemReader.setLinesToSkip(1); //첫 번째 줄 읽지 않음(제목 skip)
        flatFileItemReader.setEncoding("UTF-8"); //인코딩 지정

        /* defaultLineMapper: 읽으려는 데이터 LineMapper을 통해 Dto로 매핑 */
        DefaultLineMapper<ClassRoomDto> defaultLineMapper = new DefaultLineMapper<>();

        /* delimitedLineTokenizer : setNames를 통해 각 데이터 이름 설정 */
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer(","); //csv 파일에서 구분자
        delimitedLineTokenizer.setNames("classNumber", "className", "professor", "section", "grade", "classTime", "credit");
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);

        /* beanWrapperFieldSetMapper : Tokenizer로 가져온 데이터 Dto로 바인드 */
        BeanWrapperFieldSetMapper<ClassRoomDto> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(ClassRoomDto.class);

        defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);

        flatFileItemReader.setLineMapper(defaultLineMapper);

        return flatFileItemReader;
    }

    /**
     * 연락망 csv 파일 읽기
     */
    @Bean
    public FlatFileItemReader<PhoneDto> csvPhoneReader(){
        /* 파일 읽기 */
        FlatFileItemReader<PhoneDto> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource("/csv/phone.csv"));
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setEncoding("UTF-8");

        /* defaultLineMapper: 읽으려는 데이터 LineMapper을 통해 Dto로 매핑 */
        DefaultLineMapper<PhoneDto> defaultLineMapper = new DefaultLineMapper<>();

        /* delimitedLineTokenizer : setNames를 통해 각 데이터 이름 설정 */
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer(","); //csv 파일에서 구분자
        delimitedLineTokenizer.setNames("major", "phoneNumber", "email", "location");
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);

        BeanWrapperFieldSetMapper<PhoneDto> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(PhoneDto.class);

        defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);

        flatFileItemReader.setLineMapper(defaultLineMapper);

        return flatFileItemReader;
    }

    /**
     * 학사일정 파일 읽기
     */
    @Bean
    public FlatFileItemReader<ScheduleDto> csvScheduleReader(){
        /* 파일읽기 */
        FlatFileItemReader<ScheduleDto> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource("/csv/schedule.csv")); //읽을 파일 경로 지정
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setEncoding("UTF-8"); //인코딩 설정

        /* defaultLineMapper: 읽으려는 데이터 LineMapper을 통해 Dto로 매핑 */
        DefaultLineMapper<ScheduleDto> defaultLineMapper = new DefaultLineMapper<>();

        /* delimitedLineTokenizer : csv 파일에서 구분자 지정하고 구분한 데이터 setNames를 통해 각 이름 설정 */
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer(","); //csv 파일에서 구분자
        delimitedLineTokenizer.setNames("month", "date", "event"); //행으로 읽은 데이터 매칭할 데이터 각 이름
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer); //lineTokenizer 설정

        /* beanWrapperFieldSetMapper: 매칭할 class 타입 지정 */
        BeanWrapperFieldSetMapper<ScheduleDto> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(ScheduleDto.class);

        defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper); //fieldSetMapper 지정

        flatFileItemReader.setLineMapper(defaultLineMapper); //lineMapper 지정

        return flatFileItemReader;

    }

    @Bean
    public FlatFileItemReader<FacilityDto> csvFacilityReader(){
        /* 파일 읽기 */
        FlatFileItemReader<FacilityDto> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource("/csv/facility.csv"));
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setEncoding("UTF-8");

        /* defaultLineMapper: 읽으려는 데이터 LineMapper을 통해 Dto로 매핑 */
        DefaultLineMapper<FacilityDto> defaultLineMapper = new DefaultLineMapper<>();

        /* delimitedLineTokenizer : csv 파일에서 구분자 지정하고 구분한 데이터 setNames를 통해 각 이름 설정 */
        DelimitedLineTokenizer delimitedLineTokenizer = new customDelimitedLineTokenizer(); //csv 파일에서 구분자
        delimitedLineTokenizer.setDelimiter(",");
        delimitedLineTokenizer.setNames("building", "name", "location", "time1", "time2", "phone"); //행으로 읽은 데이터 매칭할 데이터 각 이름
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer); //lineTokenizer 설정

        /* beanWrapperFieldSetMapper: 매칭할 class 타입 지정 */
        BeanWrapperFieldSetMapper<FacilityDto> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(FacilityDto.class);

        defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper); //fieldSetMapper 지정

        flatFileItemReader.setLineMapper(defaultLineMapper); //lineMapper 지정

        return flatFileItemReader;
    }
}
