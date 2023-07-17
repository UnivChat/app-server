package com.app.univchat.dto;

import com.app.univchat.domain.ClassRoom;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "classRoom Dto")
public class ClassRoomDto {

    @ApiModelProperty(name = "분반", example = "07023-01")
    private String classNumber;

    @ApiModelProperty(name = "수업(교과목명)", example = "기초파이낸스")
    private String className;

    @ApiModelProperty(name = "교수", example = "허성준")
    private String professor; //교수

    @ApiModelProperty(name = "과목번호", example = "전기")
    private String section; //구분

    @ApiModelProperty(name = "학년", example = "1")
    private String grade; //학년

    @ApiModelProperty(name = "시간", example = "수11~13(K260)")
    private String classTime; //시간

    @ApiModelProperty(name = "학점", example = "3")
    private int credit; //학점

    @ApiModelProperty(name = "비고", example = "외국인 학생 수업")
    private String etc; //비고

    public ClassRoom toEntity(){
        //교수 이름 따옴표로 묶여있다면 따옴표 제거
        professor = professor.replace("\"", "");
        //시간에 따옴표로 묶여있다면 따옴표 제거
        classTime = classTime.replace("\"", "");

        return ClassRoom.builder()
                .className(this.className)
                .className(this.className)
                .professor(this.professor)
                .section(this.section)
                .grade(this.grade)
                .classTime(this.classTime)
                .credit(this.credit)
                .build();
    }


}
