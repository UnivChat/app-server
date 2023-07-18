package com.app.univchat.batch;

import com.app.univchat.dto.ClassRoomDto;
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
}
